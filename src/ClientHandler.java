import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import com.google.gson.Gson;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final AuthService authService;
    private final MenuService menuService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.authService = new AuthService();
        this.menuService = new MenuService();
    }

    public void run() {
        System.out.println("Server started...4");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println(request);
                String[] parts = request.split(";", 2);
                String requestType = parts[0];
                String requestData = parts.length > 1 ? parts[1] : "";

                switch (requestType) {
                    case "LOGIN_REQUEST":
                        try {
                            handleLoginRequest(requestData, out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "ADD_MENU_REQUEST":
                        System.out.println("@@@@@@");
                        handleAddMenuRequest(requestData, out);
                        break;
                    case "UPDATE_MENU_REQUEST":
                        handleUpdateMenuRequest(requestData, out);
                        break;
                    case "DELETE_MENU_REQUEST":
                        handleDeleteMenuRequest(requestData, out);
                        break;
                    case "VIEW_MENU_REQUEST":
                        handleViewMenuRequest(out);
                        break;
                    default:
                        out.println("UNKNOWN_REQUEST");
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("tu hain ke nahi");
        }
    }

    private void handleLoginRequest(String requestData, PrintWriter out) {
        String[] parts = requestData.split(";");
        if (parts.length == 2) {
            String employeeId = parts[0];
            String password = parts[1];
            User user = authService.authenticate(employeeId, password);

            if (user != null) {
                out.println("LOGIN_RESPONSE;SUCCESS;Login successful, Role: " + user.getRoleName() + ";" + user.getRoleName());
            } else {
                out.println("LOGIN_RESPONSE;FAILURE;Incorrect EmployeeId/Password");
            }
        } else {
            out.println("LOGIN_RESPONSE;FAILURE;Invalid login request format");
        }
    }

    private void handleAddMenuRequest(String requestData, PrintWriter out) {
        Gson gson = new Gson();
        MenuDTO menu = gson.fromJson(requestData, MenuDTO.class);
        menu.setMenuId(MenuService.generateMenuId());
        menu.setAvailabilityStatus("Yes");
        menu.setScore(new BigDecimal(0));
        boolean success = menuService.addMenu(menu);

        if (success) {
            out.println("ADD_MENU_RESPONSE;SUCCESS;Menu item added successfully");
        } else {
            out.println("ADD_MENU_RESPONSE;FAILURE;Failed to add menu item");
        }
    }

    private void handleUpdateMenuRequest(String requestData, PrintWriter out) {
        Gson gson = new Gson();
        MenuDTO menu = gson.fromJson(requestData, MenuDTO.class);
        boolean success = menuService.updateMenu(menu);

        if (success) {
            out.println("UPDATE_MENU_RESPONSE;SUCCESS;Menu item updated successfully");
        } else {
            out.println("UPDATE_MENU_RESPONSE;FAILURE;Failed to update menu item");
        }
    }

    private void handleDeleteMenuRequest(String requestData, PrintWriter out) {
        boolean success = menuService.deleteMenu(requestData);

        if (success) {
            out.println("DELETE_MENU_RESPONSE;SUCCESS;Menu item deleted successfully");
        } else {
            out.println("DELETE_MENU_RESPONSE;FAILURE;Failed to delete menu item");
        }
    }

    private void handleViewMenuRequest(PrintWriter out) {
        try {
            String menuList = menuService.viewMenu();
            out.println("VIEW_MENU_RESPONSE;" + menuList);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
