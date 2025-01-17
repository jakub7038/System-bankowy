package com.bank.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/freepdb1";
    private static final String USER = "bank";
    private static final String PASSWORD = "bank";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
