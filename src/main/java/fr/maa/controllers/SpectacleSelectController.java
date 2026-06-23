package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SelectedSpectacle;
import fr.maa.utils.Session;
import fr.maa.utils.TablePaginationHelper;
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

import java.util.function.Predicate;

public class SpectacleSelectController {

    @FXML private TableView<Spectacle> tableSpectacles;
    @FXML private TableColumn<Spectacle, Integer> colId;
    @FXML private TableColumn<Spectacle, String> colTitre;
    @FXML private TableColumn<Spectacle, String> colLieu;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private final SpectacleDAO dao = new SpectacleDAO();
    private ObservableList<Spectacle> spectacles;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));

        // Un vendeur ne peut vendre que pour ses propres spectacles ;
        // l'admin peut vendre pour n'importe quel spectacle.
        if (Session.isVendeur()) {
            spectacles = FXCollections.observableArrayList(dao.getByVendeur(Session.getUser().getId()));
        } else {
            spectacles = FXCollections.observableArrayList(dao.getAll());
        }
        TablePaginationHelper.setup(tableSpectacles, searchField, pagination, spectacles, this::filterSpectacle, 10);
    }

    @FXML
    public void select() {
        Spectacle selected = tableSpectacles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun spectacle sélectionné", "Veuillez choisir un spectacle avant de valider.");
            return;
        }
        SelectedSpectacle.set(selected);
        Stage stage = (Stage) tableSpectacles.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel() {
        Stage stage = (Stage) tableSpectacles.getScene().getWindow();
        stage.close();
    }

    private Predicate<Spectacle> filterSpectacle(String query) {
        if (query == null || query.isBlank()) {
            return spectacle -> true;
        }
        return spectacle -> containsIgnoreCase(spectacle.getTitre(), query)
                || containsIgnoreCase(spectacle.getLieu(), query)
                || String.valueOf(spectacle.getId()).contains(query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
