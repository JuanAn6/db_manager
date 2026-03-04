package com.manager.controller;

import com.manager.model.Database;
import com.manager.model.ConnectionSql;
import com.manager.model.TreeNodeData;
import com.manager.util.AlertUtils;
import javafx.concurrent.Task;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuTreeController {

    //Icons definitions
    private static final Image SERVER_ICON = new Image(Objects.requireNonNull(MenuTreeController.class.getResource("/icons/MdiServerOutline.png"),"Icon not found: /icons/MdiServerOutline.png").toExternalForm());
    private static final Image FOLDER_ICON = new Image(Objects.requireNonNull(MenuTreeController.class.getResource("/icons/MdiFolder.png"),"Icon not found: /icons/MdiFolder.png").toExternalForm());
    private static final Image DATABASE_ICON = new Image(Objects.requireNonNull(MenuTreeController.class.getResource("/icons/MdiDatabase.png"),"Icon not found: /icons/MdiDatabase.png").toExternalForm());
    
    //Properties
    private final TreeView<TreeNodeData> menuTree;
    private final TreeItem<TreeNodeData> root;
    private final TreeItem<TreeNodeData> connectionsCategory;
    private final Map<TreeItem<TreeNodeData>, ConnectionSql> databasesByConnectionItem = new HashMap<>();
    private final Map<TreeItem<TreeNodeData>, Boolean> loadingStateByDatabasesItem = new HashMap<>();
    private final Map<TreeItem<TreeNodeData>, Boolean> loadedStateByDatabasesItem = new HashMap<>();

    public MenuTreeController(TreeView<TreeNodeData> menuTree) {
        this.menuTree = menuTree;
        this.root = new TreeItem<>(new TreeNodeData("Root", null));
        this.connectionsCategory = new TreeItem<>(new TreeNodeData("Connections", null));
        initializeTree();
    }

    public void addConnection(ConnectionSql connection) {
        String connectionName = connection.getName();
        if (connectionName == null || connectionName.isBlank()) {
            connectionName = connection.toString();
        }

        TreeItem<TreeNodeData> connectionItem = new TreeItem<>(new TreeNodeData(connectionName, SERVER_ICON));
        TreeItem<TreeNodeData> databasesItem = new TreeItem<>(new TreeNodeData("Databases", FOLDER_ICON));
        databasesByConnectionItem.put(databasesItem, connection);
        loadingStateByDatabasesItem.put(databasesItem, false);
        loadedStateByDatabasesItem.put(databasesItem, false);

        databasesItem.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (Boolean.TRUE.equals(newVal)) {
                loadDatabasesForItem(databasesItem);
            }
        });

        connectionItem.setExpanded(true);
        connectionItem.getChildren().addAll(java.util.List.of(
            databasesItem,
            new TreeItem<>(new TreeNodeData("Users", FOLDER_ICON)),
            new TreeItem<>(new TreeNodeData("Administer", FOLDER_ICON)),
            new TreeItem<>(new TreeNodeData("System info", FOLDER_ICON))
        ));

        connectionsCategory.getChildren().add(connectionItem);
    }

    private void initializeTree() {
        root.setExpanded(true);
        connectionsCategory.setExpanded(true);
        root.getChildren().add(connectionsCategory);

        menuTree.setRoot(root);
        menuTree.setShowRoot(false);
        menuTree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(TreeNodeData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(item.getName());
                if (item.getIcon() == null) {
                    setGraphic(null);
                    return;
                }

                ImageView iconView = new ImageView(item.getIcon());
                iconView.setFitHeight(16);
                iconView.setFitWidth(16);
                iconView.setPreserveRatio(true);
                setGraphic(iconView);
            }
        });

        // Click event
        menuTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && "Databases".equals(newVal.getValue().getName())) {
                loadDatabasesForItem(newVal);
            }

            if (newVal != null && newVal.isLeaf()) {
                System.out.println("Selected: " + newVal.getValue().getName());
            } else if (newVal != null && !newVal.isLeaf()) {
                System.out.println("Selected not last item: " + newVal.getValue().getName());
            }
        });
    }

    private void loadDatabasesForItem(TreeItem<TreeNodeData> databasesItem) {
        if (!databasesByConnectionItem.containsKey(databasesItem)) {
            return;
        }
        if (Boolean.TRUE.equals(loadedStateByDatabasesItem.get(databasesItem))) {
            return;
        }
        if (Boolean.TRUE.equals(loadingStateByDatabasesItem.get(databasesItem))) {
            return;
        }

        ConnectionSql connection = databasesByConnectionItem.get(databasesItem);
        loadingStateByDatabasesItem.put(databasesItem, true);
        databasesItem.getChildren().setAll(new TreeItem<>(new TreeNodeData("Loading...", null)));
        databasesItem.setExpanded(true);

        Task<List<Database>> loadDatabasesTask = new Task<>() {
            @Override
            protected List<Database> call() throws Exception {
                return connection.getDatabases();
            }
        };

        loadDatabasesTask.setOnSucceeded(event -> {
            List<Database> databases = loadDatabasesTask.getValue();
            databasesItem.getChildren().clear();

            if (databases == null || databases.isEmpty()) {
                databasesItem.getChildren().add(new TreeItem<>(new TreeNodeData("No databases found", null)));
            } else {
                for (Database database : databases) {
                    databasesItem.getChildren().add(
                        new TreeItem<>(new TreeNodeData(database.getName(), DATABASE_ICON))
                    );
                }
            }

            loadedStateByDatabasesItem.put(databasesItem, true);
            loadingStateByDatabasesItem.put(databasesItem, false);
        });

        loadDatabasesTask.setOnFailed(event -> {
            Throwable error = loadDatabasesTask.getException();
            databasesItem.getChildren().setAll(new TreeItem<>(new TreeNodeData("Error loading databases", null)));
            loadedStateByDatabasesItem.put(databasesItem, false);
            loadingStateByDatabasesItem.put(databasesItem, false);
            String message = "Error loading databases: " + (error != null ? error.getMessage() : "Unknown error");
            AlertUtils.showError(message);
        });

        Thread loaderThread = new Thread(loadDatabasesTask, "db-list-loader");
        loaderThread.setDaemon(true);
        loaderThread.start();
    }
}
