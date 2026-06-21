package fr.maa.controllers;

import fr.maa.dao.ClientDAO;
import fr.maa.models.Client;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final ClientDAO clientDAO = new ClientDAO();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        Session.logout();
    }

    @FXML
    public void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            showError("Email et mot de passe sont requis.");
            return;
        }

        Client client;
        try {
            client = clientDAO.login(email, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Echec technique lors de la connexion", e);
            showError("Impossible de contacter la base de données. "
                    + "Vérifiez qu'elle est démarrée et correctement configurée.");
            return;
        }

        if (client == null) {
            showError("Identifiants invalides.");
            return;
        }

        Session.setUser(client);
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    @FXML
    public void openRegister() {
        SceneSwitcher.switchTo("views/register.fxml", "Inscription");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
