import java.math.BigDecimal;

public class MenuDTO {

    private String Name;
    private BigDecimal menuId;
    private BigDecimal price;
    private String availabilityStatus;
    private String mealType;
    private BigDecimal score;

    public void setMenuId(BigDecimal menuId) {
        this.menuId = menuId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public void setName(String name) {
        Name = name;
    }

    public BigDecimal getMenuId() {
        return menuId;
    }

    public BigDecimal getPrice() {
        return price;
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
    public String getName() {
        return Name;
    }
}