package com.bank.repository;

import com.bank.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    static public String createTransaction(String accountNumber, String accountNumberReceiver,
                                           double amount, String typeOfTransaction, String description) {
        String resultMessage = null;
        String sql = "{call transaction_pkg.CREATE_TRANSACTION(?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {
            String paddedAccountNumber = String.format("%-26s", accountNumber);
            String paddedReceiverAccountNumber = String.format("%-26s", accountNumberReceiver);

            stmt.setString(1, paddedAccountNumber);
            stmt.setString(2, paddedReceiverAccountNumber);
            stmt.setInt(3, (int)(100*amount));
            stmt.setString(4, typeOfTransaction);
            stmt.setString(5, description);
            stmt.registerOutParameter(6, Types.VARCHAR);

            stmt.execute();

            resultMessage = stmt.getString(6);
            System.out.println("Result from DB: " + resultMessage);

        } catch (SQLException e) {
            e.printStackTrace();
            resultMessage = "Error: " + e.getMessage();
        }

        return resultMessage;
    }

    static public List<Transaction> readAllTransactions() {
        String sql = "SELECT * FROM TABLE(transaction_pkg.READ_ALL_TRANSACTIONS_FUNC())";
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
        String sql = "SELECT * FROM TABLE(transaction_pkg.READ_TRANSACTIONS_BY_ACCOUNT_FUNC(?))";
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
                rs.getDouble("amount")/100,
                rs.getString("type_of_transaction"),
                rs.getString("description_of_transaction")
        );
    }
}
