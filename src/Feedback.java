import java.sql.Timestamp;

public class Feedback {
    private String employeeId;
    private int menuId;
    private String comment;
    private int rating;
    private String sentiments;
    private Timestamp createdDate;

    private String menuName;

    private Double avgRating;

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setSentiments(String sentiments) {
        this.sentiments = sentiments;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getSentiments() {
        return sentiments;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public String getMenuName() {
        return menuName;
    }

    public Double getAvgRating() {
        return avgRating;
    }
}
