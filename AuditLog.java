package app.models;

import java.time.LocalDateTime;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * AuditLog Model Class
 * Represents a record from the 'audit_logs' table.
 * Uses JavaFX Properties for easy TableView binding.
 */

public class AuditLog {

    private final LongProperty logId;
    private final StringProperty userId;
    private final StringProperty actionType;
    private final StringProperty description;
    private final StringProperty timestamp; // Using String for simple display

    public AuditLog(long logId, String userId, String actionType, String description, LocalDateTime timestamp) {
        this.logId = new SimpleLongProperty(logId);
        this.userId = new SimpleStringProperty(userId);
        this.actionType = new SimpleStringProperty(actionType);
        this.description = new SimpleStringProperty(description);
        this.timestamp = new SimpleStringProperty(timestamp.toString());
    }
    
    // Getters
    public long getLogId() { return logId.get(); }
    public String getUserId() { return userId.get(); }
    public String getActionType() { return actionType.get(); }
    public String getDescription() { return description.get(); }
    public String getTimestamp() { return timestamp.get(); }

    // Property Getters
    public LongProperty logIdProperty() { return logId; }
    public StringProperty userIdProperty() { return userId; }
    public StringProperty actionTypeProperty() { return actionType; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty timestampProperty() { return timestamp; }
}