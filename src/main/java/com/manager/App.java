package com.manager;

import com.manager.config.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        SceneManager.load("main.fxml");

        stage.setMaximized(true);

        stage.setTitle("Mi primera app JavaFX");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}