import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserSessionDAO {
    public void insertUserSession(UserSession session) throws SQLException {
        String query = "INSERT INTO UserSession (EmployeeId, RequestType, Time) VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, session.getEmployeeId());
            statement.setString(2, session.getRequestType());
            statement.executeUpdate();
        }
    }
}
