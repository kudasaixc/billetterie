package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SpectacleFormController {

    @FXML private TextField fieldTitre;
    @FXML private TextField fieldLieu;
    @FXML private TextField fieldAffiche;
    @FXML private TextField fieldTags;
    @FXML private TextField fieldDuree;
    @FXML private TextArea fieldDescCourte;
    @FXML private TextArea fieldDescLongue;
    @FXML private TextField fieldLangue;
    @FXML private TextField fieldAgeMin;
    @FXML private TextField fieldPhotos;

    private SpectacleDAO dao = new SpectacleDAO();

    @FXML
    public void save() {

        Spectacle s = new Spectacle(
                0,
                fieldTitre.getText(),
                fieldLieu.getText(),
                fieldAffiche.getText(),
                fieldTags.getText(),
                Integer.parseInt(fieldDuree.getText()),
                fieldDescCourte.getText(),
                fieldDescLongue.getText(),
                fieldLangue.getText(),
                Integer.parseInt(fieldAgeMin.getText()),
                fieldPhotos.getText()
        );

        dao.insert(s);
        SceneSwitcher.switchTo("views/spectacle-list.fxml", "Spectacles");
    }

    @FXML
    public void cancel() {
        SceneSwitcher.switchTo("views/spectacle-list.fxml", "Spectacles");
    }
}
