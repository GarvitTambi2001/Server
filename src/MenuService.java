import java.util.List;
import java.util.UUID;
import com.google.gson.Gson;

public class MenuService {
    private final MenuDAO menuDAO;

    public MenuService() {
        this.menuDAO = new MenuDAO();
    }

    public boolean addMenu(MenuDTO menu) {
        return menuDAO.addMenu(menu);
    }

    public boolean updateMenu(MenuDTO menu) {
        return menuDAO.updateMenu(menu);
    }

    public boolean deleteMenu(String menuId) {
        return menuDAO.deleteMenu(menuId);
    }

    public String viewMenu() {
        List<MenuDTO> menus = menuDAO.getAllMenus();
        Gson gson = new Gson();
        return gson.toJson(menus);
    }

    public static String generateMenuId() {
        return UUID.randomUUID().toString();
    }
}
