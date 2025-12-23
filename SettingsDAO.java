package app.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * SettingsDAO - Data Access Object for the 'system_settings' table.
 */

public class SettingsDAO {

    private Connection connection;

    public SettingsDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Retrieves all settings from the database.
     * @return A Map<String, String> of all settings.
     */
    
    public Map<String, String> getAllSettings() {
        Map<String, String> settings = new HashMap<>();
        String query = "SELECT setting_key, setting_value FROM system_settings";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                settings.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading system settings: " + e.getMessage());
            e.printStackTrace();
        }
        return settings;
    }

    /**
     * Updates a specific setting in the database.
     * @param key The 'setting_key' to update.
     * @param value The new 'setting_value'.
     * @return true if successful, false otherwise.
     */
    
    public boolean updateSetting(String key, String value) {
        // Use INSERT ... ON DUPLICATE KEY UPDATE to handle both new and existing keys
        String query = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) " +
                       "ON DUPLICATE KEY UPDATE setting_value = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setString(3, value); 
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating setting: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}