package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;

public class RepresentationFormController {

    @FXML private TextField fieldIdSpectacle;
    @FXML private TextField fieldDateHeure;
    @FXML private TextField fieldSalle;

    private RepresentationDAO dao = new RepresentationDAO();

    @FXML
    public void save() {
        Representation r = new Representation(
                0,
                Integer.parseInt(fieldIdSpectacle.getText()),
                LocalDateTime.parse(fieldDateHeure.getText()),
                fieldSalle.getText()
        );

        dao.insert(r);
        SceneSwitcher.switchTo("views/representation-list.fxml", "Représentations");
    }

    @FXML
    public void cancel() {
        SceneSwitcher.switchTo("views/representation-list.fxml", "Représentations");
    }
}
