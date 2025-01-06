import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    public List<Account> readAllAccounts() {
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection()) {
            CallableStatement stmt = connection.prepareCall("{call account_pkg.READ_ALL_ACCOUNTS(?)}");
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                Account account = new Account(
                        rs.getString("account_number"),
                        rs.getString("type_of_account"),
                        rs.getDouble("balance"),
                        rs.getDate("date_of_creation"),
                        rs.getString("status"),
                        rs.getString("login")
                );
                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving accounts: " + e.getMessage(), e);
        }

        return accounts;
    }

    public String createAccount(String accountNumber, String typeOfAccount, double balance,
                                Date dateOfCreation, String status, String login, String password,
                                String pesel) {
        String result;

        try (Connection connection = DatabaseConfig.getConnection()) {
            CallableStatement stmt = connection.prepareCall("{call account_pkg.CREATE_ACCOUNT(?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            stmt.setString(1, accountNumber);
            stmt.setString(2, typeOfAccount);
            stmt.setDouble(3, balance);
            stmt.setDate(4, dateOfCreation);
            stmt.setString(5, status);
            stmt.setString(6, login);
            stmt.setString(7, password);
            stmt.setString(8, pesel);

            stmt.registerOutParameter(9, Types.VARCHAR);

            stmt.execute();

            result = stmt.getString(9);

        } catch (SQLException e) {
            throw new RuntimeException("Error during account creation: " + e.getMessage(), e);
        }

        return result;
    }

    public String updatePassword(String accountNumber, String oldPassword, String newPassword) {
        String result;

        try (Connection connection = DatabaseConfig.getConnection()) {
            CallableStatement stmt = connection.prepareCall("{call account_pkg.UPDATE_PASSWORD(?, ?, ?, ?)}");

            stmt.setString(1, accountNumber);
            stmt.setString(2, oldPassword);
            stmt.setString(3, newPassword);

            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            result = stmt.getString(4);

        } catch (SQLException e) {
            throw new RuntimeException("Error during password update: " + e.getMessage(), e);
        }

        return result;
    }

    public String login(String username, String password) {
        String loginStatus;

        try (Connection connection = DatabaseConfig.getConnection()) {
            CallableStatement stmt = connection.prepareCall("{call account_pkg.LOGIN(?, ?, ?)}");

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            loginStatus = stmt.getString(3);

        } catch (SQLException e) {
            throw new RuntimeException("Error during login: " + e.getMessage(), e);
        }

        return loginStatus;
    }
}
