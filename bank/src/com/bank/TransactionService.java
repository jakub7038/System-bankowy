package com.bank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    static public String createTransaction(int id, String accountNumber, String accountNumberReceiver,
                                    Timestamp dateOfTransaction, double amount,
                                    String typeOfTransaction, String description) {
        String resultMessage = null;
        String sql = "{call transaction_pkg.CREATE_TRANSACTION(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, accountNumber);
            stmt.setString(3, accountNumberReceiver);
            stmt.setTimestamp(4, dateOfTransaction);
            stmt.setDouble(5, amount);
            stmt.setString(6, typeOfTransaction);
            stmt.setString(7, description);
            stmt.registerOutParameter(8, Types.VARCHAR);

            stmt.execute();
            resultMessage = stmt.getString(8);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultMessage;
    }

   static  public List<Transaction> readAllTransactions() {
        String sql = "SELECT * FROM TABLE(transaction_pkg.read_all_transactions_func())";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    static public List<Transaction> readTransactionsByAccount(String accountNumber) {
        String sql = "SELECT * FROM TABLE(transaction_pkg.read_transactions_by_account_func(?))";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading transactions by account: " + e.getMessage());
        }

        return transactions;
    }

    static private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("ID"),
                rs.getString("account_number"),
                rs.getString("account_number_receiver"),
                rs.getTimestamp("date_of_transaction"),
                rs.getDouble("amount"),
                rs.getString("type_of_transaction"),
                rs.getString("description_of_transaction")
        );
    }
}
