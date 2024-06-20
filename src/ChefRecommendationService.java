import java.util.List;

public class ChefRecommendationService {
    private final ChefRecommendationDAO chefRecommendationDAO;

    public ChefRecommendationService() {
        this.chefRecommendationDAO = new ChefRecommendationDAO();
    }

    public List<ChefRecommendationDTO> getChefRecommendations() {
        return chefRecommendationDAO.getChefRecommendations();
    }

    public List<ChefRecommendationDTO> getTodayChefRecommendations() {
        return chefRecommendationDAO.getTodayChefRecommendations();
    }

    public boolean voteForRecommendations(String[] menuIds) {
        try {
            for (String menuId : menuIds) {
                chefRecommendationDAO.increaseVoteCount(Integer.parseInt(menuId));
            }
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Invalid MenuId format: " + e.getMessage());
            return false;
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
