package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;

public class RepresentationFormController {

    @FXML private TextField fieldIdSpectacle;
    @FXML private TextField fieldDateHeure;
    @FXML private TextField fieldSalle;
    @FXML private TextField fieldPlaces;

    private RepresentationDAO dao = new RepresentationDAO();

    @FXML
    public void save() {
        try {
            int spectacleId = Integer.parseInt(fieldIdSpectacle.getText());
            LocalDateTime date = LocalDateTime.parse(fieldDateHeure.getText());
            int places = Integer.parseInt(fieldPlaces.getText());

            Representation r = new Representation(
                    0,
                    spectacleId,
                    date,
                    fieldSalle.getText(),
                    places
            );

            dao.insert(r);
            SceneSwitcher.switchTo("views/representation-list.fxml", "Représentations");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Merci de vérifier les champs (format date ISO, nombres valides).");
            alert.showAndWait();
        }
    }

    @FXML
    public void cancel() {
        SceneSwitcher.switchTo("views/representation-list.fxml", "Représentations");
    }
}
