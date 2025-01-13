package com.bank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiverService {

    public String createReceiver(Receiver receiver) {
        String result;
        String sql = "{call receiver_pkg.CREATE_RECEIVER(?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, receiver.getAccountNumberReceiver());
            stmt.setString(2, receiver.getAccountNumberTied());
            stmt.setString(3, receiver.getDescription());
            stmt.setString(4, receiver.getFirstName());
            stmt.setString(5, receiver.getLastName());
            stmt.registerOutParameter(6, Types.VARCHAR);

            stmt.execute();
            result = stmt.getString(6);

        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error creating receiver: " + e.getMessage();
        }

        return result;
    }

    public List<Receiver> readAllReceivers() {
        List<Receiver> receivers = new ArrayList<>();
        String sql = "SELECT * FROM TABLE(receiver_pkg.READ_ALL_RECEIVERS_FUNC())";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                receivers.add(mapResultSetToReceiver(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receivers;
    }

    public List<Receiver> readReceiversByTiedAccount(String accountNumberTied) {
        List<Receiver> receivers = new ArrayList<>();
        String sql = "SELECT * FROM TABLE(receiver_pkg.READ_RECEIVER_BY_TIED_ACCOUNT_FUNC(?))";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, accountNumberTied);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    receivers.add(mapResultSetToReceiver(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receivers;
    }

    public String deleteReceiver(String accountNumberReceiver, String accountNumberTied) {
        String result;
        String sql = "{call receiver_pkg.DELETE_RECEIVER(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, accountNumberReceiver);
            stmt.setString(2, accountNumberTied);
            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();
            result = stmt.getString(3);

        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error deleting receiver: " + e.getMessage();
        }

        return result;
    }

    private Receiver mapResultSetToReceiver(ResultSet rs) throws SQLException {
        return new Receiver(
                rs.getString("account_number_RECEIVER"),
                rs.getString("account_number_TIED"),
                rs.getString("description"),
                rs.getString("first_name"),
                rs.getString("last_name")
        );
    }
}
