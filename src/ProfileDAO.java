import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileDAO {
    public Profile getProfileByEmployeeId(String employeeId) throws SQLException {
        String query = "SELECT EmployeeId, Diet, Spice,  CuisineType , SweetTooth FROM Profile WHERE EmployeeId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, employeeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Profile profile = new Profile();
                    profile.setEmployeeId(resultSet.getString("EmployeeId"));
                    profile.setDiet(resultSet.getString("Diet"));
                    profile.setSpice(resultSet.getString("Spice"));
                    profile.setCuisineType(resultSet.getString("CuisineType"));
                    profile.setSweetTooth(resultSet.getString("SweetTooth"));
                    return profile;
                }else {
                    return null;
                }
            }
        }
    }

    public boolean createProfile(Profile profile) {
        String query = "INSERT INTO Profile (EmployeeId, Diet, Spice,  CuisineType, SweetTooth) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, profile.getEmployeeId());
            statement.setString(2, profile.getDiet());
            statement.setString(3, profile.getSpice());
            statement.setString(4, profile.getCuisineType());
            statement.setString(5, profile.getSweetTooth());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean updateProfile(Profile profile) {
        String query = "UPDATE Profile SET Diet = ?, Spice = ?,  CuisineType = ?, SweetTooth = ? WHERE EmployeeId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, profile.getDiet());
            statement.setString(2, profile.getSpice());
            statement.setString(3, profile.getCuisineType());
            statement.setString(4, profile.getSweetTooth());
            statement.setString(5, profile.getEmployeeId());
            statement.executeUpdate();
            return true;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
