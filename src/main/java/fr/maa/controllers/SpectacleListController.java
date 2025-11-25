package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class SpectacleListController {

    @FXML private TableView<Spectacle> tableSpectacles;
    private SpectacleDAO dao = new SpectacleDAO();

    @FXML
    public void initialize() {
        tableSpectacles.setItems(FXCollections.observableArrayList(dao.getAll()));
    }

    @FXML
    public void addSpectacle() {
        SceneSwitcher.switchTo("views/spectacle-form.fxml", "Nouveau spectacle");
    }

    @FXML
    public void deleteSpectacle() {
        Spectacle selected = tableSpectacles.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        dao.delete(selected.getId());
        tableSpectacles.getItems().setAll(dao.getAll());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu");
    }
}
