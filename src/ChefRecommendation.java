import java.math.BigDecimal;
import java.sql.Timestamp;

public class ChefRecommendation {
    private int recId;
    private int menuId;
    private String menuName;

    private BigDecimal score;
    private int voteCount;
    private Timestamp createdDate;

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public int getMenuId() {
        return menuId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

}
