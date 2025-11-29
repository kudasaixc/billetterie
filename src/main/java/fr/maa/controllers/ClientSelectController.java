package fr.maa.controllers;

import fr.maa.dao.ClientDAO;
import fr.maa.models.Client;
import fr.maa.utils.SelectedClient;
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

public class ClientSelectController {

    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colPrenom;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private final ClientDAO dao = new ClientDAO();
    private ObservableList<Client> clients;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        clients = FXCollections.observableArrayList(dao.getAll());
        TablePaginationHelper.setup(tableClients, searchField, pagination, clients, this::filterClient, 10);
    }

    @FXML
    public void select() {
        Client selected = tableClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun client sélectionné", "Veuillez choisir un client avant de valider.");
            return;
        }
        SelectedClient.set(selected);
        close();
    }

    @FXML
    public void cancel() {
        close();
    }

    private Predicate<Client> filterClient(String query) {
        if (query == null || query.isBlank()) {
            return client -> true;
        }
        return client -> containsIgnoreCase(client.getNom(), query)
                || containsIgnoreCase(client.getPrenom(), query)
                || containsIgnoreCase(client.getEmail(), query)
                || String.valueOf(client.getId()).contains(query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void close() {
        Stage stage = (Stage) tableClients.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
