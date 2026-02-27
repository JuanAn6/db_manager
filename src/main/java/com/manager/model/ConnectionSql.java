package com.manager.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionSql {

    private Connection connection;
    private final List<Database> databases = new ArrayList<>();

    public void connect(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public List<Database> getDatabases() throws SQLException {
        databases.clear();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getCatalogs();

        while (rs.next()) {
            String dbName = rs.getString("TABLE_CAT");
            Database db = new Database(dbName);
            loadTables(db);
            databases.add(db);
        }

        return databases;
    }

    private void loadTables(Database database) throws SQLException {

        DatabaseMetaData metaData = connection.getMetaData();

        ResultSet rs = metaData.getTables(
                database.getName(), // catalog
                null,               // schema
                "%",                // table name pattern
                new String[]{"TABLE"}
        );

        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            database.addTable(new Table(tableName));
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}