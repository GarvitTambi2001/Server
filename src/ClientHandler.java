import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final AuthService authService;
    private final MenuService menuService;
    private final FeedbackService feedbackService;
    private final ChefRecommendationService chefRecommendationService;
    private final UserSessionService userSessionService;

    private final NotificationService notificationService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.authService = new AuthService();
        this.menuService = new MenuService();
        this.feedbackService = new FeedbackService();
        this.chefRecommendationService = new ChefRecommendationService();
        this.userSessionService = new UserSessionService();
        this.notificationService = new NotificationService();
    }

    public void run() {

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
                        handleLoginRequest(requestData, out);
                        break;
                    case "ADD_MENU_REQUEST":
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
                    case "VIEW_TOP_RECOMMENDATIONS":
                        handleViewTopRecommendationsRequest(requestData,out);
                        break;
                    case "VIEW_CHEF_RECOMMENDATIONS":
                        handleViewChefRecommendationsRequest(out);
                        break;
                    case "VOTE_RECOMMENDATION_REQUEST":
                        handleVoteRecommendationRequest(requestData, out);
                        break;
                    case "VIEW_VOTED_REPORT":
                        handleViewVotedReportRequest(out);
                        break;
                    case "ROLLOUT_NEXT_DAY_MENU_REQUEST":
                        handleRollOutNextDayMenuRequest(parts, out);
                        break;
                    case "GIVE_FEEDBACK_REQUEST":
                        handleGiveFeedbackRequest(parts[1], out);
                        break;
                    case "USER_SESSION_REQUEST":
                        handleUserSessionRequest(parts[1]);
                        break;
                    case "VIEW_NOTIFICATIONS_REQUEST":
                        handleViewNotificationsRequest(out);
                    default:
                        out.println("UNKNOWN_REQUEST");
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Executed Finally");
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

    private void handleViewTopRecommendationsRequest(String requestData, PrintWriter out) {
        feedbackService.updateMenuScoresAccToFeedback();
        String recommendations = menuService.viewTopRecommendations(requestData);
        out.println(recommendations);
    }

    private void handleViewChefRecommendationsRequest(PrintWriter out) {
        try {
            List<ChefRecommendationDTO> recommendations = chefRecommendationService.getChefRecommendations();
            StringBuilder response = new StringBuilder("VIEW_RECOMMENDATIONS_RESPONSE");
            for (ChefRecommendationDTO recommendation : recommendations) {
                response.append(";")
                        .append("MenuId: ").append(recommendation.getMenuId())
                        .append(", MenuName: ").append(recommendation.getMenuName())
                        .append(", Score: ").append(recommendation.getVoteCount());
            }
            out.println(response);
        }catch(Exception error){
            System.err.println(error.getMessage());
        }
    }

    private void handleVoteRecommendationRequest(String requestData, PrintWriter out) {
        try {
            String[] parts = requestData.split(";");
            String[] menuIds = parts[0].split(",");
            String employeeId = parts[1];
            chefRecommendationService.voteForRecommendations(menuIds, employeeId);
            out.println("VOTE_RECOMMENDATION_RESPONSE;SUCCESS");
        }catch (VoteAlreadyGivenException e) {
            out.println("VOTE_RECOMMENDATION_RESPONSE;FAILURE;" + e.getMessage());
        }catch (SQLException e) {
            out.println("VOTE_RECOMMENDATION_RESPONSE;FAILURE" + e.getMessage());
        }
    }

    private void handleViewVotedReportRequest(PrintWriter out) {
        List<ChefRecommendationDTO> recommendations = chefRecommendationService.getTodayChefRecommendations();
        StringBuilder response = new StringBuilder("VOTED_REPORT");
        for (ChefRecommendationDTO recommendation : recommendations) {
            response.append(";")
                    .append("RecId: ").append(recommendation.getRecId())
                    .append(", MenuId: ").append(recommendation.getMenuId())
                    .append(", VoteCount: ").append(recommendation.getVoteCount())
                    .append(", CreatedDate: ").append(recommendation.getCreatedDate());
        }
        out.println(response);
    }

    private void handleRollOutNextDayMenuRequest(String[] parts, PrintWriter out) {
        String[] menuIds = parts[1].split(",");
        boolean success = chefRecommendationService.rollOutNextDayMenu(menuIds);
        if (success) {
            out.println("ROLLOUT_NEXT_DAY_MENU_RESPONSE;SUCCESS");
        } else {
            out.println("ROLLOUT_NEXT_DAY_MENU_RESPONSE;FAILURE");
        }
    }

    private void handleGiveFeedbackRequest(String jsonFeedback, PrintWriter out) {
        try {
            Gson gson = new Gson();
            FeedbackDTO feedbackDTO = gson.fromJson(jsonFeedback, FeedbackDTO.class);
            String employeeId = feedbackDTO.getEmployeeId();
            Integer menuId = feedbackDTO.getMenuId();
            String comment = feedbackDTO.getComment();
            int rating = feedbackDTO.getRating();

            feedbackService.submitFeedback(employeeId, menuId, comment, rating);
            out.println("GIVE_FEEDBACK_RESPONSE;SUCCESS");
        }catch (FeedbackAlreadyExistsException e) {
            out.println("GIVE_FEEDBACK_RESPONSE;FAILURE;" + e.getMessage());
        }catch (Exception e) {
            System.err.println();
            out.println("GIVE_FEEDBACK_RESPONSE;FAILURE" + e.getMessage());
        }
      }

    private void handleUserSessionRequest(String data) {
        Gson gson = new Gson();
        UserSessionDTO sessionDTO = gson.fromJson(data, UserSessionDTO.class);

        try {
            userSessionService.logUserSession(sessionDTO);
            System.out.println("User Session Successfully recorded");
        } catch (SQLException e) {
            System.err.println("User Session Successfully recorded");
        }
    }

    private void handleViewNotificationsRequest(PrintWriter out) {
        try {
            List<NotificationDTO> notifications = notificationService.getTodayNotifications();

            StringBuilder response = new StringBuilder("VIEW_NOTIFICATIONS_RESPONSE");
            for (NotificationDTO notification : notifications) {
                String message = notification.getMessage();
                response.append(";").append(message);
            }
            out.println(response);
        } catch (SQLException e) {
            out.println("VIEW_NOTIFICATIONS_RESPONSE;FAILURE");
        }
    }
}
