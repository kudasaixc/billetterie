package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.dao.RepresentationDAO;
import fr.maa.dao.SpectacleDAO;
import fr.maa.models.SpectacleStat;
import fr.maa.models.VenteParJour;
import fr.maa.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML
    private Label totalSpectaclesLabel;
    @FXML
    private Label totalRepresentationsLabel;
    @FXML
    private Label totalBilletsLabel;
    @FXML
    private Label chiffreAffairesLabel;
    @FXML
    private PieChart ventesPieChart;
    @FXML
    private BarChart<String, Number> caBarChart;
    @FXML
    private LineChart<String, Number> ventesLineChart;
    @FXML
    private AreaChart<String, Number> remplissageAreaChart;

    private final SpectacleDAO spectacleDAO = new SpectacleDAO();
    private final RepresentationDAO representationDAO = new RepresentationDAO();
    private final BilletDAO billetDAO = new BilletDAO();

    @FXML
    public void initialize() {
        loadKpis();
        loadTopSpectaclesCharts();
        loadVentesLineChart();
        loadRemplissageChart();
    }

    private void loadKpis() {
        totalSpectaclesLabel.setText(String.valueOf(spectacleDAO.getTotalSpectacles()));
        totalRepresentationsLabel.setText(String.valueOf(representationDAO.getTotalRepresentations()));
        totalBilletsLabel.setText(String.valueOf(billetDAO.getTotalBilletsVendus()));
        chiffreAffairesLabel.setText(formatCurrency(billetDAO.getChiffreAffaires()));
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    private void loadTopSpectaclesCharts() {
        List<SpectacleStat> top = spectacleDAO.getTopSpectacles();
        ventesPieChart.getData().clear();
        caBarChart.getData().clear();

        for (SpectacleStat stat : top) {
            ventesPieChart.getData().add(new PieChart.Data(stat.getTitre(), stat.getBilletsVendus()));
        }

        XYChart.Series<String, Number> caSeries = new XYChart.Series<>();
        caSeries.setName("Chiffre d'affaires");
        for (SpectacleStat stat : top) {
            caSeries.getData().add(new XYChart.Data<>(stat.getTitre(), stat.getChiffreAffaires()));
        }
        caBarChart.getData().add(caSeries);
    }

    private void loadVentesLineChart() {
        List<VenteParJour> ventes = billetDAO.getVentesParJour();
        ventesLineChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Billets vendus");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (VenteParJour vente : ventes) {
            series.getData().add(new XYChart.Data<>(vente.getDate().format(formatter), vente.getBilletsVendus()));
        }
        ventesLineChart.getData().add(series);
    }

    private void loadRemplissageChart() {
        remplissageAreaChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Taux de remplissage");

        representationDAO.getAll().forEach(representation -> {
            double taux = representationDAO.getTauxRemplissage(representation.getId());
            String label = representation.getSpectacleTitle() != null
                    ? representation.getSpectacleTitle()
                    : "Représentation " + representation.getId();
            series.getData().add(new XYChart.Data<>(label, taux * 100));
        });

        remplissageAreaChart.getData().add(series);
    }

    private String formatCurrency(double value) {
        return String.format("%.2f €", value);
    }
}
