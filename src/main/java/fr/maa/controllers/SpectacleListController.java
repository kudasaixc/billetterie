package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SpectacleListController {

    @FXML private TableView<Spectacle> tableSpectacles;

    @FXML private TableColumn<Spectacle, Integer> colId;

    @FXML private TableColumn<Spectacle, String> colTitre;

    @FXML private TableColumn<Spectacle, String> colLieu;

    @FXML private TableColumn<Spectacle, String> colLangue;
    private SpectacleDAO dao = new SpectacleDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colLangue.setCellValueFactory(new PropertyValueFactory<>("langue"));
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
