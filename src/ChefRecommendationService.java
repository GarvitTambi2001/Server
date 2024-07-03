import java.sql.SQLException;
import java.util.List;

public class ChefRecommendationService {
    private final ChefRecommendationDAO chefRecommendationDAO;
    private final ProfileDAO profileDAO;

    public ChefRecommendationService() {
        this.chefRecommendationDAO = new ChefRecommendationDAO();
        this.profileDAO = new ProfileDAO();
    }

    public List<ChefRecommendation> getChefRecommendations(String employeeId) throws SQLException {
        Profile profile = profileDAO.getProfileByEmployeeId(employeeId);
        return chefRecommendationDAO.getChefRecommendations(profile);
    }

    public List<ChefRecommendation> getVotedChefRecommendations() throws SQLException {
        return chefRecommendationDAO.getVotedChefRecommendations();
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
