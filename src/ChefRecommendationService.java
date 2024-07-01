import java.sql.SQLException;
import java.util.List;

public class ChefRecommendationService {
    private final ChefRecommendationDAO chefRecommendationDAO;

    public ChefRecommendationService() {
        this.chefRecommendationDAO = new ChefRecommendationDAO();
    }

    public List<ChefRecommendation> getChefRecommendations() {
        return chefRecommendationDAO.getChefRecommendations();
    }

    public List<ChefRecommendation> getTodayChefRecommendations() {
        return chefRecommendationDAO.getTodayChefRecommendations();
    }

    public void voteForRecommendations(String[] menuIds, String employeeId) throws VoteAlreadyGivenException, SQLException {
            for (String menuId : menuIds) {
                chefRecommendationDAO.increaseVoteCount(Integer.parseInt(menuId), employeeId);
            }
    }

    public boolean rollOutNextDayMenu(String[] menuIds) {
        try {
            for (String menuId : menuIds) {
                chefRecommendationDAO.insertChefRecommendation(Integer.parseInt(menuId));
            }
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Invalid MenuId format: " + e.getMessage());
            return false;
        }
    }
}
