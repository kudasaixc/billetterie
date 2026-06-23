package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.models.Billet;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import fr.maa.utils.TablePaginationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class BilletListController {

    @FXML private TableView<Billet> tableBillets;
    @FXML private TableColumn<Billet, String> colNumero;
    @FXML private TableColumn<Billet, String> colSpectacle;
    @FXML private TableColumn<Billet, String> colClient;
    @FXML private TableColumn<Billet, String> colDate;
    @FXML private TableColumn<Billet, Double> colPrix;
    @FXML private Button addButton;

    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private final BilletDAO dao = new BilletDAO();
    private ObservableList<Billet> billets;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        if (!Session.isLoggedIn()) {
            SceneSwitcher.switchTo("views/login.fxml", "Connexion");
            return;
        }

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colSpectacle.setCellValueFactory(new PropertyValueFactory<>("spectacleTitle"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colDate.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                formatDate(cell.getValue().getDateAchat())));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Périmètre des billets affichés selon le rôle :
        //  - admin   : tous les billets
        //  - vendeur : uniquement ceux dont le spectacle lui appartient
        //  - client  : uniquement les siens (et pas de bouton de vente)
        if (Session.isAdmin()) {
            billets = FXCollections.observableArrayList(dao.getAll());
        } else if (Session.isVendeur()) {
            billets = FXCollections.observableArrayList(dao.getAllByVendeur(Session.getUser().getId()));
        } else {
            billets = FXCollections.observableArrayList(dao.getAllByClient(Session.getUser().getId()));
            addButton.setVisible(false);
            addButton.setManaged(false);
        }

        TablePaginationHelper.setup(tableBillets, searchField, pagination, billets, this::filterBillet, 10);
    }

    @FXML
    public void addBillet() {
        SceneSwitcher.switchTo("views/billet-form.fxml", "Nouveau billet");
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    private Predicate<Billet> filterBillet(String query) {
        if (query == null || query.isBlank()) {
            return billet -> true;
        }

        return billet -> containsIgnoreCase(billet.getNumero(), query)
                || containsIgnoreCase(billet.getSpectacleTitle(), query)
                || containsIgnoreCase(billet.getClientName(), query)
                || containsIgnoreCase(formatDate(billet.getDateAchat()), query)
                || String.valueOf(billet.getPrix()).contains(query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(formatter);
    }
}
