package app.controllers;

import app.database.SettingsDAO;
import java.util.Map;

/**
 * SettingsController - GRASP Controller Pattern
 * Handles business logic for updating system settings.
 */

public class SettingsController {

    private SettingsDAO settingsDAO;

    public SettingsController() {
        this.settingsDAO = new SettingsDAO();
    }

    /**
     * Fetches all current settings.
     * @return A map of setting keys and values.
     */
    
    public Map<String, String> getSettings() {
        return settingsDAO.getAllSettings();
    }

    /**
     * Updates a single setting.
     * @param key The key of the setting (e.g., "min_age").
     * @param value The new value for the setting.
     * @return true if the update was successful.
     */
    
    public boolean updateSetting(String key, String value) {
        if (key == null || key.trim().isEmpty() || value == null) {
            System.err.println("❌ Setting key or value cannot be null");
            return false;
        }
        
        System.out.println("🔧 Updating Setting: " + key + " = " + value);
        return settingsDAO.updateSetting(key, value);
    }
}