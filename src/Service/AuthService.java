package Service;

import DAO.UserDAO;
import DTO.User;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User authenticate(String employeeId, String password) {
        return userDAO.getUserByEmployeeIdAndPassword(employeeId, password);
    }
}
