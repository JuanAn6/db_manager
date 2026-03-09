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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public MenuTreeController(TreeView<TreeNodeData> menuTree) {
        this.menuTree = menuTree;
        this.root = new TreeItem<>(new TreeNodeData("Root", null));
        this.connectionsCategory = new TreeItem<>(new TreeNodeData("Connections", null));
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
            
            //TODO: buscar el tipo del elemento seleccionado para cargar lo necesarios

            if(newVal != null){
                System.out.println("Selected val"+newVal.toString());
            }

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
        TreeItem<TreeNodeData> conTreeItem = new TreeItem<>(new TreeNodeData(connectionName, SERVER_ICON));
        treeItemsConnections.put(conTreeItem, connection);
        
        //Expand connection
        conTreeItem.expandedProperty().addListener((obs, oldVal, newVal) -> {
            try{
                if (treeItemsConnections.get(conTreeItem).checkConnection()) {
                    System.out.println("Connect to the database...");
                    
                    //TODO: This will be inside the connection?
                    System.out.println("Load folders after a success...");
                    loadConnectionFolders(conTreeItem);
                }
            }catch(SQLException ex){
                AlertUtils.showError("Cant establish connection with " + treeItemsConnections.get(conTreeItem).getName() + ":\n" + ex.getMessage());
            }
        });

        connectionsCategory.getChildren().add(conTreeItem);
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
            new TreeItem<>(new TreeNodeData("Databases", FOLDER_ICON)),
            new TreeItem<>(new TreeNodeData("Users", FOLDER_ICON)),
            new TreeItem<>(new TreeNodeData("Administer", FOLDER_ICON)),
            new TreeItem<>(new TreeNodeData("System info", FOLDER_ICON))
        ));

        conn.setIsFoldersTreeLoaded(true);
        
    }
    
}
