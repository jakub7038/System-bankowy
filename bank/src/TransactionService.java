import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    public String createTransaction(int id, String accountNumber, String accountNumberReceiver,
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

    public List<Transaction> readAllTransactions() {
        String sql = "{call transaction_pkg.READ_ALL_TRANSACTIONS(?)}";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public List<Transaction> readTransactionsByAccount(String accountNumber) {
        String sql = "{call transaction_pkg.READ_TRANSACTIONS_BY_ACCOUNT(?, ?)}";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, accountNumber);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("ID"),
                rs.getString("account_number"),
                rs.getString("account_number_RECEIVER"),
                rs.getTimestamp("date_of_transaction"),
                rs.getDouble("amount"),
                rs.getString("type_of_transaction"),
                rs.getString("description_of_transaction")
        );
    }
}
