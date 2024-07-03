import java.math.BigDecimal;

public class Menu {

    private String name;
    private BigDecimal menuId;
    private BigDecimal price;
    private String availabilityStatus;
    private String mealType;
    private BigDecimal score;
    private String diet;
    private String spice;
    private String cuisineType;
    private String sweetTooth;

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
        this.name = name;
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
        return name;
    }

    // New getters and setters
    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getSpice() {
        return spice;
    }

    public void setSpice(String spice) {
        this.spice = spice;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getSweetTooth() {
        return sweetTooth;
    }

    public void setSweetTooth(String sweetTooth) {
        this.sweetTooth = sweetTooth;
    }
}
