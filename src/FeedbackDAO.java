import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Feedback")) {

            while (resultSet.next()) {
                Feedback feedback = new Feedback();
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

    public void insertFeedback(Feedback feedback) throws SQLException, FeedbackAlreadyExistsException {
        final String CHECK_QUERY = "SELECT COUNT(*) FROM feedback WHERE EmployeeId = ? AND MenuId = ? AND DATE(CreatedDate) = CURDATE()";
        final String INSERT_QUERY = "INSERT INTO feedback (EmployeeId, MenuId, Comment, Rating, Sentiments, CreatedDate) VALUES (?, ?, ?, ?, ?, CURDATE())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(CHECK_QUERY);
             PreparedStatement insertStatement = connection.prepareStatement(INSERT_QUERY)) {

            if (isFeedbackAlreadyGiven(checkStatement, feedback)) {
                throw new FeedbackAlreadyExistsException("You can give feedback for a particular dish only one time in a day.");
            }

            saveFeedback(insertStatement, feedback);
        }
    }

    private boolean isFeedbackAlreadyGiven(PreparedStatement checkStatement, Feedback feedback) throws SQLException {
        checkStatement.setString(1, feedback.getEmployeeId());
        checkStatement.setInt(2, feedback.getMenuId());
        try (ResultSet resultSet = checkStatement.executeQuery()) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    private void saveFeedback(PreparedStatement insertStatement, Feedback feedback) throws SQLException {
        insertStatement.setString(1, feedback.getEmployeeId());
        insertStatement.setInt(2, feedback.getMenuId());
        insertStatement.setString(3, feedback.getComment());
        insertStatement.setInt(4, feedback.getRating());
        insertStatement.setString(5, feedback.getSentiments());
        insertStatement.executeUpdate();
    }

    public List<Feedback> getLowRatedFeedbacks() throws SQLException {
        String query =  "SELECT f.EmployeeId, f.Sentiments, f.MenuId, m.Name, f.Comment, avgTable.avgRating " +
                "FROM Feedback f " +
                "JOIN Menu m ON f.MenuId = m.MenuId " +
                "JOIN (" +
                "    SELECT f.MenuId, AVG(f.Rating) as avgRating " +
                "    FROM Feedback f " +
                "    GROUP BY f.MenuId " +
                "    HAVING avgRating <= 2" +
                ") as avgTable ON f.MenuId = avgTable.MenuId";
        List<Feedback> feedbackList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                feedback.setMenuId(resultSet.getInt("MenuId"));
                feedback.setEmployeeId(resultSet.getString("EmployeeId"));
                feedback.setComment(resultSet.getString("Comment"));
                feedback.setAvgRating(resultSet.getDouble("avgRating"));
                feedback.setSentiments(resultSet.getString("Sentiments"));
                feedback.setMenuName(resultSet.getString("Name"));
                feedbackList.add(feedback);
            }
        }

        return feedbackList;
    }
}
