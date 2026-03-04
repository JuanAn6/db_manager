package com.manager.controller;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.manager.model.ConnectionSql;
import com.manager.util.AlertUtils;

public class ConnectionDialogController {

    @FXML private VBox selectorPane;
    @FXML private VBox formPane;
    @FXML private Label selectedDbLabel;
    @FXML private Label nameFieldLabel;
    @FXML private Label urlFieldLabel;
    @FXML private TextField nameField;
    @FXML private TextField urlField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;

    // reference to main controller so we can add the newly created connection
    private MainController mainController;
    private String selectedDatabaseType = "MySQL";

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    public void initialize() {
        updateSelectedDatabaseLabel();
    }

    @FXML
    private void handleDatabaseSelection(ActionEvent event) {
        if (!(event.getSource() instanceof Button button)) {
            return;
        }

        Object userData = button.getUserData();
        if (userData instanceof String dbType && !dbType.isBlank()) {
            selectedDatabaseType = dbType;
        }

        updateSelectedDatabaseLabel();
        selectorPane.setVisible(false);
        selectorPane.setManaged(false);
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleChangeDatabase() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        selectorPane.setVisible(true);
        selectorPane.setManaged(true);
    }

    @FXML
    private void handleCreate() {
        String name = nameField.getText();
        String url = urlField.getText();
        String user = userField.getText();
        String password = passwordField.getText();

        if (url == null || url.isEmpty() || user == null || user.isEmpty()) {
            AlertUtils.showWarning("The url and the user are required");
            return;
        }

        ConnectionSql connection = new ConnectionSql(name, url, user, password);
        try {
            connection.connect(url, user, password);
            if (mainController != null) {
                mainController.addConnection(connection);
            }
            // close dialog
            closeDialog();
        } catch (Exception ex) {
            AlertUtils.showError("Cant establish connection with " + selectedDatabaseType + ":\n" + ex.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    @FXML
    private void handleTestConnection() {
        System.out.print("Test connection");
        //TODO: check the connection with the current properties and show a message for the result. Show alert dialog with message.

    }

    private void updateSelectedDatabaseLabel() {
        if (selectedDbLabel != null) selectedDbLabel.setText("Database selected: " + selectedDatabaseType);
        if (nameFieldLabel != null) nameFieldLabel.setText("Connection name: ");
        if (urlFieldLabel != null) urlFieldLabel.setText("URL JDBC (ex. jdbc:mysql://localhost:3306/):");
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
