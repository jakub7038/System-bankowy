import oracle.jdbc.OracleTypes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    public List<Account> readAllAccounts() {
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

    public Account readAccountByNumber(String accountNumber) {
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

    public String createAccount(String accountNumber, String typeOfAccount, double balance,
                                Date dateOfCreation, String status, String login, String password,
                                String pesel) {
        String result;
        String sql = "{call account_pkg.CREATE_ACCOUNT(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

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
            result = "Error creating account: " + e.getMessage();
        }

        return result;
    }

    public String updatePassword(String accountNumber, String oldPassword, String newPassword) {
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

    public String login(String username, String password) {
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

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getString("account_number"),
                rs.getString("type_of_account"),
                rs.getDouble("balance"),
                rs.getDate("date_of_creation"),
                rs.getString("status"),
                rs.getString("login")
        );
    }
}
