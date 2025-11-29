package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Billet;
import fr.maa.models.Client;
import fr.maa.models.Representation;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedClient;
import fr.maa.utils.SelectedRepresentation;
import fr.maa.utils.SelectedSpectacle;
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

public class BilletFormController {

    @FXML private TextField clientField;
    @FXML private TextField spectacleField;
    @FXML private TextField representationField;
    @FXML private TextField prixField;

    private final BilletDAO billetDAO = new BilletDAO();
    private final RepresentationDAO representationDAO = new RepresentationDAO();

    private Client selectedClient;
    private Spectacle selectedSpectacle;
    private Representation selectedRepresentation;

    @FXML
    public void initialize() {
        selectedClient = SelectedClient.get();
        selectedSpectacle = SelectedSpectacle.get();
        selectedRepresentation = SelectedRepresentation.get();
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

        double prix;
        try {
            prix = Double.parseDouble(prixField.getText());
            if (prix <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Prix invalide", "Merci d'indiquer un prix valide (ex: 49.90).");
            return;
        }

        if (selectedRepresentation.getPlacesDisponibles() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Plus de places", "Il n'y a plus de places disponibles pour cette représentation.");
            return;
        }

        String numero = billetDAO.generateNumero();
        Billet billet = new Billet(0, numero, selectedRepresentation.getId(), selectedClient.getId(), prix, LocalDateTime.now());

        boolean inserted = billetDAO.insert(billet);
        boolean decremented = representationDAO.decrementPlaces(selectedRepresentation.getId());

        if (inserted) {
            showAlert(Alert.AlertType.INFORMATION, "Billet créé", "Le billet " + numero + " a été généré avec succès." +
                    (decremented ? " Stock mis à jour." : ""));
            clearSelections();
            SceneSwitcher.switchTo("views/billet-list.fxml", "Billets");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'enregistrement du billet a échoué.");
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
    }

    private String formatRepresentation(Representation representation) {
        return representation.getDateHeure().toString() + " - " + representation.getSalle() +
                " (" + representation.getPlacesDisponibles() + " places)";
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
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
