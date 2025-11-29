package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.TablePaginationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class RepresentationListController {

    @FXML private TableView<Representation> tableReps;

    @FXML private TableColumn<Representation, Integer> colId;

    @FXML private TableColumn<Representation, String> colSpectacle;

    @FXML private TableColumn<Representation, LocalDateTime> colDate;

    @FXML private TableColumn<Representation, String> colSalle;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    private RepresentationDAO dao = new RepresentationDAO();
    private ObservableList<Representation> representations;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSpectacle.setCellValueFactory(new PropertyValueFactory<>("spectacleTitle"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        representations = FXCollections.observableArrayList(dao.getAll());

        TablePaginationHelper.setup(tableReps, searchField, pagination, representations, this::filterRepresentation, 10);
    }

    @FXML
    public void addRepresentation() {
        SceneSwitcher.switchTo("views/representation-form.fxml", "Nouvelle représentation");
    }

    @FXML
    public void deleteRepresentation() {
        Representation selected = tableReps.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        dao.delete(selected.getId());
        representations.setAll(dao.getAll());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    private Predicate<Representation> filterRepresentation(String query) {
        if (query == null || query.isBlank()) {
            return representation -> true;
        }

        return representation -> String.valueOf(representation.getId()).contains(query)
                || containsIgnoreCase(representation.getSpectacleTitle(), query)
                || containsIgnoreCase(representation.getSalle(), query)
                || representation.getDateHeure().toString().toLowerCase().contains(query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }
}
