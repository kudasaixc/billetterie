package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

public class RepresentationListController {

    @FXML private TableView<Representation> tableReps;

    @FXML private TableColumn<Representation, Integer> colId;

    @FXML private TableColumn<Representation, Integer> colSpectacle;

    @FXML private TableColumn<Representation, LocalDateTime> colDate;

    @FXML private TableColumn<Representation, String> colSalle;
    private RepresentationDAO dao = new RepresentationDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSpectacle.setCellValueFactory(new PropertyValueFactory<>("idSpectacle"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        tableReps.setItems(FXCollections.observableArrayList(dao.getAll()));
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
        tableReps.getItems().setAll(dao.getAll());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }
}
