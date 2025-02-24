package com.bank.repository;

import com.bank.model.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    static public List<Card> readAllCards() {
        String sql = "SELECT * FROM TABLE(card_pkg.READ_ALL_CARDS_FUNC())";
        List<Card> cards = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cards;
    }

    public static String deleteCard(String cardNumber) {
        String result = "Error deleting card.";
        String sql = "{CALL card_pkg.DELETE_CARD(?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, cardNumber);

            stmt.registerOutParameter(2, Types.VARCHAR);

            stmt.execute();

            result = stmt.getString(2);

        } catch (SQLException e) {
            System.err.println("Error deleting card: " + e.getMessage());
        }

        return result;
    }

    static public Card readCardByNumber(String cardNumber) {
        String sql = "SELECT * FROM TABLE(card_pkg.READ_CARD_BY_NUMBER(?))";
        Card card = null;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, cardNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    card = mapResultSetToCard(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return card;
    }

    static public List<Card> readCardsByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM TABLE(card_pkg.READ_CARD_BY_ACCOUNT_NUMBER(?))";
        List<Card> cards = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(mapResultSetToCard(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cards;
    }

    static public String createCard(double dailyLimit, double singlePaymentLimit, String cvv, boolean isActive, String accountNumber, String pin) {
        String result;
        String sql = "{call card_pkg.CREATE_CARD(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setDouble(1, dailyLimit);
            stmt.setDouble(2, singlePaymentLimit);
            stmt.setString(3, cvv);
            stmt.setInt(4, isActive ? 1 : 0);
            stmt.setString(5, accountNumber);
            stmt.setString(6, pin);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(7);
        } catch (SQLException e) {
            result = "Error creating card: " + e.getMessage();
        }

        return result;
    }

    static public String updateCardLimits(String cardNumber, double dailyLimit, double singlePaymentLimit) {
        String result;
        String sql = "{call card_pkg.UPDATE_CARD_LIMITS(?, ?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, cardNumber);
            stmt.setDouble(2, dailyLimit);
            stmt.setDouble(3, singlePaymentLimit);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(4);
        } catch (SQLException e) {
            result = "Error updating card limits: " + e.getMessage();
        }

        return result;
    }

    static public String updateCardStatus(String cardNumber, boolean isActive) {
        String result;
        String sql = "{call card_pkg.UPDATE_CARD_STATUS(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, cardNumber);
            stmt.setInt(2, isActive ? 1 : 0);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(3);
        } catch (SQLException e) {
            result = "Error updating card status: " + e.getMessage();
        }

        return result;
    }

    static public String updateCardPin(String cardNumber, String newPIN) {
        String result;
        String sql = "{call card_pkg.UPDATE_CARD_PIN(?, ?, ?)}";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, cardNumber);
            stmt.setString(2, newPIN);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            result = stmt.getString(3);
        } catch (SQLException e) {
            result = "Error updating card PIN: " + e.getMessage();
        }

        return result;
    }

    static private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        return new Card(
                rs.getString("card_number"),
                rs.getDate("date_of_expiration"),
                rs.getDouble("daily_limit"),
                rs.getDouble("single_payment_limit"),
                rs.getString("CVV"),
                rs.getInt("is_active") == 1,
                rs.getString("account_number"),
                rs.getString("PIN")
        );
    }

}
