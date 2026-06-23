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
    @FXML private Button statistiquesButton;

    @FXML
    public void initialize() {
        if (!Session.isLoggedIn()) {
            SceneSwitcher.switchTo("views/login.fxml", "Connexion");
            return;
        }

        if (welcomeLabel != null) {
            welcomeLabel.setText("Connecté en tant que " + Session.getUser().getEmail());
        }

        // Affichage du menu selon les capacités de l'utilisateur. Le contrôle
        // d'accès réel est dupliqué dans chaque contrôleur de destination
        // (défense en profondeur) : masquer un bouton n'est pas une sécurité.
        setVisible(dashboardButton, Session.isAdmin());
        setVisible(clientsButton, Session.peutGererUsers());
        setVisible(spectaclesButton, Session.peutGererSpectacles());
        setVisible(representationsButton, Session.peutGererSpectacles());
        setVisible(statistiquesButton, Session.peutVoirStatistiques());
    }

    private void setVisible(Button button, boolean visible) {
        if (button == null) {
            return;
        }
        button.setVisible(visible);
        button.setManaged(visible);
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
    public void openStatistiques() {
        SceneSwitcher.switchTo("views/statistiques.fxml", "Statistiques");
    }

    @FXML
    public void logout() {
        Session.logout();
        SceneSwitcher.switchTo("views/login.fxml", "Connexion");
    }
}
