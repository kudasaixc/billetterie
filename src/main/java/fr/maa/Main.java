package fr.maa;

import fr.maa.utils.SceneSwitcher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneSwitcher.setStage(stage);
        SceneSwitcher.switchTo("views/main.fxml", "Menu principal");
    }

    public static void main(String[] args) {
        launch();
    }
}
