package fr.maa.controllers;

import fr.maa.dao.ClientDAO;
import fr.maa.models.Client;
import fr.maa.utils.TablePaginationHelper;
import fr.maa.utils.SelectedClient;
import fr.maa.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.function.Predicate;

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

    @FXML
    private TextField searchField;

    @FXML
    private Pagination pagination;

    private ClientDAO dao = new ClientDAO();
    private ObservableList<Client> clients;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPseudo.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        clients = FXCollections.observableArrayList(dao.getAll());

        TablePaginationHelper.setup(tableClients, searchField, pagination, clients, this::filterClient, 10);
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
        clients.setAll(dao.getAll());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    private Predicate<Client> filterClient(String query) {
        if (query == null || query.isBlank()) {
            return client -> true;
        }

        return client -> String.valueOf(client.getId()).contains(query)
                || containsIgnoreCase(client.getPseudo(), query)
                || containsIgnoreCase(client.getNom(), query)
                || containsIgnoreCase(client.getPrenom(), query)
                || containsIgnoreCase(client.getEmail(), query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }
}
