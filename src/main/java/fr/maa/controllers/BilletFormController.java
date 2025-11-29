package fr.maa.controllers;

import fr.maa.dao.BilletDAO;
import fr.maa.models.Billet;
import fr.maa.utils.SceneSwitcher;
import fr.maa.utils.SelectedBillet;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BilletFormController {

    @FXML private TextField fieldNumero;
    @FXML private TextField fieldIdRep;
    @FXML private TextField fieldIdClient;

    private BilletDAO dao = new BilletDAO();
    private Billet editing = null;

    @FXML
    public void initialize() {
        editing = SelectedBillet.get();
        if (editing != null) {
            fieldNumero.setText(editing.getNumero());
            fieldIdRep.setText(String.valueOf(editing.getIdRepresentation()));
            fieldIdClient.setText(String.valueOf(editing.getIdClient()));
        }
    }

    @FXML
    public void save() {
        if (editing == null) {
            Billet b = new Billet(
                    0,
                    fieldNumero.getText(),
                    Integer.parseInt(fieldIdRep.getText()),
                    Integer.parseInt(fieldIdClient.getText())
            );
            dao.insert(b);
        } else {
            editing.setNumero(fieldNumero.getText());
            editing.setIdRepresentation(Integer.parseInt(fieldIdRep.getText()));
            editing.setIdClient(Integer.parseInt(fieldIdClient.getText()));
            dao.update(editing);
        }

        SelectedBillet.clear();
        SceneSwitcher.switchTo("views/billet-list.fxml", "Billets");
    }

    @FXML
    public void cancel() {
        SelectedBillet.clear();
        SceneSwitcher.switchTo("views/billet-list.fxml", "Billets");
    }
}
