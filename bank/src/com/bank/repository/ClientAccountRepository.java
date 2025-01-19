package com.bank.repository;

import java.sql.*;

public class ClientAccountRepository {

    public static String addClientToAccount(String pesel, String accountNumber) {
        String result;
        String sql = "{call client_account_pkg.ADD_CLIENT_TO_ACCOUNT(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, pesel);
            stmt.setString(2, accountNumber);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(3);
        } catch (SQLException e) {
            result = e.getMessage();
        }

        return result;
    }

    public static String deleteClientFromAccount(String pesel, String accountNumber) {
        String result;
        String sql = "{call client_account_pkg.DELETE_CLIENT_FROM_ACCOUNT(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, pesel);
            stmt.setString(2, accountNumber);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(3);
        } catch (SQLException e) {
            result = e.getMessage();
        }

        return result;
    }
}