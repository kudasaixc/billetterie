package fr.maa.controllers;

import fr.maa.dao.ClientDAO;
import fr.maa.models.Client;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedClient;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class ClientFormController {

    @FXML private TextField fieldPseudo;
    @FXML private TextField fieldNom;
    @FXML private TextField fieldPrenom;
    @FXML private TextField fieldNumero;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldPassword;
    @FXML private TextField fieldAdresse;
    @FXML private CheckBox checkAdmin;

    private ClientDAO dao = new ClientDAO();
    private Client editing = null;

    @FXML
    public void initialize() {
        editing = SelectedClient.get();
        if (editing != null) {
            fieldPseudo.setText(editing.getPseudo());
            fieldNom.setText(editing.getNom());
            fieldPrenom.setText(editing.getPrenom());
            fieldNumero.setText(editing.getNumero());
            fieldEmail.setText(editing.getEmail());
            fieldPassword.setText(editing.getPassword());
            fieldAdresse.setText(editing.getAdresse());
            checkAdmin.setSelected(editing.isAdmin());
        }
    }

    @FXML
    public void save() {

        if (editing == null) {
            Client c = new Client(
                0,
                fieldPseudo.getText(),
                fieldNom.getText(),
                fieldPrenom.getText(),
                fieldNumero.getText(),
                fieldEmail.getText(),
                fieldPassword.getText(),
                fieldAdresse.getText(),
                checkAdmin.isSelected()
            );
            dao.insert(c);
        } else {
            editing.setPseudo(fieldPseudo.getText());
            editing.setNom(fieldNom.getText());
            editing.setPrenom(fieldPrenom.getText());
            editing.setNumero(fieldNumero.getText());
            editing.setEmail(fieldEmail.getText());
            editing.setPassword(fieldPassword.getText());
            editing.setAdresse(fieldAdresse.getText());
            editing.setAdmin(checkAdmin.isSelected());

            dao.update(editing);
        }

        SelectedClient.clear();
        SceneSwitcher.switchTo("views/client-list.fxml", "Clients");
    }

    @FXML
    public void cancel() {
        SelectedClient.clear();
        SceneSwitcher.switchTo("views/client-list.fxml", "Clients");
    }
}
