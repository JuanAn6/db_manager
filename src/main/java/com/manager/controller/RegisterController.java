package com.manager.controller;

import com.manager.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleRegister() {

        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            AlertUtils.showError("Campos vacíos");
            return;
        }

        // Aquí iría la lógica real
        AlertUtils.showInfo("Usuario registrado correctamente");
    }
}
