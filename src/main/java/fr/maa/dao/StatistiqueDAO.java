package fr.maa.dao;

import fr.maa.models.RemplissageStat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Statistiques de remplissage par spectacle.
 */
public class StatistiqueDAO {

    private static final Logger LOGGER = Logger.getLogger(StatistiqueDAO.class.getName());

    // Requêtes constantes (aucune concaténation à l'exécution). Les agrégats
    // utilisent des sous-requêtes corrélées plutôt que des JOIN directs pour
    // éviter le fan-out qui fausserait la somme des places.
    private static final String SQL_BASE =
            "SELECT s.titre, "
            + "COALESCE((SELECT SUM(r.places_disponibles) FROM representation r WHERE r.id_spectacle = s.id), 0) AS places_restantes, "
            + "COALESCE((SELECT COUNT(b.id) FROM billet b JOIN representation r ON b.id_representation = r.id WHERE r.id_spectacle = s.id), 0) AS billets_vendus "
            + "FROM spectacle s ";

    private static final String SQL_TOUS = SQL_BASE + "ORDER BY s.titre";

    private static final String SQL_PAR_VENDEUR = SQL_BASE + "WHERE s.id_vendeur = ? ORDER BY s.titre";

    private final Connection conn = Database.getConnection();

    // Présence optionnelle de la colonne `spectacle.id_vendeur`, nécessaire
    // pour filtrer les statistiques d'un vendeur sur ses propres spectacles.
    private final boolean hasVendeurColumn = checkColumnExists("spectacle", "id_vendeur");

    private boolean checkColumnExists(String tableName, String columnName) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return false;
    }

    /**
     * Retourne les statistiques de remplissage de tous les spectacles (admin).
     */
    public List<RemplissageStat> getRemplissageStats() {
        return getRemplissageStats(null);
    }

    /**
     * Retourne les statistiques de remplissage par spectacle.
     * <p>
     * Les agrégats utilisent des sous-requêtes corrélées (et non des JOIN
     * directs) pour éviter la multiplication des lignes (fan-out) qui
     * fausserait la somme des places.
     *
     * @param vendeurId si non {@code null}, restreint aux spectacles appartenant
     *                  à ce vendeur (colonne {@code spectacle.id_vendeur}).
     */
    public List<RemplissageStat> getRemplissageStats(Integer vendeurId) {
        boolean filtreVendeur = vendeurId != null;

        // Sécurité fonctionnelle : si l'on demande les stats d'un vendeur mais
        // que la colonne d'appartenance n'existe pas encore (migration non
        // appliquée), on ne révèle aucune donnée plutôt que de tout exposer.
        if (filtreVendeur && !hasVendeurColumn) {
            LOGGER.log(Level.WARNING, "Colonne spectacle.id_vendeur absente : statistiques vendeur indisponibles.");
            return Collections.emptyList();
        }

        List<RemplissageStat> stats = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(filtreVendeur ? SQL_PAR_VENDEUR : SQL_TOUS)) {
            if (filtreVendeur) {
                stmt.setInt(1, vendeurId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.add(new RemplissageStat(
                            rs.getString("titre"),
                            rs.getInt("billets_vendus"),
                            rs.getInt("places_restantes")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return stats;
    }
}
