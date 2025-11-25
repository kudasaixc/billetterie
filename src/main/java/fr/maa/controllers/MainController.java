package fr.maa.controllers;

import fr.maa.utils.SceneSwitcher;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    public void openClients() {
        SceneSwitcher.switchTo("views/client-list.fxml", "Clients");
    }

    @FXML
    public void openSpectacles() {
        SceneSwitcher.switchTo("views/spectacle-list.fxml", "Spectacles");
    }

    @FXML
    public void openRepresentations() {
        SceneSwitcher.switchTo("views/representation-list.fxml", "Représentations");
    }

    @FXML
    public void openBillets() {
        SceneSwitcher.switchTo("views/billet-form.fxml", "Billets");
    }
}
