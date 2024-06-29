import java.sql.*;
import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public List<NotificationDTO> getTodayNotifications() throws SQLException {
        return notificationDAO.getTodayNotifications();
    }
}
