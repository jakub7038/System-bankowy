package com.bank.repository;


import com.bank.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;




import java.sql.*;

public class ClientRepository {

    static public String createClient(String pesel, String firstName, String lastName,
                                      String middleName, String phoneNumber, Date dateOfBirth) {
        String result = null;
        String sql = "{call client_pkg.CREATE_CLIENT(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, pesel);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, middleName);
            stmt.setString(5, phoneNumber);
            stmt.setDate(6, dateOfBirth);
            stmt.registerOutParameter(7, Types.VARCHAR);

            stmt.execute();
            result = stmt.getString(7);

        } catch (SQLException e) {
            System.err.println("Error creating client: " + e.getMessage());
        }
        return result;
    }

    public static String deleteClient(String pesel) {
        String result = "Error deleting client.";
        String sql = "{CALL client_pkg.DELETE_CLIENT(?, ?)}"; // Stored procedure call

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, pesel);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.execute();
            result = stmt.getString(2);

        } catch (SQLException e) {
            System.err.println("Error deleting client: " + e.getMessage());
        }

        return result;
    }


    public static List<Client> readClientsByAccount(String accountNumber) {
    List<Client> clients = new ArrayList<>();
    String sql = "SELECT * FROM TABLE(client_pkg.READ_CLIENTS_BY_ACCOUNT(?))";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement stmt = connection.prepareStatement(sql)) {

        stmt.setString(1, accountNumber);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        }

    } catch (SQLException e) {
        System.err.println("Error reading clients by account number: " + e.getMessage());
    }

    return clients;
}

    static public List<Client> readAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM TABLE(client_pkg.READ_ALL_CLIENTS())";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error reading all clients: " + e.getMessage());
        }

        return clients;
    }

    static public Client readClientByPesel(String pesel) {
        Client client = null;
        String sql = "SELECT * FROM TABLE(client_pkg.READ_CLIENT_BY_PESEL(?))";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, pesel);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    client = mapResultSetToClient(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error reading client by PESEL: " + e.getMessage());
        }

        return client;
    }

    static public String updateClientFirstName(String pesel, String newFirstName) {
        return updateClientDetail("client_pkg.UPDATE_FIRST_NAME", pesel, newFirstName);
    }

    static public String updateClientLastName(String pesel, String newLastName) {
        return updateClientDetail("client_pkg.UPDATE_LAST_NAME", pesel, newLastName);
    }

    static public String updateClientMiddleName(String pesel, String newMiddleName) {
        return updateClientDetail("client_pkg.UPDATE_MIDDLE_NAME", pesel, newMiddleName);
    }

    static public String updateClientPhoneNumber(String pesel, String newPhoneNumber) {
        return updateClientDetail("client_pkg.UPDATE_PHONE_NUMBER", pesel, newPhoneNumber);
    }

    static private String updateClientDetail(String procedure, String pesel, String newValue) {
        String result = null;
        String sql = "{call " + procedure + "(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, pesel);
            stmt.setString(2, newValue);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();
            result = stmt.getString(3);

        } catch (SQLException e) {
            System.err.println("Error updating client detail: " + e.getMessage());
        }

        return result;
    }

    static private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getString("PESEL"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("middle_name"),
                rs.getString("phone_number"),
                rs.getDate("date_of_birth")
        );
    }

}
