package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedSpectacle;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpectacleFormController {

    private static final Logger LOGGER = Logger.getLogger(SpectacleFormController.class.getName());

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
    @FXML private ImageView imagePreview;
    @FXML private Label imagePathLabel;

    private SpectacleDAO dao = new SpectacleDAO();
    private Spectacle editing = null;

    private static final String IMAGES_DIR = "src/main/resources/images";
    private String selectedImagePath = null;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
            return;
        }
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
            selectedImagePath = editing.getImagePath();
        }

        updateImagePreview(selectedImagePath);
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
                fieldPhotos.getText(),
                selectedImagePath
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
            s.setImagePath(selectedImagePath);
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

    @FXML
    public void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(imagePreview.getScene().getWindow());
        if (selectedFile == null) return;

        try {
            Files.createDirectories(Path.of(IMAGES_DIR));
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path destination = Path.of(IMAGES_DIR, fileName);
            Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            selectedImagePath = "images/" + fileName;
            updateImagePreview(selectedImagePath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'import de l'image du spectacle", e);
        }
    }

    private void updateImagePreview(String imagePath) {
        Image image = loadImage(imagePath);
        imagePreview.setImage(image);
        imagePathLabel.setText(imagePath == null || imagePath.isBlank() ? "Aucune image" : imagePath);
    }

    private Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        Path diskPath = Path.of("src/main/resources").resolve(imagePath);
        if (Files.exists(diskPath)) {
            return new Image(diskPath.toUri().toString());
        }

        URL resource = getClass().getResource("/" + imagePath);
        return resource != null ? new Image(resource.toExternalForm()) : null;
    }
}
