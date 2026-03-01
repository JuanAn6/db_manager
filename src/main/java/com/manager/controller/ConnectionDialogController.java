package com.manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import com.manager.model.ConnectionSql;

public class ConnectionDialogController {

    @FXML private TextField aliasField;
    @FXML private TextField urlField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;

    // reference to main controller so we can add the newly created connection
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleCreate() {
        String alias = aliasField.getText();
        String url = urlField.getText();
        String user = userField.getText();
        String password = passwordField.getText();

        if (url == null || url.isEmpty() || user == null || user.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos incompletos");
            alert.setHeaderText(null);
            alert.setContentText("La URL y el usuario son obligatorios.");
            alert.showAndWait();
            return;
        }

        ConnectionSql connection = new ConnectionSql(alias, url, user, password);
        try {
            connection.connect(url, user, password);
            if (mainController != null) {
                mainController.addConnection(connection);
            }
            // close dialog
            Stage stage = (Stage) urlField.getScene().getWindow();
            stage.close();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de conexión");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo establecer la conexión:\n" + ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) urlField.getScene().getWindow();
        stage.close();
    }
}
