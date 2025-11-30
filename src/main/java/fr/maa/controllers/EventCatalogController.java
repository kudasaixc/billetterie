package fr.maa.controllers;

import fr.maa.dao.SpectacleDAO;
import fr.maa.models.Spectacle;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedSpectacle;
import fr.maa.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EventCatalogController {

    @FXML
    private ListView<Spectacle> spectacleListView;

    private final SpectacleDAO spectacleDAO = new SpectacleDAO();

    @FXML
    public void initialize() {
        if (!Session.isLoggedIn()) {
            SceneSwitcher.switchTo("views/login.fxml", "Connexion");
            return;
        }

        spectacleListView.getItems().setAll(spectacleDAO.getAll());
        spectacleListView.setCellFactory(list -> new SpectacleCell());
    }

    @FXML
    public void back() {
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    private static class SpectacleCell extends ListCell<Spectacle> {
        @Override
        protected void updateItem(Spectacle spectacle, boolean empty) {
            super.updateItem(spectacle, empty);
            if (empty || spectacle == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            VBox infoBox = new VBox(4);
            Label title = new Label(spectacle.getTitre());
            title.getStyleClass().add("h2");
            Label description = new Label(spectacle.getDescriptionCourte() != null
                    ? spectacle.getDescriptionCourte()
                    : "Aucune description");
            description.setWrapText(true);
            infoBox.getChildren().addAll(title, description);

            Button openButton = new Button("Voir l'évènement");
            openButton.getStyleClass().add("primary-button");
            openButton.setOnAction(evt -> {
                SelectedSpectacle.set(spectacle);
                SceneSwitcher.switchTo("views/event-detail.fxml", spectacle.getTitre());
            });

            HBox row = new HBox(12, infoBox, openButton);
            HBox.setHgrow(infoBox, Priority.ALWAYS);
            setGraphic(row);
        }
    }
}
