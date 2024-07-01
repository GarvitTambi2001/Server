import org.javatuples.Triplet;
import java.sql.SQLException;
import java.util.*;

public class FeedbackService {

    private final FeedbackDAO feedbackDAO;
    private final MenuDAO menuDAO;

    private static final Set<String> GOOD_WORDS = new HashSet<>(Arrays.asList(
            "good", "great", "excellent", "amazing", "awesome", "fantastic", "positive", "nice", "wonderful", "happy"
    ));

    private static final Set<String> BAD_WORDS = new HashSet<>(Arrays.asList(
            "bad", "terrible", "awful", "horrible", "poor", "negative", "worst", "sad", "angry", "hate"
    ));

    private static final Set<String> NEUTRAL_WORDS = new HashSet<>(Arrays.asList(
            "okay", "fine", "average", "mediocre", "neutral", "so-so"
    ));


    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
        this.menuDAO = new MenuDAO();
    }

    public void updateMenuScoresAccToFeedback() {
        List<Feedback> feedbackList = feedbackDAO.getAllFeedback();

        Map<Integer, Double> scoreMap = new HashMap<>();
        for (Feedback feedback : feedbackList) {
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

    public void submitFeedback(String employeeId,  Integer menuId, String comment, int rating) throws SQLException,FeedbackAlreadyExistsException {
            String sentiment = getSentiment(comment);
            Feedback feedback = new Feedback();
            feedback.setEmployeeId(employeeId);
            feedback.setMenuId(menuId);
            feedback.setComment(comment);
            feedback.setRating(rating);
            feedback.setSentiments(sentiment);
            feedbackDAO.insertFeedback(feedback);
    }

    private String getSentiment(String comment) {
        int sentimentScore = generateSentimentScore(comment);
        return determineOverallSentiment(sentimentScore);
    }

    private int generateSentimentScore(String comment){
        int sentimentScore = 0;
        boolean negate = false;

        for (String word : comment.split("\\s+")) {
            String lowerCaseWord = word.toLowerCase();
            if (lowerCaseWord.equals("not")) {
                negate = true;
            } else {
                int wordScore = 0;
                if (GOOD_WORDS.contains(lowerCaseWord)) {
                    wordScore = 1;
                } else if (BAD_WORDS.contains(lowerCaseWord)) {
                    wordScore = -1;
                } else if (NEUTRAL_WORDS.contains(lowerCaseWord)) {
                    wordScore = 0;
                }
                if (negate) {
                    wordScore = -wordScore;
                    negate = false;
                }
                sentimentScore += wordScore;
            }
        }
        return sentimentScore;
    }

    private static String determineOverallSentiment(int sentimentScore) {
        if (sentimentScore > 0) {
            return "positive";
        } else if (sentimentScore < 0) {
            return "negative";
        } else {
            return "neutral";
        }
    }

    public Map<Triplet<Integer,Double,String>, List<String>> getLowRatedMenuItems() throws SQLException {
        List<Feedback> feedbackList = feedbackDAO.getLowRatedFeedbacks();
        Map<Triplet<Integer,Double,String>, List<String>> menuFeedbackMap = new HashMap<>();

        for (Feedback feedback : feedbackList) {
            double sentimentScore = analyzeSentiment(feedback.getSentiments());
            if (feedback.getRating() < 2 && sentimentScore < 0) {
                Triplet<Integer,Double,String> key = Triplet.with(feedback.getMenuId(),feedback.getAvgRating(),feedback.getMenuName());
                menuFeedbackMap.computeIfAbsent(key, k -> new ArrayList<>()).add(feedback.getComment());
            }
        }

        return menuFeedbackMap;
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

    public void deleteMenuItem(String menuId) throws SQLException {
        menuDAO.deleteMenu(menuId);
    }
}
