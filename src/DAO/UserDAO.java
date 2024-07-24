package DAO;

import DTO.User;
import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User getUserByEmployeeIdAndPassword(String employeeId, String password) {
        String query = "SELECT user.*, role.RoleName FROM user " +
                "JOIN role ON user.RoleID = role.RoleID " +
                "WHERE user.EmployeeID = ? AND user.Password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employeeId);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapToUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return new User();
    }

    private User mapToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("UserID"));
        user.setEmployeeId(resultSet.getString("EmployeeID"));
        user.setName(resultSet.getString("Name"));
        user.setPassword(resultSet.getString("Password"));
        user.setRoleId(resultSet.getInt("RoleID"));
        user.setRoleName(resultSet.getString("RoleName"));
        return user;
    }
}
