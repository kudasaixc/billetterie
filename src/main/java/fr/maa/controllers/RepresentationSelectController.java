package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.models.Spectacle;
import fr.maa.utils.SelectedRepresentation;
import fr.maa.utils.TablePaginationHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class RepresentationSelectController {

    @FXML private TableView<Representation> tableReps;
    @FXML private TableColumn<Representation, Integer> colId;
    @FXML private TableColumn<Representation, String> colDate;
    @FXML private TableColumn<Representation, String> colSalle;
    @FXML private TableColumn<Representation, Integer> colPlaces;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private final RepresentationDAO dao = new RepresentationDAO();
    private ObservableList<Representation> representations = FXCollections.observableArrayList();
    private Spectacle spectacle;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateHeure().format(formatter)));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("placesDisponibles"));
    }

    public void setSpectacle(Spectacle spectacle) {
        this.spectacle = spectacle;
    }

    public void loadData() {
        if (spectacle == null) {
            return;
        }
        representations.setAll(dao.getBySpectacle(spectacle.getId()));
        TablePaginationHelper.setup(tableReps, searchField, pagination, representations, this::filterRepresentation, 10);
    }

    @FXML
    public void select() {
        Representation selected = tableReps.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucune représentation sélectionnée", "Veuillez choisir une représentation avant de valider.");
            return;
        }
        SelectedRepresentation.set(selected);
        close();
    }

    @FXML
    public void cancel() {
        close();
    }

    private Predicate<Representation> filterRepresentation(String query) {
        if (query == null || query.isBlank()) {
            return representation -> true;
        }
        return representation -> representation.getDateHeure().format(formatter).toLowerCase().contains(query)
                || containsIgnoreCase(representation.getSalle(), query)
                || String.valueOf(representation.getId()).contains(query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void close() {
        Stage stage = (Stage) tableReps.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
