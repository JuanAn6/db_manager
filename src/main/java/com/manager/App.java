package com.manager;

import com.manager.config.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        
        // initial window size
        stage.setWidth(1300);
        stage.setHeight(800);
        stage.centerOnScreen();

        // stage.setMaximized(true);

        stage.setTitle("DB Manager");
        
        SceneManager.setStage(stage);
        SceneManager.load("main.fxml");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
