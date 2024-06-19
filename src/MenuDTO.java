import java.math.BigDecimal;

public class MenuDTO {
    private String menuId;
    private BigDecimal prices;
    private String availabilityStatus;
    private String mealType;
    private BigDecimal score;

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public void setPrice(BigDecimal prices) {
        this.prices = prices;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getMenuId() {
        return menuId;
    }

    public BigDecimal getPrice() {
        return prices;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getMealType() {
        return mealType;
    }

    public BigDecimal getScore() {
        return score;
    }
}