package com.manager.controller;

import com.manager.model.Table;
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

public class TablesDatabaseListControler {

    @FXML
    private TableView<Table> tableView;

    @FXML
    private TableColumn<Table, String> nameColumn;

    private final ObservableList<Table> tablesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.setItems(tablesData);
    }

    public void setTables(List<Table> tables) {
        tablesData.clear();
        if (tables == null) {
            return;
        }

        for (Table table : tables) {
            if (table == null || table.getName() == null || table.getName().isBlank()) {
                continue;
            }
            tablesData.add(table);
        }
    }

    public static StackPane buildTablesContent(List<Table> tables) {
        try {
            FXMLLoader loader = new FXMLLoader(TablesDatabaseListControler.class.getResource("/com/manager/TablesDatabases.fxml"));
            Node view = loader.load();
            TablesDatabaseListControler controller = loader.getController();
            controller.setTables(tables);
            return new StackPane(view);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Could not load tables view:\n" + e.getMessage());
            return null;
        }
    }
}
