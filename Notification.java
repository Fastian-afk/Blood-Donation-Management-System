package app.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification Model Class
 * UPDATED to match bdms_schema.txt (VARCHAR IDs)
 */

public class Notification {
    
    private String notificationId;
    private String userId; // The recipient's user_id
    private String senderId; // The user_id of the sender (e.g., admin)
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    
    /**
     * Constructor for creating a new notification.
     */
    
    public Notification(String userId, String senderId, String message) {
        this.notificationId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.userId = userId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { this.isRead = read; }
}