import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public boolean addMenu(MenuDTO menu) {
        String query = "INSERT INTO menu (Name, Price, AvailabilityStatus, MealType, Score) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, menu.getName());
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
        String query = "UPDATE menu SET Name = ?, Price = ?, AvailabilityStatus = ?, MealType = ? WHERE MenuId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, menu.getName());
            preparedStatement.setBigDecimal(2, menu.getPrice());
            preparedStatement.setString(3, menu.getAvailabilityStatus());
            preparedStatement.setString(4, menu.getMealType());
            preparedStatement.setBigDecimal(5, menu.getMenuId());
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
                menu.setMenuId(resultSet.getBigDecimal("MenuId"));
                menu.setPrice(resultSet.getBigDecimal("Price"));
                menu.setAvailabilityStatus(resultSet.getString("AvailabilityStatus"));
                menu.setMealType(resultSet.getString("MealType"));
                menu.setScore(resultSet.getBigDecimal("Score"));
                menu.setName(resultSet.getString("Name"));
                menus.add(menu);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return menus;
    }

    public List<MenuDTO> getTopRecommendations() {
        List<MenuDTO> topRecommendations = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Menu WHERE AvailabilityStatus = 'Yes' ORDER BY Score DESC LIMIT 6";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                MenuDTO menu = new MenuDTO();
                menu.setMenuId(resultSet.getBigDecimal("MenuId"));
                menu.setName(resultSet.getString("Name"));
                menu.setPrice(resultSet.getBigDecimal("Price"));
                menu.setAvailabilityStatus(resultSet.getString("AvailabilityStatus"));
                menu.setMealType(resultSet.getString("MealType"));
                menu.setScore(resultSet.getBigDecimal("Score"));

                topRecommendations.add(menu);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return topRecommendations;
    }

    public void updateMenuScore(int menuId, double score) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE Menu SET Score = ? WHERE MenuId = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setBigDecimal(1, BigDecimal.valueOf(score));
            updateStatement.setInt(2, menuId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
