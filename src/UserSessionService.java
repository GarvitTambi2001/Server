import java.sql.Connection;
import java.sql.SQLException;

public class UserSessionService {
    private final UserSessionDAO userSessionDAO;

    public UserSessionService() {
        this.userSessionDAO = new UserSessionDAO();
    }

    public void logUserSession(UserSessionDTO session) throws SQLException {
        userSessionDAO.insertUserSession(session);
    }
}
