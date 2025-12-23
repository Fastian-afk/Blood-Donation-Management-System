package app.controllers;

import app.database.UserDAO;

/**
 * LoginController - GRASP Controller Pattern
 */

public class LoginController {
    
    private UserDAO userDAO;
    
    public LoginController() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Authenticates a user based on username, password, and role.
     * @return The user_id if successful, null otherwise.
     */
    public String authenticate(String username, String password, String role) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            role == null) {
            System.err.println("❌ Login fields cannot be empty");
            return null;
        }
        
        // Use the new DAO method to get the ID
        return userDAO.findUserIdByCredentials(username, password, role);
    }
}