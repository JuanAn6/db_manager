package com.manager.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class AlertUtils {

    private AlertUtils() {
    }

    public static void showError(String message) {
        show(Alert.AlertType.ERROR, "Error", message);
    }

    public static void showWarning(String message) {
        show(Alert.AlertType.WARNING, "Warning", message);
    }

    public static void showInfo(String message) {
        show(Alert.AlertType.INFORMATION, "Information", message);
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Runnable showAlertTask = () -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message == null ? "" : message);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showAlertTask.run();
            return;
        }

        Platform.runLater(showAlertTask);
    }
}
