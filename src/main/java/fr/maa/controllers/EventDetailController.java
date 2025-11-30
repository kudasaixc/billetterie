package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.models.Spectacle;
import fr.maa.utils.PurchaseContext;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedSpectacle;
import fr.maa.utils.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventDetailController {

    @FXML
    private ImageView spectacleImage;
    @FXML
    private StackPane imageContainer;
    @FXML
    private Label titleLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label availabilityLabel;
    @FXML
    private ComboBox<Representation> representationCombo;
    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private TextFlow descriptionFlow;
    @FXML
    private Label errorLabel;

    private final RepresentationDAO representationDAO = new RepresentationDAO();
    private Spectacle spectacle;

    @FXML
    public void initialize() {
        if (!Session.isLoggedIn()) {
            SceneSwitcher.switchTo("views/login.fxml", "Connexion");
            return;
        }

        spectacle = SelectedSpectacle.get();
        if (spectacle == null) {
            back();
            return;
        }

        titleLabel.setText(spectacle.getTitre());
        setupImage();
        setupDescription();
        setupRepresentations();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
    }

    private void setupImage() {
        String path = spectacle.getImagePath();
        if (path == null || path.isBlank()) {
            path = spectacle.getAffiche();
        }
        if (path != null && !path.isBlank()) {
            try {
                Image image = new Image(new File(path).toURI().toString(), 260, 340, true, true);
                spectacleImage.setImage(image);
                return;
            } catch (Exception ignored) {
            }
        }
        Text placeholder = new Text("Aucune image disponible");
        placeholder.getStyleClass().add("muted");
        imageContainer.getChildren().add(placeholder);
    }

    private void setupDescription() {
        descriptionFlow.getChildren().clear();
        String desc = spectacle.getDescriptionLongue();
        if (desc == null || desc.isBlank()) {
            desc = spectacle.getDescriptionCourte();
        }
        if (desc == null || desc.isBlank()) {
            desc = "Aucune description disponible";
        }
        descriptionFlow.getChildren().add(new Text(desc));
    }

    private void setupRepresentations() {
        List<Representation> representations = representationDAO.getBySpectacle(spectacle.getId());
        representationCombo.setItems(FXCollections.observableArrayList(representations));
        representationCombo.setCellFactory(list -> new RepresentationCell());
        representationCombo.setButtonCell(new RepresentationCell());
        if (!representations.isEmpty()) {
            representationCombo.getSelectionModel().selectFirst();
            updateDetails(representations.getFirst());
        }
        representationCombo.setOnAction(evt -> updateDetails(representationCombo.getSelectionModel().getSelectedItem()));
    }

    private void updateDetails(Representation representation) {
        if (representation == null) {
            priceLabel.setText("—");
            availabilityLabel.setText("Aucune représentation");
            return;
        }
        priceLabel.setText(String.format("%.2f €", representation.getPrix()));
        availabilityLabel.setText("Places restantes : " + representation.getPlacesDisponibles());
    }

    @FXML
    public void back() {
        SelectedSpectacle.clear();
        SceneSwitcher.switchTo("views/event-catalog.fxml", "Événements");
    }

    @FXML
    public void startPurchase() {
        errorLabel.setText("");
        Representation representation = representationCombo.getSelectionModel().getSelectedItem();
        if (representation == null) {
            errorLabel.setText("Choisissez une représentation");
            return;
        }
        int quantity = quantitySpinner.getValue();
        if (quantity <= 0) {
            errorLabel.setText("Quantité invalide");
            return;
        }
        if (quantity > representation.getPlacesDisponibles()) {
            errorLabel.setText("Pas assez de places disponibles");
            return;
        }
        PurchaseContext.set(spectacle, representation, quantity);
        SceneSwitcher.switchTo("views/payment.fxml", "Paiement");
    }

    private static class RepresentationCell extends javafx.scene.control.ListCell<Representation> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        @Override
        protected void updateItem(Representation item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                return;
            }
            setText(item.getDateHeure().format(formatter) + " – " + item.getSalle() + " – " +
                    String.format("%.2f €", item.getPrix()));
        }
    }
}
