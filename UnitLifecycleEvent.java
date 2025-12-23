package app.models;

/**
 * UnitLifecycleEvent - Data Transfer Object (DTO) for visualizing the history of a unit (UC10).
 */

public class UnitLifecycleEvent {
    private String timestamp;
    private String description;
    private String actionBy;

    public UnitLifecycleEvent(String timestamp, String description, String actionBy) {
        this.timestamp = timestamp;
        this.description = description;
        this.actionBy = actionBy;
    }

    // Getters
    public String getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public String getActionBy() { return actionBy; }
    
    // Setters (Included for general utility)
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setDescription(String description) { this.description = description; }
    public void setActionBy(String actionBy) { this.actionBy = actionBy; }
}