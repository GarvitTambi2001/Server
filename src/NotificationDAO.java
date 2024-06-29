import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<NotificationDTO> getTodayNotifications() throws SQLException {
        String query = "SELECT Message, CreatedDate FROM Notification WHERE DATE(CreatedDate) = CURDATE()";
        List<NotificationDTO> notifications = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                NotificationDTO notification = new NotificationDTO();
                notification.setMessage(resultSet.getString("Message"));
                notification.setCreatedDate(resultSet.getTimestamp("CreatedDate"));
                notifications.add(notification);
            }
        }
        return notifications;
    }
}
