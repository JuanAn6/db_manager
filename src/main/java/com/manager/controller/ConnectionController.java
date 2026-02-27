package com.manager.controller;

import java.util.List;
import java.sql.SQLException;

import com.manager.model.ConnectionSql;
import com.manager.model.Database;
import com.manager.model.Table;

public class ConnectionController {
    public void testConnection(){
        ConnectionSql connectionSql = new ConnectionSql();

        try {
            connectionSql.connect(
                "jdbc:mysql://localhost:3306/",
                "root",
                "1234"
            );

            List<Database> dbs = connectionSql.getDatabases();

            for (Database db : dbs) {
                System.out.println("Base: " + db.getName());

                for (Table table : db.getTables()) {
                    System.out.println("   Tabla: " + table.getName());
                }
            }

            connectionSql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
}
