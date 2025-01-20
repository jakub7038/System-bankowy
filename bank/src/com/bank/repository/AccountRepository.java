
package com.bank.repository;

import com.bank.model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    static public List<Account> readAllAccounts() {
        String sql = "SELECT * FROM TABLE(account_pkg.READ_ALL_ACCOUNTS_FUNC())";
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    static public Account readAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM TABLE(account_pkg.READ_ACCOUNT_BY_NUMBER_FUNC(?))";
        Account account = null;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    account = mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }

    static public String createAccount(String typeOfAccount, double balance, String status, String login,
                                       String password, String pesel) {
        String result;
        String sql = "{call account_pkg.create_account(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, typeOfAccount);
            stmt.setDouble(2, balance*100);
            stmt.setString(3, status);
            stmt.setString(4, login);
            stmt.setString(5, password);
            stmt.setString(6, pesel);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(7);
        } catch (SQLException e) {
            result = "Error creating account: " + e.getMessage();
        }

        return result;
    }

    public static String deleteAccount(String accountNumber) {
        String result = "Error deleting account.";
        String sql = "{CALL account_pkg.DELETE_ACCOUNT(?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, accountNumber);

            stmt.registerOutParameter(2, Types.VARCHAR);

            stmt.execute();

            result = stmt.getString(2);

        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }

        return result;
    }

    static public String updatePassword(String accountNumber, String oldPassword, String newPassword) {
        String result;
        String sql = "{call account_pkg.UPDATE_PASSWORD(?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setString(2, oldPassword);
            stmt.setString(3, newPassword);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(4);
        } catch (SQLException e) {
            result = "Error updating password: " + e.getMessage();
        }

        return result;
    }

    public static String updateLogin(String accountNumber, String newLogin) {
        String result;
        String sql = "{call account_pkg.UPDATE_LOGIN(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setString(2, newLogin);

            stmt.registerOutParameter(3, Types.VARCHAR);

            stmt.execute();
            result = stmt.getString(3);

        } catch (SQLException e) {
            result = "Error updating login: " + e.getMessage();
        }

        return result;
    }

    static public String login(String username, String password) {
        String loginStatus;
        String sql = "{call account_pkg.LOGIN(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            loginStatus = stmt.getString(3);
        } catch (SQLException e) {
            loginStatus = "Error during login: " + e.getMessage();
        }

        return loginStatus;
    }

    static public List<Account> readAccountsByClient(String pesel) {
        String sql = "SELECT * FROM TABLE(account_pkg.READ_ACCOUNTS_BY_CLIENT(?))";
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, pesel);  // Set the PESEL parameter

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));  // Map each row to Account object
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    static public String adminUpdatePassword(String accountNumber, String newPassword) {
        String result;
        String sql = "{call account_pkg.admin_update_password(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setString(2, newPassword);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(3);
        } catch (SQLException e) {
            result = "Error updating password: " + e.getMessage();
        }

        return result;
    }

    static public String adminUpdateLogin(String accountNumber, String newLogin) {
        String result;
        String sql = "{call account_pkg.admin_update_login(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setString(2, newLogin);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(3);
        } catch (SQLException e) {
            result = "Error updating login: " + e.getMessage();
        }

        return result;
    }

    static private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getString("account_number"),
                rs.getString("type_of_account"),
                rs.getDouble("balance")/100,
                rs.getDate("date_of_creation"),
                rs.getString("status"),
                rs.getString("login")
        );
    }
}

