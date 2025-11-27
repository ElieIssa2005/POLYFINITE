package com.eliemichel.polyfinite.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectMySQL {
    private Statement stmt;
    private Connection con;
    private boolean isConnected = false;
    private String errorMessage = "";

    public DBConnectMySQL() {
        final String DB_HOST = "jdbc:mysql://localhost:3306/polyfinite_game?allowPublicKeyRetrieval=true&useSSL=false";
        final String DB_USER = "root";
        final String DB_PWD = "1234";

        try {
            con = DriverManager.getConnection(DB_HOST, DB_USER, DB_PWD);
            stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            isConnected = true;
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            isConnected = false;
            errorMessage = e.getMessage();
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.con;
    }
    public Statement getStatement() {
        return this.stmt;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void closeConnection() {
        try {
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}