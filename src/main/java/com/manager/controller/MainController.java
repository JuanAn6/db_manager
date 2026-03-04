package com.manager.controller;

import com.manager.model.ConnectionSql;
import com.manager.model.TreeNodeData;
import com.manager.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private TreeView<TreeNodeData> menuTree;

    @FXML
    private CheckMenuItem toggleSideMenu;

    private MenuTreeController menuTreeController;

    // private ConnectionController connectionController;

    // keep track of created connections
    private final List<ConnectionSql> savedConnections = new ArrayList<>();
    private final PersistController persistController = new PersistController();

    @FXML
    public void initialize() {
        loadView("home.fxml"); // vista inicial
        menuTreeController = new MenuTreeController(menuTree);
        loadPersistedConnections();
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
            dialog.setWidth(600);
            dialog.setHeight(400);
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
                splitPane.setDividerPositions(0.15);
            }
        } else {
            splitPane.getItems().remove(sideMenu);
        }
    }

    @FXML
    public void addConnection(ConnectionSql connection) {
        savedConnections.add(connection);
        menuTreeController.addConnection(connection);
        System.out.println("Connection added: " + connection);

        try {
            persistController.saveConnection(connection);
        } catch (IOException ex) {
            AlertUtils.showWarning("Connection was created but could not be saved to disk:\n" + ex.getMessage());
        }
    }

    private void loadPersistedConnections() {
        List<ConnectionSql> persistedConnections = persistController.loadConnections();
        for (ConnectionSql connection : persistedConnections) {
            savedConnections.add(connection);
            menuTreeController.addConnection(connection);
        }
    }

}
