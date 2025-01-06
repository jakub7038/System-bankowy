import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                System.out.println("It's working!");
            } else {
                System.out.println(":(");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
