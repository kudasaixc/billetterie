package fr.maa.controllers;

import fr.maa.dao.ClientDAO;
import fr.maa.models.Client;
import fr.maa.utils.SelectedClient;
import fr.maa.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientListController {

    @FXML
    private TableView<Client> tableClients;

    @FXML
    private TableColumn<Client, Integer> colId;

    @FXML
    private TableColumn<Client, String> colPseudo;

    @FXML
    private TableColumn<Client, String> colNom;

    @FXML
    private TableColumn<Client, String> colPrenom;

    @FXML
    private TableColumn<Client, String> colEmail;

    private ClientDAO dao = new ClientDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPseudo.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
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
