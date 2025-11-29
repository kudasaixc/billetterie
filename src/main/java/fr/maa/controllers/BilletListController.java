package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.models.Billet;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedBillet;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BilletListController {

    @FXML private TableView<Billet> tableBillets;
    @FXML private TableColumn<Billet, Integer> colId;
    @FXML private TableColumn<Billet, String> colNumero;
    @FXML private TableColumn<Billet, Integer> colIdRep;
    @FXML private TableColumn<Billet, Integer> colIdClient;

    private BilletDAO dao = new BilletDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colIdRep.setCellValueFactory(new PropertyValueFactory<>("idRepresentation"));
        colIdClient.setCellValueFactory(new PropertyValueFactory<>("idClient"));

        refreshTable();
    }

    @FXML
    public void addBillet() {
        SelectedBillet.clear();
        SceneSwitcher.switchTo("views/billet-form.fxml", "Nouveau billet");
    }

    @FXML
    public void editBillet() {
        Billet selected = tableBillets.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        SelectedBillet.set(selected);
        SceneSwitcher.switchTo("views/billet-form.fxml", "Modifier un billet");
    }

    @FXML
    public void deleteBillet() {
        Billet selected = tableBillets.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        dao.delete(selected.getId());
        refreshTable();
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    private void refreshTable() {
        tableBillets.setItems(FXCollections.observableArrayList(dao.getAll()));
    }
}
