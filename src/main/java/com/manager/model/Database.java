package com.manager.model;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private final String name;
    private final List<Table> tables = new ArrayList<>();

    public Database(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void addTable(Table table) {
        tables.add(table);
    }
}