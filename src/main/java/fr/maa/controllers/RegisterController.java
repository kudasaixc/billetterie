package fr.maa.controllers;

import fr.maa.dao.ClientDAO;
import fr.maa.models.Client;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField pseudoField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private Label errorLabel;

    private final ClientDAO clientDAO = new ClientDAO();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    public void register() {
        if (isBlank(pseudoField) || isBlank(nomField) || isBlank(prenomField) || isBlank(emailField) || passwordField.getText().isBlank()) {
            showError("Tous les champs obligatoires doivent être remplis.");
            return;
        }

        if (clientDAO.emailExists(emailField.getText())) {
            showError("Cet email est déjà utilisé.");
            return;
        }

        Client client = new Client(
                0,
                pseudoField.getText(),
                nomField.getText(),
                prenomField.getText(),
                telephoneField.getText(),
                emailField.getText(),
                passwordField.getText(),
                adresseField.getText(),
                false
        );

        if (clientDAO.register(client)) {
            Client logged = clientDAO.login(emailField.getText(), passwordField.getText());
            Session.setUser(logged);
            SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
        } else {
            showError("Impossible de créer le compte.");
        }
    }

    @FXML
    public void backToLogin() {
        SceneSwitcher.switchTo("views/login.fxml", "Connexion");
    }

    private boolean isBlank(TextField field) {
        return field.getText() == null || field.getText().isBlank();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
