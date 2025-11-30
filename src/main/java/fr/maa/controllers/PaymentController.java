package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Billet;
import fr.maa.models.Representation;
import fr.maa.models.Spectacle;
import fr.maa.services.BilletPDFService;
import fr.maa.utils.PurchaseContext;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentController {

    @FXML
    private Label summaryLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryField;
    @FXML
    private TextField cvvField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button confirmButton;

    private final BilletDAO billetDAO = new BilletDAO();
    private final RepresentationDAO representationDAO = new RepresentationDAO();
    private final BilletPDFService billetPDFService = new BilletPDFService();

    @FXML
    public void initialize() {
        if (!Session.isLoggedIn()) {
            SceneSwitcher.switchTo("views/login.fxml", "Connexion");
            return;
        }

        Spectacle spectacle = PurchaseContext.getSpectacle();
        Representation representation = PurchaseContext.getRepresentation();
        int quantity = PurchaseContext.getQuantity();

        if (spectacle == null || representation == null || quantity <= 0) {
            cancel();
            return;
        }

        summaryLabel.setText(spectacle.getTitre() + " – " + representation.getSpectacleTitle());
        double total = representation.getPrix() * quantity;
        totalLabel.setText(String.format("Total : %.2f € (%d billet%s)", total, quantity, quantity > 1 ? "s" : ""));
    }

    @FXML
    public void confirmPayment() {
        errorLabel.setText("");
        confirmButton.setDisable(true);

        Representation representation = PurchaseContext.getRepresentation();
        Spectacle spectacle = PurchaseContext.getSpectacle();
        int quantity = PurchaseContext.getQuantity();

        if (representation == null || spectacle == null || quantity <= 0) {
            errorLabel.setText("Sélection invalide");
            confirmButton.setDisable(false);
            return;
        }

        if (!representationDAO.decrementPlaces(representation.getId(), quantity)) {
            errorLabel.setText("Plus assez de places disponibles");
            confirmButton.setDisable(false);
            return;
        }

        List<Billet> createdBillets = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            Billet billet = new Billet();
            billet.setNumero(billetDAO.generateNumero());
            billet.setIdRepresentation(representation.getId());
            billet.setIdClient(Session.getUser().getId());
            billet.setPrix(representation.getPrix());
            billet.setDateAchat(LocalDateTime.now());
            if (!billetDAO.insert(billet)) {
                errorLabel.setText("Erreur lors de la création du billet");
                confirmButton.setDisable(false);
                return;
            }
            createdBillets.add(billet);
        }

        billetPDFService.generateBillets(
                createdBillets,
                Session.getUser(),
                spectacle,
                representation,
                confirmButton.getScene() == null ? null : confirmButton.getScene().getWindow()
        );

        PurchaseContext.clear();
        SceneSwitcher.switchTo("views/billet-list.fxml", "Mes billets");
    }

    @FXML
    public void cancel() {
        PurchaseContext.clear();
        SceneSwitcher.switchTo("views/event-catalog.fxml", "Événements");
    }
}
