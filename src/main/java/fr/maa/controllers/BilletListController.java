package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.models.Billet;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedBillet;
import fr.maa.utils.TablePaginationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.function.Predicate;

public class BilletListController {

    @FXML private TableView<Billet> tableBillets;
    @FXML private TableColumn<Billet, Integer> colId;
    @FXML private TableColumn<Billet, String> colNumero;
    @FXML private TableColumn<Billet, String> colIdRep;
    @FXML private TableColumn<Billet, String> colIdClient;

    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private BilletDAO dao = new BilletDAO();
    private ObservableList<Billet> billets;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colIdRep.setCellValueFactory(new PropertyValueFactory<>("representationLabel"));
        colIdClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        billets = FXCollections.observableArrayList(dao.getAll());

        TablePaginationHelper.setup(tableBillets, searchField, pagination, billets, this::filterBillet, 10);
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
        billets.setAll(dao.getAll());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    private Predicate<Billet> filterBillet(String query) {
        if (query == null || query.isBlank()) {
            return billet -> true;
        }

        return billet -> String.valueOf(billet.getId()).contains(query)
                || containsIgnoreCase(billet.getNumero(), query)
                || containsIgnoreCase(billet.getRepresentationLabel(), query)
                || containsIgnoreCase(billet.getClientName(), query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }
}
