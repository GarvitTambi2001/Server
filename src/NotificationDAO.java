import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<Notification> getTodayNotifications() throws SQLException {
        String query = "SELECT Message, CreatedDate FROM Notification WHERE DATE(CreatedDate) = CURDATE()";
        List<Notification> notifications = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Notification notification = new Notification();
                notification.setMessage(resultSet.getString("Message"));
                notification.setCreatedDate(resultSet.getTimestamp("CreatedDate"));
                notifications.add(notification);
            }
        }
        return notifications;
    }
}
