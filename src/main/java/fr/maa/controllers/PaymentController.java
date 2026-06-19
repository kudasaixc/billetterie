package fr.maa.controllers;

import fr.maa.models.Billet;
import fr.maa.models.Representation;
import fr.maa.models.Spectacle;
import fr.maa.services.BilletPDFService;
import fr.maa.services.PurchaseException;
import fr.maa.services.PurchaseService;
import fr.maa.utils.PurchaseContext;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    private final PurchaseService purchaseService = new PurchaseService();
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

        // L'achat (décrément des places + création des billets) est réalisé
        // dans une transaction unique : tout réussit ou rien n'est enregistré.
        List<Billet> createdBillets;
        try {
            createdBillets = purchaseService.purchase(representation, Session.getUser(), quantity);
        } catch (PurchaseException e) {
            errorLabel.setText(e.getMessage());
            confirmButton.setDisable(false);
            return;
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
