import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    public List<FeedbackDTO> getAllFeedback() {
        List<FeedbackDTO> feedbackList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Feedback")) {

            while (resultSet.next()) {
                FeedbackDTO feedback = new FeedbackDTO();
                feedback.setEmployeeId(resultSet.getString("EmployeeId"));
                feedback.setMenuId(resultSet.getInt("MenuId"));
                feedback.setComment(resultSet.getString("Comment"));
                feedback.setRating(resultSet.getInt("Rating"));
                feedback.setSentiments(resultSet.getString("Sentiments"));
                feedback.setCreatedDate(resultSet.getTimestamp("CreatedDate"));

                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return feedbackList;
    }

    public void insertFeedback(FeedbackDTO feedback) throws SQLException {
        String query = "INSERT INTO feedback (EmployeeId, MenuId, Comment, Rating) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, feedback.getEmployeeId());
                statement.setInt(2, feedback.getMenuId());
                statement.setString(3, feedback.getComment());
                statement.setInt(4, feedback.getRating());
                statement.addBatch();

                statement.executeBatch();
        }
    }
}
