package com.bank.model;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTester {
    public static boolean testConnection() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Połączenie z bazą danych zostało nawiązane.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas łączenia z bazą danych: " + e.getMessage());
        }
        System.out.println("Nie udało się nawiązać połączenia z bazą danych.");
        return false;
    }
}
