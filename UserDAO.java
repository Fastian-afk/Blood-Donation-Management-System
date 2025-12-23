package app.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
import java.util.ArrayList; 
import java.util.List;      

/**
 * UserDAO - Data Access Object for the 'users' table.
 * Handles login, user creation, and validation.
 */

public class UserDAO {
    
    private Connection connection;
    
    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    /**
     * Validates a user's login credentials against the 'users' table.
     */
    
    public boolean validateLogin(String username, String password, String role) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); 
            
        } catch (SQLException e) {
            System.err.println("❌ Error validating login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates a new user in the 'users' table.
     * @return The generated user_id if successful, null otherwise.
     */
    
    public String createUser(String username, String password, String role, String email) {
        String userId = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String query = "INSERT INTO users (user_id, username, password, role, email) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            pstmt.setString(5, email);
            
            int result = pstmt.executeUpdate();
            
            return (result > 0) ? userId : null;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks if a username or email already exists.
     */
    
    public boolean userExists(String username, String email) {
        String query = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking if user exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates only the email of a user in the 'users' table.
     */
    
    public boolean updateUserEmail(String userId, String newEmail) {
        String query = "UPDATE users SET email = ?, username = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newEmail);
            pstmt.setString(3, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("❌ Error: Email " + newEmail + " already exists.");
            } else {
                System.err.println("❌ Error updating user email: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Finds a user by username and password.
     * @return The user's ID if found, null otherwise.
     */
    
    public String findUserIdByCredentials(String username, String password, String role) {
        String query = "SELECT user_id FROM users WHERE username = ? AND password = ? AND role = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("user_id");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding user by credentials: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds all user IDs for a specific role.
     */
    
    public List<String> findUserIdsByRole(String role) {
        List<String> userIds = new ArrayList<>();
        String query = "SELECT user_id FROM users WHERE role = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getString("user_id"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding users by role: " + e.getMessage());
            e.printStackTrace();
        }
        return userIds;
    }

    /**
     * Finds all user IDs for Donors with a specific blood group.
     */
    
    public List<String> findUserIdsByBloodGroup(String bloodGroup) {
        List<String> userIds = new ArrayList<>();
        String query = "SELECT u.user_id FROM users u " +
                       "JOIN donors d ON u.user_id = d.user_id " +
                       "WHERE d.blood_group = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, bloodGroup);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getString("user_id"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding users by blood group: " + e.getMessage());
            e.printStackTrace();
        }
        return userIds;
    }

    /**
     * Finds all user IDs belonging to Admin, Staff, or Hospital roles.
     * These are recipients of system-wide notifications.
     */
    
    public List<String> findAdminStaffHospitalUserIds() {
        List<String> userIds = new ArrayList<>();
        String query = "SELECT user_id FROM users WHERE role IN ('Admin', 'Staff', 'Hospital')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getString("user_id"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding Admin/Staff/Hospital users: " + e.getMessage());
            e.printStackTrace();
        }
        return userIds;
    }
}