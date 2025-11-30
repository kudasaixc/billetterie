package fr.maa.controllers;

import fr.maa.dao.RepresentationDAO;
import fr.maa.models.Representation;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;

public class RepresentationFormController {

    @FXML private TextField fieldIdSpectacle;
    @FXML private TextField fieldDateHeure;
    @FXML private TextField fieldSalle;
    @FXML private TextField fieldPlaces;
    @FXML private TextField fieldPrix;

    private RepresentationDAO dao = new RepresentationDAO();

    @FXML
    public void save() {
        if (!Session.isAdmin()) {
            SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
            return;
        }
        try {
            int spectacleId = Integer.parseInt(fieldIdSpectacle.getText());
            LocalDateTime date = LocalDateTime.parse(fieldDateHeure.getText());
            int places = Integer.parseInt(fieldPlaces.getText());
            double prix = Double.parseDouble(fieldPrix.getText());
            if (places < 0 || prix <= 0) {
                throw new IllegalArgumentException();
            }

            Representation r = new Representation(
                    0,
                    spectacleId,
                    date,
                    fieldSalle.getText(),
                    places,
                    prix
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
        if (!Session.isAdmin()) {
            SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
            return;
        }
        SceneSwitcher.switchTo("views/representation-list.fxml", "Représentations");
    }
}
