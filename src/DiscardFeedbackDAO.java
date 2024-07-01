import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscardFeedbackDAO {
    public void insertDiscardFeedback(DiscardFeedback feedback) throws SQLException {
        String query = "INSERT INTO DiscardMenuFeedback (MenuId, Question) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, feedback.getMenuId());
            statement.setString(2, feedback.getQuestion());
            statement.executeUpdate();
        }
    }

    public List<DiscardFeedback> getTodayDiscardFeedbackDetails() throws SQLException {
        List<DiscardFeedback> feedbackList = new ArrayList<>();
        String query = "SELECT MenuId, Question FROM DiscardMenuFeedback WHERE DATE(CreatedDate) = CURDATE()";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {


            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    DiscardFeedback feedback = new DiscardFeedback();
                    feedback.setMenuId(resultSet.getInt("MenuId"));
                    feedback.setQuestion(resultSet.getString("Question"));
                    feedbackList.add(feedback);
                }
            }
        }
        return feedbackList;
    }
}
