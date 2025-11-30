package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedSpectacle;
import fr.maa.utils.TablePaginationHelper;
import fr.maa.utils.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class SpectacleListController {

    @FXML private TableView<Spectacle> tableSpectacles;

    @FXML private TableColumn<Spectacle, Integer> colId;

    @FXML private TableColumn<Spectacle, String> colTitre;

    @FXML private TableColumn<Spectacle, String> colLieu;

    @FXML private TableColumn<Spectacle, String> colLangue;

    @FXML private TableColumn<Spectacle, String> colImage;

    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    private SpectacleDAO dao = new SpectacleDAO();
    private ObservableList<Spectacle> spectacles;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
            return;
        }
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colLangue.setCellValueFactory(new PropertyValueFactory<>("langue"));
        setupImageColumn();
        spectacles = FXCollections.observableArrayList(dao.getAll());

        TablePaginationHelper.setup(tableSpectacles, searchField, pagination, spectacles, this::filterSpectacle, 10);
    }

    @FXML
    public void addSpectacle() {
        SelectedSpectacle.clear();
        SceneSwitcher.switchTo("views/spectacle-form.fxml", "Nouveau spectacle");
    }

    @FXML
    public void editSpectacle() {
        Spectacle selected = tableSpectacles.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        SelectedSpectacle.set(selected);
        SceneSwitcher.switchTo("views/spectacle-form.fxml", "Modifier un spectacle");
    }

    @FXML
    public void deleteSpectacle() {
        Spectacle selected = tableSpectacles.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        dao.delete(selected.getId());
        spectacles.setAll(dao.getAll());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu");
    }

    private Predicate<Spectacle> filterSpectacle(String query) {
        if (query == null || query.isBlank()) {
            return spectacle -> true;
        }

        return spectacle -> String.valueOf(spectacle.getId()).contains(query)
                || containsIgnoreCase(spectacle.getTitre(), query)
                || containsIgnoreCase(spectacle.getLieu(), query)
                || containsIgnoreCase(spectacle.getLangue(), query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void setupImageColumn() {
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colImage.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Image image = loadImage(path);
                imageView.setImage(image);
                setGraphic(imageView);
            }
        });
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
