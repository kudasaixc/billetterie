package fr.maa.controllers;

import fr.maa.dao.ClientDAO;
import fr.maa.models.Client;
import fr.maa.utils.SelectedClient;
import fr.maa.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ClientListController {

    @FXML
    private TableView<Client> tableClients;

    private ClientDAO dao = new ClientDAO();

    @FXML
    public void initialize() {
        tableClients.setItems(FXCollections.observableArrayList(dao.getAll()));
    }

    @FXML
    public void addClient() {
        SceneSwitcher.switchTo("views/client-form.fxml", "Ajouter un client");
    }

    @FXML
    public void editClient() {
        Client selected = tableClients.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        SelectedClient.set(selected); 
        SceneSwitcher.switchTo("views/client-form.fxml", "Modifier un client");
    }

    @FXML
    public void deleteClient() {
        Client selected = tableClients.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        dao.delete(selected.getId());
        tableClients.getItems().setAll(dao.getAll());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }
}
