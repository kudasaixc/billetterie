package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedSpectacle;
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
    private Spectacle editing = null;

    @FXML
    public void initialize() {
        editing = SelectedSpectacle.get();
        if (editing != null) {
            fieldTitre.setText(editing.getTitre());
            fieldLieu.setText(editing.getLieu());
            fieldAffiche.setText(editing.getAffiche());
            fieldTags.setText(editing.getTags());
            fieldDuree.setText(String.valueOf(editing.getDuree()));
            fieldDescCourte.setText(editing.getDescriptionCourte());
            fieldDescLongue.setText(editing.getDescriptionLongue());
            fieldLangue.setText(editing.getLangue());
            fieldAgeMin.setText(String.valueOf(editing.getAgeMinimum()));
            fieldPhotos.setText(editing.getPhotos());
        }
    }

    @FXML
    public void save() {

        Spectacle s = editing == null ? new Spectacle(
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
        ) : editing;

        if (editing == null) {
            dao.insert(s);
        } else {
            s.setTitre(fieldTitre.getText());
            s.setLieu(fieldLieu.getText());
            s.setAffiche(fieldAffiche.getText());
            s.setTags(fieldTags.getText());
            s.setDuree(Integer.parseInt(fieldDuree.getText()));
            s.setDescriptionCourte(fieldDescCourte.getText());
            s.setDescriptionLongue(fieldDescLongue.getText());
            s.setLangue(fieldLangue.getText());
            s.setAgeMinimum(Integer.parseInt(fieldAgeMin.getText()));
            s.setPhotos(fieldPhotos.getText());
            dao.update(s);
        }

        SelectedSpectacle.clear();
        SceneSwitcher.switchTo("views/spectacle-list.fxml", "Spectacles");
    }

    @FXML
    public void cancel() {
        SelectedSpectacle.clear();
        SceneSwitcher.switchTo("views/spectacle-list.fxml", "Spectacles");
    }
}
