package com.manager.controller;

import com.manager.model.ConnectionSql;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class MenuTreeController {

    private final TreeView<String> menuTree;
    private final TreeItem<String> root;
    private final TreeItem<String> connectionsCategory;

    public MenuTreeController(TreeView<String> menuTree) {
        this.menuTree = menuTree;
        this.root = new TreeItem<>("Root");
        this.connectionsCategory = new TreeItem<>("Connections");
        initializeTree();
    }

    public void addConnection(ConnectionSql connection) {
        String connectionName = connection.getName();
        if (connectionName == null || connectionName.isBlank()) {
            connectionName = connection.toString();
        }

        TreeItem<String> connectionItem = new TreeItem<>(connectionName);
        connectionItem.setExpanded(true);
        connectionItem.getChildren().addAll(java.util.List.of(
            new TreeItem<>("Databases"),
            new TreeItem<>("Users"),
            new TreeItem<>("Administer"),
            new TreeItem<>("System info")
        ));

        connectionsCategory.getChildren().add(connectionItem);
    }

    private void initializeTree() {
        root.setExpanded(true);
        connectionsCategory.setExpanded(true);
        root.getChildren().add(connectionsCategory);

        menuTree.setRoot(root);
        menuTree.setShowRoot(false);

        // Click event
        menuTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                System.out.println("Selected: " + newVal.getValue());
            } else if (newVal != null && !newVal.isLeaf()) {
                System.out.println("Selected not last item: " + newVal.getValue());
            }
        });
    }
}
