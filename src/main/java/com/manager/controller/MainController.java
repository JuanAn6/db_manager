package com.manager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private StackPane contentPane;
    
    @FXML
    private ListView<String> menuList;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox sideMenu;

    @FXML
    private TreeView<String> menuTree;

    @FXML
    private CheckMenuItem toggleSideMenu;


    private ConnectionController connectionController;

    // keep track of created connections
    private final List<com.manager.model.ConnectionSql> savedConnections = new ArrayList<>();

    @FXML
    public void initialize() {
        loadView("home.fxml"); // vista inicial
        loadTreeMenu();

    }

    @FXML
    private void goToRegister() {
        loadView("register.fxml");
    }

    @FXML
    private void openNewConnection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/manager/connection.fxml"));
            Parent root = loader.load();
            com.manager.controller.ConnectionDialogController controller = loader.getController();
            controller.setMainController(this);

            Stage dialog = new Stage();
            // dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("New Connection");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToHome() {
        loadView("home.fxml");
    }

    private void loadView(String fxml) {
        try {
            Node view = FXMLLoader.load(
                getClass().getResource("/com/manager/" + fxml)
            );
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleSideMenu() {

        if (toggleSideMenu.isSelected()) {
            if (!splitPane.getItems().contains(sideMenu)) {
                splitPane.getItems().add(0, sideMenu);
                splitPane.setDividerPositions(0.1);
            }
        } else {
            splitPane.getItems().remove(sideMenu);
        }
    }

    @FXML
    public void addConnection(com.manager.model.ConnectionSql connection) {
        savedConnections.add(connection);
        System.out.println("Conexión guardada: " + connection);
    }

    private void loadTreeMenu(){
        // Root node (Hiden if we set it)
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);

        TreeItem<String> category1 = new TreeItem<>("Manage");
        category1.getChildren().addAll(java.util.Arrays.asList(
            new TreeItem<>("Users"),
            new TreeItem<>("Products"),
            new TreeItem<>("Orders")
        ));

        TreeItem<String> category2 = new TreeItem<>("Settings");
        category2.getChildren().addAll(java.util.Arrays.asList(
            new TreeItem<>("General"),
            new TreeItem<>("Security"),
            new TreeItem<>("Notifications")
        ));

        // Add categories to root node
        root.getChildren().addAll(java.util.Arrays.asList(category1, category2));

        // Set the TreeView
        menuTree.setRoot(root);

        // Hide root item
        // menuTree.setShowRoot(false);

        //Click event
        menuTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                System.out.println("Selected: " + newVal.getValue());
            }
        });

    }


}