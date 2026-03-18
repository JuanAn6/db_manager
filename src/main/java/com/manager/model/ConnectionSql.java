package com.manager.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionSql {

    private Connection connection;
    private final List<Database> databases = new ArrayList<>();

    // Tree menu properties
    private Boolean isFoldersTreeLoaded = false;

    // store connection parameters so we can keep track of saved connections
    private String url;
    private String user;
    private String password;
    private String name;

    public ConnectionSql() {
    }

    /**
     * Convenience constructor that also remembers parameters.
     */
    public ConnectionSql(String name, String url, String user, String password) {
        this.name = name;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void connect(String url, String user, String password) throws SQLException {
        // remember the parameters
        this.url = url;
        this.user = user;
        this.password = password;
        connection = DriverManager.getConnection(url, user, password);
    }

    public List<Database> getDatabases() throws SQLException {
        ensureConnected();
        databases.clear();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getCatalogs();

        while (rs.next()) {
            String dbName = rs.getString("TABLE_CAT");
            databases.add(new Database(dbName));
        }

        return databases;
    }

    public List<Table> getTables(String databaseName) throws SQLException {
        ensureConnected();

        List<Table> tables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet rs = metaData.getTables(
                databaseName,
                null,
                "%",
                new String[]{"TABLE"}
        )) {
            while (rs.next()) {
                tables.add(new Table(rs.getString("TABLE_NAME")));
            }
        }

        return tables;
    }

    public void ensureConnected() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        if (url == null || url.isBlank() || user == null || user.isBlank()) {
            throw new SQLException("Missing connection parameters");
        }
        connect(url, user, password);
    }

    public Boolean checkConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return true;
        }
        if (url == null || url.isBlank() || user == null || user.isBlank()) {
            throw new SQLException("Missing connection parameters");
        }
        return false;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    /**
     * TREE MENU PROPERTIES
     */

    public Boolean getIsFoldersTreeLoaded(){
        return this.isFoldersTreeLoaded;
    }

    public void setIsFoldersTreeLoaded(Boolean val){
        this.isFoldersTreeLoaded = val;
    }




    /**
     * 
     * CONNECTION PROPERTIES
     */

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if (name != null && !name.isEmpty()) {
            return name + " (" + url + ")";
        }
        return url;
    }
}
