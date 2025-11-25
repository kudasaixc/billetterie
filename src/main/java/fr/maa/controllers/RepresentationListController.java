package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class RepresentationListController {

    @FXML private TableView<Representation> tableReps;
    private RepresentationDAO dao = new RepresentationDAO();

    @FXML
    public void initialize() {
        tableReps.setItems(FXCollections.observableArrayList(dao.getAll()));
    }

    @FXML
    public void addRepresentation() {
        SceneSwitcher.switchTo("views/representation-form.fxml", "Nouvelle représentation");
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }
}
