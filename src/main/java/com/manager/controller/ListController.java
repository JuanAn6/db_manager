package com.manager.controller;

import com.manager.model.Database;
import com.manager.util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class ListController {

    @FXML
    private TableView<Database> tableView;

    @FXML
    private TableColumn<Database, String> nameColumn;

    private final ObservableList<Database> databases = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.setItems(databases);
    }

    public void setDatabases(List<Database> databases) {
        if (databases == null) {
            this.databases.clear();
            return;
        }
        this.databases.setAll(databases);
    }

    public static StackPane buildDatabasesContent(List<Database> databases) {
        try {
            FXMLLoader loader = new FXMLLoader(ListController.class.getResource("/com/manager/list.fxml"));
            Node view = loader.load();
            ListController controller = loader.getController();
            controller.setDatabases(databases);
            return new StackPane(view);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Could not load databases view:\n" + e.getMessage());
            return null;
        }
    }
}
