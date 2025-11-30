package fr.maa.controllers;

import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private Button dashboardButton;
    @FXML private Button clientsButton;
    @FXML private Button spectaclesButton;
    @FXML private Button representationsButton;

    @FXML
    public void initialize() {
        if (!Session.isLoggedIn()) {
            SceneSwitcher.switchTo("views/login.fxml", "Connexion");
            return;
        }

        if (welcomeLabel != null) {
            welcomeLabel.setText("Connecté en tant que " + Session.getUser().getEmail());
        }

        if (!Session.isAdmin()) {
            dashboardButton.setVisible(false);
            dashboardButton.setManaged(false);
            clientsButton.setVisible(false);
            clientsButton.setManaged(false);
            spectaclesButton.setVisible(false);
            spectaclesButton.setManaged(false);
            representationsButton.setVisible(false);
            representationsButton.setManaged(false);
        }
    }

    @FXML
    public void openClients() {
        SceneSwitcher.switchTo("views/client-list.fxml", "Clients");
    }

    @FXML
    public void openSpectacles() {
        SceneSwitcher.switchTo("views/spectacle-list.fxml", "Spectacles");
    }

    @FXML
    public void openRepresentations() {
        SceneSwitcher.switchTo("views/representation-list.fxml", "Représentations");
    }

    @FXML
    public void openCatalog() {
        SceneSwitcher.switchTo("views/event-catalog.fxml", "Événements");
    }

    @FXML
    public void openBillets() {
        SceneSwitcher.switchTo("views/billet-list.fxml", "Billets");
    }

    @FXML
    public void openDashboard() {
        SceneSwitcher.switchTo("views/dashboard.fxml", "Dashboard");
    }

    @FXML
    public void logout() {
        Session.logout();
        SceneSwitcher.switchTo("views/login.fxml", "Connexion");
    }
}
