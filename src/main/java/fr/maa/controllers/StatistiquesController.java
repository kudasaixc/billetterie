package fr.maa.controllers;

import fr.maa.dao.StatistiqueDAO;
import fr.maa.models.RemplissageStat;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Page de statistiques de remplissage : pour chaque spectacle, le nombre total
 * de places, les billets vendus, les places restantes et le taux de remplissage.
 * L'admin voit tous les spectacles ; le vendeur uniquement les siens.
 */
public class StatistiquesController {

    @FXML private Label titleLabel;
    @FXML private TableView<RemplissageStat> tableStats;
    @FXML private TableColumn<RemplissageStat, String> colSpectacle;
    @FXML private TableColumn<RemplissageStat, Integer> colPlacesTotales;
    @FXML private TableColumn<RemplissageStat, Integer> colBilletsVendus;
    @FXML private TableColumn<RemplissageStat, Integer> colPlacesRestantes;
    @FXML private TableColumn<RemplissageStat, String> colTaux;

    private final StatistiqueDAO dao = new StatistiqueDAO();

    @FXML
    public void initialize() {
        // Défense en profondeur : on verrouille l'accès côté contrôleur, pas
        // seulement en masquant le bouton du menu.
        if (!Session.peutVoirStatistiques()) {
            SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
            return;
        }

        colSpectacle.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colPlacesTotales.setCellValueFactory(new PropertyValueFactory<>("placesTotales"));
        colBilletsVendus.setCellValueFactory(new PropertyValueFactory<>("billetsVendus"));
        colPlacesRestantes.setCellValueFactory(new PropertyValueFactory<>("placesRestantes"));
        colTaux.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTauxRemplissagePct()));

        List<RemplissageStat> stats;
        if (Session.peutVoirStatistiquesGlobales()) {
            titleLabel.setText("Statistiques de remplissage — tous les spectacles");
            stats = dao.getRemplissageStats();
        } else {
            titleLabel.setText("Statistiques de remplissage — mes spectacles");
            stats = dao.getRemplissageStats(Session.getUser().getId());
        }

        ObservableList<RemplissageStat> data = FXCollections.observableArrayList(stats);
        tableStats.setItems(data);
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }
}
