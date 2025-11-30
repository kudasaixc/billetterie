package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Billet;
import fr.maa.models.Client;
import fr.maa.models.Representation;
import fr.maa.models.Spectacle;
import fr.maa.services.BilletPDFService;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedClient;
import fr.maa.utils.SelectedRepresentation;
import fr.maa.utils.SelectedSpectacle;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BilletFormController {

    @FXML private TextField clientField;
    @FXML private TextField spectacleField;
    @FXML private TextField representationField;
    @FXML private TextField prixField;
    @FXML private TextField quantiteField;

    private final BilletDAO billetDAO = new BilletDAO();
    private final RepresentationDAO representationDAO = new RepresentationDAO();
    private final BilletPDFService billetPDFService = new BilletPDFService();

    private Client selectedClient;
    private Spectacle selectedSpectacle;
    private Representation selectedRepresentation;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
            return;
        }
        selectedClient = SelectedClient.get();
        selectedSpectacle = SelectedSpectacle.get();
        selectedRepresentation = SelectedRepresentation.get();
        quantiteField.setText("1");
        refreshFields();
    }

    @FXML
    public void openClientSelection() {
        openModal("views/client-select.fxml", "Sélectionner un client", null);
        selectedClient = SelectedClient.get();
        refreshFields();
    }

    @FXML
    public void openSpectacleSelection() {
        openModal("views/spectacle-select.fxml", "Sélectionner un spectacle", null);
        selectedSpectacle = SelectedSpectacle.get();
        selectedRepresentation = null;
        SelectedRepresentation.clear();
        quantiteField.setText("1");
        refreshFields();
    }

    @FXML
    public void openRepresentationSelection() {
        if (selectedSpectacle == null) {
            showAlert(Alert.AlertType.WARNING, "Spectacle manquant", "Sélectionnez un spectacle avant de choisir une représentation.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/representation-select.fxml"));
            Parent root = loader.load();
            RepresentationSelectController controller = loader.getController();
            controller.setSpectacle(selectedSpectacle);
            controller.loadData();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Sélectionner une représentation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la sélection de représentation." + e.getMessage());
        }
        selectedRepresentation = SelectedRepresentation.get();
        refreshFields();
    }

    @FXML
    public void confirmPurchase() {
        if (selectedClient == null) {
            showAlert(Alert.AlertType.ERROR, "Client manquant", "Vous devez sélectionner un client avant d'acheter.");
            return;
        }
        if (selectedRepresentation == null) {
            showAlert(Alert.AlertType.ERROR, "Représentation manquante", "Choisissez une représentation avant de confirmer.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(quantiteField.getText());
            if (quantite <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Merci d'indiquer une quantité entière positive.");
            return;
        }

        double prix = selectedRepresentation.getPrix();
        prixField.setText(String.format("%.2f", prix));

        if (selectedRepresentation.getPlacesDisponibles() < quantite) {
            showAlert(Alert.AlertType.WARNING, "Plus de places", "Il n'y a pas assez de places disponibles pour cette représentation.");
            return;
        }

        boolean insertedAll = true;
        List<Billet> generatedBillets = new ArrayList<>();
        for (int i = 0; i < quantite; i++) {
            String numero = billetDAO.generateNumero();
            Billet billet = new Billet(0, numero, selectedRepresentation.getId(), selectedClient.getId(), prix, LocalDateTime.now());
            if (!billetDAO.insert(billet)) {
                insertedAll = false;
                break;
            }
            billet.setClientName(selectedClient.getPrenom() + " " + selectedClient.getNom());
            billet.setSpectacleTitle(selectedSpectacle.getTitre());
            billet.setRepresentationLabel(formatRepresentationLabel(selectedRepresentation));
            generatedBillets.add(billet);
        }

        boolean decremented = insertedAll && representationDAO.decrementPlaces(selectedRepresentation.getId(), quantite);
        if (decremented) {
            selectedRepresentation.setPlacesDisponibles(selectedRepresentation.getPlacesDisponibles() - quantite);
        }

        if (insertedAll && decremented) {
            billetPDFService.generateBillets(generatedBillets, selectedClient, selectedSpectacle, selectedRepresentation, clientField.getScene() == null ? null : clientField.getScene().getWindow());
            showAlert(Alert.AlertType.INFORMATION, "Billets créés", quantite + " billet(s) ont été générés avec succès.");
            clearSelections();
            SceneSwitcher.switchTo("views/billet-list.fxml", "Billets");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'enregistrement des billets a échoué. Merci de réessayer.");
        }
    }

    @FXML
    public void cancel() {
        clearSelections();
        SceneSwitcher.switchTo("views/billet-list.fxml", "Billets");
    }

    private void refreshFields() {
        clientField.setText(selectedClient == null ? "" : selectedClient.getPrenom() + " " + selectedClient.getNom());
        spectacleField.setText(selectedSpectacle == null ? "" : selectedSpectacle.getTitre());
        representationField.setText(selectedRepresentation == null ? "" : formatRepresentation(selectedRepresentation));
        prixField.setText(selectedRepresentation == null ? "" : String.format("%.2f", selectedRepresentation.getPrix()));
    }

    private String formatRepresentation(Representation representation) {
        return representation.getDateHeure().toString() + " - " + representation.getSalle() +
                " (" + representation.getPlacesDisponibles() + " places, " + String.format("%.2f€", representation.getPrix()) + ")";
    }

    private String formatRepresentationLabel(Representation representation) {
        return representation.getDateHeure().toString() + " - " + representation.getSalle();
    }

    private void openModal(String resource, String title, Runnable afterClose) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(resource));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (afterClose != null) {
                afterClose.run();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de sélection." + e.getMessage());
        }
    }

    private void clearSelections() {
        SelectedClient.clear();
        SelectedSpectacle.clear();
        SelectedRepresentation.clear();
        quantiteField.setText("1");
        prixField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
