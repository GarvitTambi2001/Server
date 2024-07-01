import java.util.List;
import com.google.gson.Gson;

public class MenuService {
    private final MenuDAO menuDAO;

    public MenuService() {
        this.menuDAO = new MenuDAO();
    }

    public boolean addMenu(Menu menu) {
        return menuDAO.addMenu(menu);
    }

    public boolean updateMenu(Menu menu) {
        return menuDAO.updateMenu(menu);
    }

    public boolean deleteMenu(String menuId) {
        return menuDAO.deleteMenu(menuId);
    }

    public String viewMenu() {
        List<Menu> menus = menuDAO.getAllMenus();
        Gson gson = new Gson();
        return gson.toJson(menus);
    }

    public String viewTopRecommendations(String numberOfItems) {
        List<Menu> topRecommendations = menuDAO.getTopRecommendations(numberOfItems);
        Gson gson = new Gson();
        return gson.toJson(topRecommendations);
    }
}
