import com.google.gson.Gson;
import org.javatuples.Triplet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler extends Thread {
    private final BufferedReader in;
    private final PrintWriter out;
    private String employeeId;
    private final AuthService authService;
    private final MenuService menuService;
    private final FeedbackService feedbackService;
    private final ChefRecommendationService chefRecommendationService;
    private final UserSessionService userSessionService;
    private final NotificationService notificationService;
    private final DiscardFeedbackService discardFeedbackService;
    private final ProfileService profileService;
    private final Map<String, Command> commandMap;

    public ClientHandler(Socket socket) throws IOException {
        this.authService = new AuthService();
        this.menuService = new MenuService();
        this.feedbackService = new FeedbackService();
        this.chefRecommendationService = new ChefRecommendationService();
        this.userSessionService = new UserSessionService();
        this.notificationService = new NotificationService();
        this.discardFeedbackService = new DiscardFeedbackService();
        this.profileService = new ProfileService();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.commandMap = new HashMap<>();
        initCommandMap();
    }

    private void initCommandMap() {
        commandMap.put(Constants.LOGIN_REQUEST, this::handleLoginRequest);
        commandMap.put(Constants.ADD_MENU_REQUEST, this::handleAddMenuRequest);
        commandMap.put(Constants.UPDATE_MENU_REQUEST, this::handleUpdateMenuRequest);
        commandMap.put(Constants.DELETE_MENU_REQUEST, this::handleDeleteMenuRequest);
        commandMap.put(Constants.VIEW_MENU_REQUEST, this::handleViewMenuRequest);
        commandMap.put(Constants.VIEW_TOP_RECOMMENDATIONS, this::handleViewTopRecommendationsRequest);
        commandMap.put(Constants.VIEW_CHEF_RECOMMENDATIONS, this::handleViewChefRecommendationsRequest);
        commandMap.put(Constants.VOTE_RECOMMENDATION_REQUEST, this::handleVoteRecommendationRequest);
        commandMap.put(Constants.VIEW_VOTED_REPORT, this::handleViewVotedReportRequest);
        commandMap.put(Constants.ROLLOUT_NEXT_DAY_MENU_REQUEST, this::handleRollOutNextDayMenuRequest);
        commandMap.put(Constants.GIVE_FEEDBACK_REQUEST, this::handleGiveFeedbackRequest);
        commandMap.put(Constants.USER_SESSION_REQUEST, this::handleUserSessionRequest);
        commandMap.put(Constants.VIEW_NOTIFICATIONS_REQUEST, this::handleViewNotificationsRequest);
        commandMap.put(Constants.DISCARD_MENU_REQUEST, this::handleDiscardMenuRequest);
        commandMap.put(Constants.DISCARD_FEEDBACK_DETAILS_REQUEST_FOR_EMPLOYEE, this::handleDiscardFeedbackEmployeeActions);
        commandMap.put(Constants.UPDATE_PROFILE_REQUEST, this::handleUpdateProfileRequest);

    }

    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println(request);
                String[] parts = request.split(";", 2);
                String requestType = parts[0];
                String requestData = parts.length > 1 ? parts[1] : "";
                Command command = commandMap.get(requestType);

                if (command != null) {
                    command.execute(requestData);
                } else {
                    out.println("UNKNOWN_REQUEST");
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Executed Finally");
        }
    }

    private void handleLoginRequest(String requestData) {
        String[] parts = requestData.split(";");
        if (parts.length == 2) {
            employeeId = parts[0];
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

    private void handleAddMenuRequest(String requestData) {
        Gson gson = new Gson();
        Menu menu = gson.fromJson(requestData, Menu.class);
        menu.setAvailabilityStatus("Yes");
        menu.setScore(new BigDecimal(0));
        boolean success = menuService.addMenu(menu);

        if (success) {
            out.println("ADD_MENU_RESPONSE;SUCCESS;Menu item added successfully");
        } else {
            out.println("ADD_MENU_RESPONSE;FAILURE;Failed to add menu item");
        }
    }

    private void handleUpdateMenuRequest(String requestData) {
        Gson gson = new Gson();
        Menu menu = gson.fromJson(requestData, Menu.class);
        boolean success = menuService.updateMenu(menu);

        if (success) {
            out.println("UPDATE_MENU_RESPONSE;SUCCESS;Menu item updated successfully");
        } else {
            out.println("UPDATE_MENU_RESPONSE;FAILURE;Failed to update menu item");
        }
    }

    private void handleDeleteMenuRequest(String requestData) {
        boolean success = menuService.deleteMenu(requestData);

        if (success) {
            out.println("DELETE_MENU_RESPONSE;SUCCESS;Menu item deleted successfully");
        } else {
            out.println("DELETE_MENU_RESPONSE;FAILURE;Failed to delete menu item");
        }
    }

    private void handleViewMenuRequest(String requestData) {
        try {
            String menuList = menuService.viewMenu();
            out.println("VIEW_MENU_RESPONSE;" + menuList);
        } catch (Exception e) {
            out.println("VIEW_MENU_RESPONSE;FAILURE;Error retrieving menu");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleViewTopRecommendationsRequest(String requestData) {
        feedbackService.updateMenuScoresAccToFeedback();
        String recommendations = menuService.viewTopRecommendations(requestData);
        out.println(recommendations);
    }

    private void handleViewChefRecommendationsRequest(String requestData) {
        try {
            List<ChefRecommendation> recommendations = chefRecommendationService.getChefRecommendations(employeeId);
            StringBuilder response = new StringBuilder("VIEW_RECOMMENDATIONS_RESPONSE");
            for (ChefRecommendation recommendation : recommendations) {
                response.append(";")
                        .append("MenuId: ").append(recommendation.getMenuId())
                        .append(", MenuName: ").append(recommendation.getMenuName())
                        .append(", Score: ").append(recommendation.getVoteCount());
            }
            out.println(response);
        } catch (Exception e) {
            out.println("VIEW_RECOMMENDATIONS_RESPONSE;FAILURE;Error retrieving recommendations");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleVoteRecommendationRequest(String requestData) {
        try {
            String[] parts = requestData.split(";");
            String[] menuIds = parts[0].split(",");
            String employeeId = parts[1];
            chefRecommendationService.voteForRecommendations(menuIds, employeeId);
            out.println("VOTE_RECOMMENDATION_RESPONSE;SUCCESS");
        } catch (VoteAlreadyGivenException | SQLException e) {
            out.println("VOTE_RECOMMENDATION_RESPONSE;FAILURE;" + e.getMessage());
        }
    }

    private void handleViewVotedReportRequest(String requestData) {
        try {
            List<ChefRecommendation> recommendations = chefRecommendationService.getVotedChefRecommendations();
            StringBuilder response = new StringBuilder("VOTED_REPORT");
            for (ChefRecommendation recommendation : recommendations) {
                response.append(";")
                        .append("RecId: ").append(recommendation.getRecId())
                        .append(", MenuId: ").append(recommendation.getMenuId())
                        .append(", VoteCount: ").append(recommendation.getVoteCount())
                        .append(", CreatedDate: ").append(recommendation.getCreatedDate());
            }
            out.println(response);
        } catch (Exception e) {
            out.println("VOTED_REPORT;FAILURE;Error retrieving voted report");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleRollOutNextDayMenuRequest(String requestData) {
        String[] parts = requestData.split(",");
        boolean success = chefRecommendationService.rollOutNextDayMenu(parts);
        if (success) {
            out.println("ROLLOUT_NEXT_DAY_MENU_RESPONSE;SUCCESS");
        } else {
            out.println("ROLLOUT_NEXT_DAY_MENU_RESPONSE;FAILURE;Error rolling out next day menu");
        }
    }

    private void handleGiveFeedbackRequest(String requestData) {
        try {
            Gson gson = new Gson();
            Feedback feedback = gson.fromJson(requestData, Feedback.class);
            String employeeId = feedback.getEmployeeId();
            Integer menuId = feedback.getMenuId();
            String comment = feedback.getComment();
            int rating = feedback.getRating();

            feedbackService.submitFeedback(employeeId, menuId, comment, rating);
            out.println("GIVE_FEEDBACK_RESPONSE;SUCCESS");
        } catch (FeedbackAlreadyExistsException e) {
            out.println("GIVE_FEEDBACK_RESPONSE;FAILURE;" + e.getMessage());
        } catch (Exception e) {
            out.println("GIVE_FEEDBACK_RESPONSE;FAILURE;Error submitting feedback");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleUserSessionRequest(String requestData) {
        Gson gson = new Gson();
        UserSession sessionDTO = gson.fromJson(requestData, UserSession.class);

        try {
            userSessionService.logUserSession(sessionDTO);
            System.out.println("USER_SESSION_RESPONSE;SUCCESS");
        } catch (SQLException e) {
            out.println("USER_SESSION_RESPONSE;FAILURE;Error logging user session");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleViewNotificationsRequest(String requestData) {
        try {
            List<Notification> notifications = notificationService.getTodayNotifications();

            StringBuilder response = new StringBuilder("VIEW_NOTIFICATIONS_RESPONSE");
            for (Notification notification : notifications) {
                String message = notification.getMessage();
                response.append(";").append(message);
            }
            out.println(response);
        } catch (SQLException e) {
            out.println("VIEW_NOTIFICATIONS_RESPONSE;FAILURE;Error retrieving notifications");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleDiscardMenuRequest(String requestData) {
        try {
            Map<Triplet<Integer, Double, String>, List<String>> lowRatedMenuItems = feedbackService.getLowRatedMenuItems();
            sendLowRatedMenuItemsResponse(lowRatedMenuItems);

            String selectedMenuItem = in.readLine();
            if (checkSelectedItemPresent(lowRatedMenuItems.keySet(), selectedMenuItem)) {
                out.println("DISCARD_MENU_ACTION;VALID");
                processUserAction(selectedMenuItem);
            } else {
                out.println("DISCARD_MENU_ACTION;INVALID;Wrong Menu Id");
            }
        } catch (Exception e) {
            out.println("DISCARD_MENU_RESPONSE;FAILURE;Error discarding menu item");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private boolean checkSelectedItemPresent(Set<Triplet<Integer, Double, String>> keys, String selectedMenuItem) {
        return keys.stream().anyMatch(key -> key.contains(Integer.parseInt(selectedMenuItem)));
    }

    private void sendLowRatedMenuItemsResponse(Map<Triplet<Integer, Double, String>, List<String>> lowRatedMenuItems) {
        StringBuilder response = new StringBuilder("DISCARD_MENU_RESPONSE");
        for (Map.Entry<Triplet<Integer, Double, String>, List<String>> entry : lowRatedMenuItems.entrySet()) {
            response.append(";").append(entry.getKey());
            for (String comment : entry.getValue()) {
                response.append(";").append(comment);
            }
        }
        out.println(response);
    }

    private void processUserAction(String selectedMenuItem) {
        try {
            String action = in.readLine();
            if ("1".equals(action)) {
                feedbackService.deleteMenuItem(selectedMenuItem);
                out.println("DISCARD_MENU_RESULT;SUCCESS;Item removed successfully");
            } else if ("2".equals(action)) {
                String discardFeedbackResponse = in.readLine();
                String[] parts = discardFeedbackResponse.split(";");
                if (parts[0].equals("DISCARD_FEEDBACK_DETAILS_REQUEST_FOR_ADMIN_CHEF")) {
                    String menuId = parts[1];
                    String questions =
                            "Q1. What you not like about " + menuId + "?;" +
                                    "Q2. How would you like " + menuId + " to taste?;" +
                                    "Q3. Share your mom’s recipe";
                    discardFeedbackService.submitDiscardFeedback(questions, menuId);
                }
            }
        } catch (IOException | SQLException e) {
            out.println("DISCARD_MENU_RESULT;FAILURE;Error processing user action");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleDiscardFeedbackEmployeeActions(String requestData) {
        try {
            Map<Integer, String> menuIdwithQuestionMap = discardFeedbackService.getTodayFeedbackDetails();
            String response = menuIdwithQuestionMap.keySet().stream()
                    .map(key -> key + "=" + menuIdwithQuestionMap.get(key))
                    .collect(Collectors.joining(", ", "{", "}"));
            out.println(response);
        } catch (Exception e) {
            out.println("DISCARD_FEEDBACK_DETAILS_RESPONSE_FOR_EMPLOYEE;FAILURE;Error retrieving feedback details");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleUpdateProfileRequest(String requestData) {
        try {
            Gson gson = new Gson();
            Profile profile = gson.fromJson(requestData, Profile.class);
            boolean success = profileService.updateOrCreateProfile(profile);
            if (success) {
                out.println("UPDATE_PROFILE_RESPONSE;SUCCESS;Profile added successfully");
            } else {
                out.println("UPDATE_PROFILE_RESPONSE;FAILURE;Failed to Update Profile");
            }
        }catch(SQLException e){
            out.println("UPDATE_PROFILE_RESPONSE;FAILURE;Error Retrieving your Profile");
        }
    }
}
