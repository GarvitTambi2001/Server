import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackService {

    private final FeedbackDAO feedbackDAO;

    private static final List<String> GOOD_WORDS = List.of("good", "excellent", "great", "fantastic", "amazing");
    private static final List<String> BAD_WORDS = List.of("bad", "terrible", "awful", "poor", "worst");
    private static final List<String> NEUTRAL_WORDS = List.of("okay", "fine", "average", "mediocre", "decent");

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

    public void submitFeedback(String employeeId,  Integer menuId, String comment, int rating) throws SQLException {
            String sentiment = getSentiment(comment);
            FeedbackDTO feedback = new FeedbackDTO();
            feedback.setEmployeeId(employeeId);
            feedback.setMenuId(menuId);
            feedback.setComment(comment);
            feedback.setRating(rating);
            feedback.setSentiments(sentiment);
            feedbackDAO.insertFeedback(feedback);
    }

    private String getSentiment(String comment) {
        for (String word : comment.split("\\s+")) {
            String lowerCaseWord = word.toLowerCase();
            if (GOOD_WORDS.contains(lowerCaseWord)) {
                return "positive";
            } else if (BAD_WORDS.contains(lowerCaseWord)) {
                return "negative";
            } else if (NEUTRAL_WORDS.contains(lowerCaseWord)) {
                return "neutral";
            }
        }
        return "neutral";
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
