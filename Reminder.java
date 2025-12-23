package app.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reminder Model - Represents an entry in the 'reminders' table (SD2 Compliance).
 */

public class Reminder {
    private String reminderId;
    private String donorId;
    private String appointmentId;
    private String message;
    private LocalDateTime scheduledTime;
    private boolean sent;

    public Reminder() {
        this.reminderId = "REM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.sent = false;
    }

    public Reminder(String donorId, String appointmentId, String message, LocalDateTime scheduledTime) {
        this();
        this.donorId = donorId;
        this.appointmentId = appointmentId;
        this.message = message;
        this.scheduledTime = scheduledTime;
    }

    // Getters and Setters
    public String getReminderId() { return reminderId; }
    public void setReminderId(String reminderId) { this.reminderId = reminderId; }
    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }
}