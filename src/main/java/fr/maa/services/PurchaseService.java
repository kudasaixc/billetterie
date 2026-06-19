package fr.maa.services;

import fr.maa.dao.BilletDAO;
import fr.maa.dao.Database;
import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Billet;
import fr.maa.models.Client;
import fr.maa.models.Representation;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Orchestre l'achat de billets au sein d'une <b>transaction unique</b>.
 * <p>
 * Le décrément des places et la création de l'ensemble des billets sont
 * réalisés sur la même connexion, autocommit désactivé. Soit toutes les
 * opérations réussissent et la transaction est validée ({@code commit}),
 * soit la moindre erreur survient et l'intégralité est annulée
 * ({@code rollback}). Cela garantit qu'un achat est <em>entièrement validé
 * ou entièrement annulé</em>, évitant toute incohérence (places décrémentées
 * sans billet, ou billets créés sans décrément).
 */
public class PurchaseService {

    private static final Logger LOGGER = Logger.getLogger(PurchaseService.class.getName());

    private final RepresentationDAO representationDAO = new RepresentationDAO();
    private final BilletDAO billetDAO = new BilletDAO();

    /**
     * Réalise l'achat de {@code quantity} billets pour une représentation.
     *
     * @return la liste des billets créés (avec leur numéro généré)
     * @throws PurchaseException si la sélection est invalide, si les places
     *                           sont insuffisantes ou si une erreur technique
     *                           survient (dans ce cas, aucune donnée n'est
     *                           persistée).
     */
    public List<Billet> purchase(Representation representation, Client client, int quantity) throws PurchaseException {
        if (representation == null || client == null || quantity <= 0) {
            throw new PurchaseException("Sélection invalide");
        }

        Connection conn = Database.getConnection();
        if (conn == null) {
            throw new PurchaseException("Connexion à la base de données indisponible.");
        }

        boolean previousAutoCommit = true;
        try {
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // 1) Décrément atomique et conditionnel : l'UPDATE échoue (0 ligne)
            //    s'il ne reste pas assez de places, ce qui évite la survente.
            if (!representationDAO.decrementPlaces(conn, representation.getId(), quantity)) {
                conn.rollback();
                throw new PurchaseException("Plus assez de places disponibles");
            }

            // 2) Création des billets dans la MÊME transaction.
            List<Billet> billets = new ArrayList<>();
            for (int i = 0; i < quantity; i++) {
                Billet billet = new Billet();
                billet.setNumero(billetDAO.generateNumero(conn));
                billet.setIdRepresentation(representation.getId());
                billet.setIdClient(client.getId());
                billet.setPrix(representation.getPrix());
                billet.setDateAchat(LocalDateTime.now());

                if (!billetDAO.insert(conn, billet)) {
                    conn.rollback();
                    throw new PurchaseException("Erreur lors de la création du billet");
                }
                billets.add(billet);
            }

            // 3) Toutes les opérations ont réussi : on valide la transaction.
            conn.commit();
            return billets;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            LOGGER.log(Level.SEVERE, "Echec de la transaction d'achat, rollback effectué", e);
            throw new PurchaseException("Erreur lors de l'achat. Aucune opération n'a été enregistrée.", e);
        } finally {
            restoreAutoCommit(conn, previousAutoCommit);
        }
    }

    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Echec du rollback de la transaction d'achat", e);
        }
    }

    private void restoreAutoCommit(Connection conn, boolean previousAutoCommit) {
        try {
            conn.setAutoCommit(previousAutoCommit);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Impossible de restaurer l'autocommit après l'achat", e);
        }
    }
}
