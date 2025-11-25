package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.models.Billet;
import fr.maa.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BilletFormController {

    @FXML private TextField fieldNumero;
    @FXML private TextField fieldIdRep;
    @FXML private TextField fieldIdClient;

    private BilletDAO dao = new BilletDAO();

    @FXML
    public void save() {
        Billet b = new Billet(
                0,
                fieldNumero.getText(),
                Integer.parseInt(fieldIdRep.getText()),
                Integer.parseInt(fieldIdClient.getText())
        );

        dao.insert(b);

        SceneSwitcher.switchTo("views/main.fxml", "Menu");
    }

    @FXML
    public void cancel() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu");
    }
}
