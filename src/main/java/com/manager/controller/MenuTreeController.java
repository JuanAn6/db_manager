package com.manager.controller;

import com.manager.model.Database;
import com.manager.model.ConnectionSql;
import com.manager.model.TreeNodeData;
import com.manager.model.TreeNodeType;
import com.manager.util.AlertUtils;
import javafx.concurrent.Task;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class MenuTreeController {

    //Icons definitions
    private static final Image SERVER_ICON = new Image(Objects.requireNonNull(MenuTreeController.class.getResource("/icons/MdiServerOutline.png"),"Icon not found: /icons/MdiServerOutline.png").toExternalForm());
    private static final Image FOLDER_ICON = new Image(Objects.requireNonNull(MenuTreeController.class.getResource("/icons/MdiFolder.png"),"Icon not found: /icons/MdiFolder.png").toExternalForm());
    private static final Image DATABASE_ICON = new Image(Objects.requireNonNull(MenuTreeController.class.getResource("/icons/MdiDatabase.png"),"Icon not found: /icons/MdiDatabase.png").toExternalForm());
    private static final String LOADING_LABEL = "Loading...";
    private static final String CONNECT_PLACEHOLDER_LABEL = "Expand and select to connect...";
    private static final String DATABASES_PLACEHOLDER_LABEL = "Expand to load databases...";
    
    //Properties
    private final TreeView<TreeNodeData> menuTree;
    private final TreeItem<TreeNodeData> root;
    private final TreeItem<TreeNodeData> connectionsCategory;
    private final Map<TreeItem<TreeNodeData>, ConnectionSql> treeItemsConnections = new HashMap<>();
    private final Map<TreeItem<TreeNodeData>, ConnectionSql> connectionByDatabasesFolderItem = new HashMap<>();
    private final Map<TreeItem<TreeNodeData>, Boolean> loadingStateByDatabasesFolderItem = new HashMap<>();
    private final Map<TreeItem<TreeNodeData>, Boolean> loadedStateByDatabasesFolderItem = new HashMap<>();
    private final Map<TreeNodeType, Consumer<TreeItem<TreeNodeData>>> selectionHandlers = new HashMap<>();

    public MenuTreeController(TreeView<TreeNodeData> menuTree) {
        this.menuTree = menuTree;
        this.root = new TreeItem<>(new TreeNodeData("Root", null, TreeNodeType.ROOT));
        this.connectionsCategory = new TreeItem<>(new TreeNodeData("Connections", null, TreeNodeType.CONNECTIONS_CATEGORY));
        initializeSelectionHandlers();
        initializeTree();
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
            handleSelection(newVal);
        });

        // Expand Event
        menuTree.addEventHandler(TreeItem.branchExpandedEvent(), event -> {
            TreeItem<Object> item = event.getTreeItem();
            System.out.println("Expanded: " + item.getValue().toString());
        });

        //Collapse Event
        menuTree.addEventHandler(TreeItem.branchCollapsedEvent(), event -> {
            TreeItem<Object> item = event.getTreeItem();
            System.out.println("Collapsed: " + item.getValue().toString());
        });

    }

    private void initializeSelectionHandlers() {
        selectionHandlers.put(TreeNodeType.CONNECTION, this::handleConnectionSelection);
        selectionHandlers.put(TreeNodeType.FOLDER_DATABASES, this::handleDatabasesFolderSelection);
    }

    private void handleSelection(TreeItem<TreeNodeData> selectedItem) {
        if (selectedItem == null || selectedItem.getValue() == null) {
            return;
        }

        TreeNodeType type = selectedItem.getValue().getType();
        Consumer<TreeItem<TreeNodeData>> handler = selectionHandlers.get(type);
        if (handler != null) {
            handler.accept(selectedItem);
            return;
        }

        System.out.println("Selected: " + selectedItem.getValue().getName() + " (" + type + ")");
    }

    private void handleConnectionSelection(TreeItem<TreeNodeData> connectionItem) {
        ensureConnectionAndLoadFolders(connectionItem);
    }

    private void handleDatabasesFolderSelection(TreeItem<TreeNodeData> databasesFolder) {
        loadDatabasesFolder(databasesFolder);
    }

    /**
     * Adds connection and the event to connect
     * @param connection
     */
    public void addConnection(ConnectionSql connection) {
        String connectionName = connection.getName();
        if (connectionName == null || connectionName.isBlank()) {
            connectionName = connection.toString();
        }

        //TODO: this has to be inside the connectionSql object?
        TreeItem<TreeNodeData> conTreeItem = new TreeItem<>(new TreeNodeData(connectionName, SERVER_ICON, TreeNodeType.CONNECTION));
        treeItemsConnections.put(conTreeItem, connection);
        conTreeItem.getChildren().setAll(List.of(new TreeItem<>(new TreeNodeData(CONNECT_PLACEHOLDER_LABEL, null, TreeNodeType.PLACEHOLDER))));
        
        //Expand connection
        conTreeItem.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (Boolean.TRUE.equals(newVal)) {
                ensureConnectionAndLoadFolders(conTreeItem);
            }
        });

        connectionsCategory.getChildren().add(conTreeItem);
    }

    private void ensureConnectionAndLoadFolders(TreeItem<TreeNodeData> connectionTreeItem) {
        if (!treeItemsConnections.containsKey(connectionTreeItem)) {
            return;
        }

        ConnectionSql conn = treeItemsConnections.get(connectionTreeItem);
        try {
            if (!conn.getIsFoldersTreeLoaded()) {
                setLoadingChild(connectionTreeItem);
            }
            if (!conn.checkConnection()) {
                System.out.println("Attempting database connection for: " + conn.getName());
                conn.ensureConnected();
            }
            loadConnectionFolders(connectionTreeItem);
        } catch (SQLException ex) {
            AlertUtils.showError("Cant establish connection with " + conn.getName() + ":\n" + ex.getMessage());
        }
    }

    private void loadConnectionFolders(TreeItem<TreeNodeData> connectionTreeItem) {
        if (!treeItemsConnections.containsKey(connectionTreeItem)) {
            return;
        }
        
        ConnectionSql conn = treeItemsConnections.get(connectionTreeItem);
        
        if (conn.getIsFoldersTreeLoaded()) {
            return;
        }
        
        connectionTreeItem.getChildren().setAll(List.of(
            buildDatabasesFolder(conn),
            new TreeItem<>(new TreeNodeData("Users", FOLDER_ICON, TreeNodeType.FOLDER_USERS)),
            new TreeItem<>(new TreeNodeData("Administer", FOLDER_ICON, TreeNodeType.FOLDER_ADMINISTER)),
            new TreeItem<>(new TreeNodeData("System info", FOLDER_ICON, TreeNodeType.FOLDER_SYSTEM_INFO))
        ));

        conn.setIsFoldersTreeLoaded(true);
        
    }

    private void loadDatabasesFolder(TreeItem<TreeNodeData> databasesFolder) {
        if (!connectionByDatabasesFolderItem.containsKey(databasesFolder)) {
            return;
        }
        if (Boolean.TRUE.equals(loadedStateByDatabasesFolderItem.get(databasesFolder))) {
            return;
        }
        if (Boolean.TRUE.equals(loadingStateByDatabasesFolderItem.get(databasesFolder))) {
            return;
        }

        ConnectionSql connection = connectionByDatabasesFolderItem.get(databasesFolder);
        loadingStateByDatabasesFolderItem.put(databasesFolder, true);
        setLoadingChild(databasesFolder);

        Task<List<Database>> loadDatabasesTask = new Task<>() {
            @Override
            protected List<Database> call() throws Exception {
                return connection.getDatabases();
            }
        };

        loadDatabasesTask.setOnSucceeded(event -> {
            List<Database> databases = loadDatabasesTask.getValue();
            databasesFolder.getChildren().setAll(buildDatabaseItems(databases));
            loadedStateByDatabasesFolderItem.put(databasesFolder, true);
            loadingStateByDatabasesFolderItem.put(databasesFolder, false);
            System.out.println("Loading databases... finish");
        });

        loadDatabasesTask.setOnFailed(event -> {
            Throwable error = loadDatabasesTask.getException();
            databasesFolder.getChildren().setAll(List.of(new TreeItem<>(new TreeNodeData("Error loading databases", null, TreeNodeType.ERROR))));
            loadedStateByDatabasesFolderItem.put(databasesFolder, false);
            loadingStateByDatabasesFolderItem.put(databasesFolder, false);
            String message = "Error loading databases: " + (error != null ? error.getMessage() : "Unknown error");
            AlertUtils.showError(message);
        });

        Thread loaderThread = new Thread(loadDatabasesTask, "db-list-loader");
        loaderThread.setDaemon(true);
        loaderThread.start();
    }

    private TreeItem<TreeNodeData> buildDatabasesFolder(ConnectionSql connection) {
        TreeItem<TreeNodeData> databasesFolder = new TreeItem<>(new TreeNodeData("Databases", FOLDER_ICON, TreeNodeType.FOLDER_DATABASES));
        databasesFolder.getChildren().setAll(List.of(new TreeItem<>(new TreeNodeData(DATABASES_PLACEHOLDER_LABEL, null, TreeNodeType.PLACEHOLDER))));
        connectionByDatabasesFolderItem.put(databasesFolder, connection);
        loadingStateByDatabasesFolderItem.put(databasesFolder, false);
        loadedStateByDatabasesFolderItem.put(databasesFolder, false);
        databasesFolder.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (Boolean.TRUE.equals(newVal)) {
                loadDatabasesFolder(databasesFolder);
            }
        });
        return databasesFolder;
    }

    private List<TreeItem<TreeNodeData>> buildDatabaseItems(List<Database> databases) {
        if (databases == null || databases.isEmpty()) {
            return List.of(new TreeItem<>(new TreeNodeData("No databases found", null, TreeNodeType.PLACEHOLDER)));
        }

        List<TreeItem<TreeNodeData>> databaseItems = new java.util.ArrayList<>();
        for (Database database : databases) {
            databaseItems.add(new TreeItem<>(new TreeNodeData(database.getName(), DATABASE_ICON, TreeNodeType.DATABASE)));
        }
        return databaseItems;
    }

    private void setLoadingChild(TreeItem<TreeNodeData> parentItem) {
        if (parentItem == null) {
            return;
        }
        parentItem.getChildren().setAll(
            List.of(new TreeItem<>(new TreeNodeData(LOADING_LABEL, null, TreeNodeType.PLACEHOLDER)))
        );
    }
    
}
