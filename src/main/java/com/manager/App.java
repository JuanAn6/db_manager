package com.manager;

import com.manager.config.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        SceneManager.load("main.fxml");

        // initial window size
        stage.setWidth(1650);
        stage.setHeight(1080);
        stage.centerOnScreen();

        // stage.setMaximized(true);

        stage.setTitle("DB Manager");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
