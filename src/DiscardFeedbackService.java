import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiscardFeedbackService {
    private final DiscardFeedbackDAO discardFeedbackDAO;

    public DiscardFeedbackService(){
        discardFeedbackDAO = new DiscardFeedbackDAO();
    }

    public void submitDiscardFeedback(String questions, String menuId) throws SQLException {
        DiscardFeedback discardFeedback = new DiscardFeedback();
        discardFeedback.setQuestion(questions);
        discardFeedback.setMenuId(Integer.parseInt(menuId));
        this.discardFeedbackDAO.insertDiscardFeedback(discardFeedback);
    }

    public Map<Integer, String> getTodayFeedbackDetails() throws SQLException{
        List<DiscardFeedback> discardFeedbackDetails = discardFeedbackDAO.getTodayDiscardFeedbackDetails();
        return preparemenuIdWithQuestionMap(discardFeedbackDetails);
    }

    private Map<Integer, String> preparemenuIdWithQuestionMap(List<DiscardFeedback> discardFeedbackDetails){
        Map<Integer, String> menuIdWithQuestionMap = new LinkedHashMap<Integer, String>();
        for(DiscardFeedback record : discardFeedbackDetails){
           menuIdWithQuestionMap.put(record.getMenuId(),record.getQuestion());
        }
        return menuIdWithQuestionMap;
    }
}
