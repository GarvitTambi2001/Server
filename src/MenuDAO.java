import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public boolean addMenu(Menu menu) {
        String query = "INSERT INTO menu (Name, Price, AvailabilityStatus, MealType, Score, Diet, Spice, CuisineType, SweetTooth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, menu.getName());
            preparedStatement.setBigDecimal(2, menu.getPrice());
            preparedStatement.setString(3, menu.getAvailabilityStatus());
            preparedStatement.setString(4, menu.getMealType());
            preparedStatement.setBigDecimal(5, menu.getScore());
            preparedStatement.setString(6, menu.getDiet());
            preparedStatement.setString(7, menu.getSpice());
            preparedStatement.setString(8, menu.getCuisineType());
            preparedStatement.setString(9, menu.getSweetTooth());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMenu(Menu menu) {
        String query = "UPDATE menu SET Name = ?, Price = ?, AvailabilityStatus = ?, MealType = ?, Diet = ?, Spice = ?, CuisineType = ?, SweetTooth = ? WHERE MenuId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, menu.getName());
            preparedStatement.setBigDecimal(2, menu.getPrice());
            preparedStatement.setString(3, menu.getAvailabilityStatus());
            preparedStatement.setString(4, menu.getMealType());
            preparedStatement.setString(5, menu.getDiet());
            preparedStatement.setString(6, menu.getSpice());
            preparedStatement.setString(7, menu.getCuisineType());
            preparedStatement.setString(8, menu.getSweetTooth());
            preparedStatement.setBigDecimal(9, menu.getMenuId());
            return preparedStatement.executeUpdate() > 0;
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

    public List<Menu> getAllMenus() {
        List<Menu> menus = new ArrayList<>();
        String query = "SELECT * FROM menu";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Menu menu = new Menu();
                menu.setMenuId(resultSet.getBigDecimal("MenuId"));
                menu.setPrice(resultSet.getBigDecimal("Price"));
                menu.setAvailabilityStatus(resultSet.getString("AvailabilityStatus"));
                menu.setMealType(resultSet.getString("MealType"));
                menu.setScore(resultSet.getBigDecimal("Score"));
                menu.setName(resultSet.getString("Name"));
                menu.setDiet(resultSet.getString("Diet"));
                menu.setSpice(resultSet.getString("Spice"));
                menu.setCuisineType(resultSet.getString("CuisineType"));
                menu.setSweetTooth(resultSet.getString("SweetTooth"));
                menus.add(menu);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return menus;
    }

    public List<Menu> getTopRecommendations(String numberOfRecommendations) {
        List<Menu> topRecommendations = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Menu WHERE AvailabilityStatus = 'Yes' ORDER BY Score DESC LIMIT " + numberOfRecommendations;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Menu menu = new Menu();
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
