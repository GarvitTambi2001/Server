import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackService {

    private final FeedbackDAO feedbackDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
    }

    public void updateMenuScoresAccToFeedback() {
        List<FeedbackDTO> feedbackList = feedbackDAO.getAllFeedback();

        Map<Integer, Double> scoreMap = new HashMap<>();
        for (FeedbackDTO feedback : feedbackList) {
            int menuId = feedback.getMenuId();
            int rating = feedback.getRating();
            String sentiment = feedback.getSentiments();

            double sentimentScore = analyzeSentiment(sentiment);
            double finalScore = rating + sentimentScore;

            scoreMap.put(menuId, scoreMap.getOrDefault(menuId, 0.0) + finalScore);
        }

        for (Map.Entry<Integer, Double> entry : scoreMap.entrySet()) {
            MenuDAO menuDAO = new MenuDAO();
            menuDAO.updateMenuScore(entry.getKey(), entry.getValue());
        }
    }

    private double analyzeSentiment(String sentiment) {
        switch (sentiment.toLowerCase()) {
            case "positive":
                return 1.0;
            case "neutral":
                return 0.5;
            case "negative":
                return -1.0;
            default:
                return 0.0;
        }
    }
}
