import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public boolean addMenu(MenuDTO menu) {
        String query = "INSERT INTO menu (MenuId, Prices, AvailabilityStatus, MealType, Score) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, menu.getMenuId());
            preparedStatement.setBigDecimal(2, menu.getPrice());
            preparedStatement.setString(3, menu.getAvailabilityStatus());
            preparedStatement.setString(4, menu.getMealType());
            preparedStatement.setBigDecimal(5, menu.getScore());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMenu(MenuDTO menu) {
        String query = "UPDATE menu SET Prices = ?, AvailabilityStatus = ?, MealType = ?, Score = ? WHERE MenuId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBigDecimal(1, menu.getPrice());
            preparedStatement.setString(2, menu.getAvailabilityStatus());
            preparedStatement.setString(3, menu.getMealType());
            preparedStatement.setBigDecimal(4, menu.getScore());
            preparedStatement.setString(5, menu.getMenuId());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMenu(String menuId) {
        String query = "DELETE FROM menu WHERE MenuId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, menuId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public List<MenuDTO> getAllMenus() {
        List<MenuDTO> menus = new ArrayList<>();
        String query = "SELECT * FROM menu";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                MenuDTO menu = new MenuDTO();
                menu.setMenuId(resultSet.getString("MenuId"));
                menu.setPrice(resultSet.getBigDecimal("Prices"));
                menu.setAvailabilityStatus(resultSet.getString("AvailabilityStatus"));
                menu.setMealType(resultSet.getString("MealType"));
                menu.setScore(resultSet.getBigDecimal("Score"));
                menus.add(menu);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return menus;
    }
}
