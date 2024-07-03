import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChefRecommendationDAO {

    public void increaseVoteCount(int menuId, String employeeId) throws VoteAlreadyGivenException, SQLException {
        final String CHECK_QUERY = "SELECT COUNT(*) FROM EmployeeChoice WHERE EmployeeId = ? AND MenuId = ? AND DATE(CreatedDate) = CURDATE()";
        final String UPDATE_QUERY = "UPDATE ChefRecommendation SET VoteCount = VoteCount + 1 WHERE MenuId = ?";
        final String INSERT_QUERY = "INSERT INTO EmployeeChoice (EmployeeId, MenuId, CreatedDate) VALUES (?, ?, CURDATE())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(CHECK_QUERY);
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_QUERY);
             PreparedStatement insertStatement = connection.prepareStatement(INSERT_QUERY)) {

            if (hasAlreadyVoted(checkStatement, employeeId, menuId)) {
                throw new VoteAlreadyGivenException("You have already voted for this menu item today.");
            }

            incrementVoteCount(updateStatement, menuId);
            recordVote(insertStatement, employeeId, menuId);
        }
    }

    private boolean hasAlreadyVoted(PreparedStatement checkStatement, String employeeId, int menuId) throws SQLException {
        checkStatement.setString(1, employeeId);
        checkStatement.setInt(2, menuId);
        try (ResultSet resultSet = checkStatement.executeQuery()) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    private void incrementVoteCount(PreparedStatement updateStatement, int menuId) throws SQLException {
        updateStatement.setInt(1, menuId);
        updateStatement.executeUpdate();
    }

    private void recordVote(PreparedStatement insertStatement, String employeeId, int menuId) throws SQLException {
        insertStatement.setString(1, employeeId);
        insertStatement.setInt(2, menuId);
        insertStatement.executeUpdate();
    }


    public List<ChefRecommendation> getChefRecommendations(Profile profile) {
        List<ChefRecommendation> recommendations = new ArrayList<>();
        String query = "SELECT cr.MenuId, m.Name AS MenuName, m.Score AS score, cr.VoteCount, " +
                "m.Diet, m.Spice, m.CuisineType , m.SweetTooth " +
                "FROM ChefRecommendation cr " +
                "JOIN Menu m ON cr.MenuId = m.MenuId " +
                "WHERE DATE(cr.CreatedDate) = CURDATE() " +
                "ORDER BY (CASE WHEN m.Diet = ? THEN 0 ELSE 1 END), " +
                "(CASE WHEN m.Spice = ? THEN 0 ELSE 1 END), " +
                "(CASE WHEN m.CuisineType  = ? THEN 0 ELSE 1 END), " +
                "(CASE WHEN m.SweetTooth = ? THEN 0 ELSE 1 END), cr.VoteCount DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, profile.getDiet());
            statement.setString(2, profile.getSpice());
            statement.setString(3, profile.getCuisineType());
            statement.setString(4, profile.getSweetTooth());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ChefRecommendation recommendation = new ChefRecommendation();
                    recommendation.setMenuId(resultSet.getInt("MenuId"));
                    recommendation.setMenuName(resultSet.getString("MenuName"));
                    recommendation.setScore(resultSet.getBigDecimal("score"));
                    recommendation.setVoteCount(resultSet.getInt("VoteCount"));

                    recommendations.add(recommendation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return recommendations;
    }

    public void insertChefRecommendation(int menuId) {
        String query = "INSERT INTO ChefRecommendation (MenuId, VoteCount, CreatedDate) VALUES (?, 0, CURDATE())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, menuId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting chef recommendation: " + e.getMessage());
        }
    }

    public List<ChefRecommendation> getVotedChefRecommendations() throws SQLException {
        List<ChefRecommendation> recommendations = new ArrayList<>();
        String query = "SELECT * FROM ChefRecommendation WHERE DATE(CreatedDate) >= CURDATE()";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ChefRecommendation recommendation = new ChefRecommendation();
                recommendation.setRecId(resultSet.getInt("RecId"));
                recommendation.setMenuId(resultSet.getInt("MenuId"));
                recommendation.setVoteCount(resultSet.getInt("VoteCount"));
                recommendation.setCreatedDate(resultSet.getTimestamp("CreatedDate"));

                recommendations.add(recommendation);
            }
        }
        return recommendations;
    }
}