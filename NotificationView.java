package app.ui;

import app.database.NotificationDAO;
import app.database.ReminderDAO;
import app.database.DonorDAO;
import app.models.Donor;
import app.models.Notification;
import app.models.Reminder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import app.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * NotificationView - FINAL VERSION
 * Loads REAL Broadcasts from 'notifications' table for everyone.
 * Loads REAL Reminders from 'reminders' table for Donors.
 * "Clear All" permanently deletes messages from the DB.
 */

public class NotificationView {
    private Stage stage;
    private ReminderDAO reminderDAO;
    private DonorDAO donorDAO;
    private NotificationDAO notificationDAO;
    private ObservableList<String> notificationList;
    
    public NotificationView(Stage stage) {
        this.stage = stage;
        this.reminderDAO = new ReminderDAO();
        this.donorDAO = new DonorDAO();
        this.notificationDAO = new NotificationDAO();
        this.notificationList = FXCollections.observableArrayList();
        loadNotifications();
    }
    
    private void loadNotifications() {
        notificationList.clear();
        String currentUserId = LoginHelper.getCurrentUserId();
        String currentUsername = LoginHelper.getCurrentUsername();
        String currentRole = LoginHelper.getCurrentRole();
        
        if (currentUserId == null) return;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

        // 1. LOAD SYSTEM NOTIFICATIONS (Broadcasts) - For ALL Roles
        List<Notification> systemNotifs = notificationDAO.getNotificationsByUserId(currentUserId);
        for (Notification n : systemNotifs) {
            String timeStr = n.getCreatedAt().format(dtf);
            // Format: Time | Message
            notificationList.add(timeStr + " | " + n.getMessage());
        }

        // 2. LOAD REMINDERS - For Donors Only
        if ("Donor".equals(currentRole)) {
            // We need to look up the Donor ID first using the helper
            // Note: In a perfect world we'd store donor_id in session, but looking it up is fine
            app.database.DonationDAO helperDAO = new app.database.DonationDAO();
            String donorId = helperDAO.findDonorIdByUserId(currentUserId);
            
            if (donorId != null) {
                List<Reminder> reminders = reminderDAO.getUnsentRemindersByDonor(donorId);
                for (Reminder rem : reminders) {
                    String status = rem.getScheduledTime().isBefore(LocalDateTime.now()) ? "DUE NOW" : "Scheduled";
                    notificationList.add(status + " | 🔔 Reminder: " + rem.getMessage() + " (" + rem.getScheduledTime().format(dtf) + ")");
                }
            }
        }

        if (notificationList.isEmpty()) {
            notificationList.add("System | No new notifications.");
        }
    }
    
    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("🔔 Notifications");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox notificationsList = new VBox(10);
        
        for (String msg : notificationList) {
            notificationsList.getChildren().add(createNotificationItem(msg));
        }
        
        Button clearBtn = new Button("🗑️ Clear All");
        clearBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-background-radius: 5; -fx-cursor: hand;");
        
        clearBtn.setOnAction(e -> {
            String currentUserId = LoginHelper.getCurrentUserId();
            if (currentUserId != null) {
                // Delete from DB
                notificationDAO.clearNotificationsForUser(currentUserId);
                // Clear UI
                notificationList.clear();
                notificationList.add("System | No new notifications.");
                
                // Refresh the list view
                notificationsList.getChildren().clear();
                notificationsList.getChildren().add(createNotificationItem("System | No new notifications."));
                
                Main.showAlert("Success", "All notifications cleared.", Alert.AlertType.INFORMATION);
            }
        });
        
        root.getChildren().addAll(titleLabel, new Separator(), notificationsList, clearBtn);
        
        ScrollPane scroll = new ScrollPane(root);
        scroll.setStyle("-fx-background-color: #f5f7fa;");
        stage.setScene(new javafx.scene.Scene(scroll, 500, 400));
    }
    
    private VBox createNotificationItem(String fullMessage) {
        VBox item = new VBox(5);
        item.setPadding(new Insets(10));
        
        String timePart = "System";
        String messagePart = fullMessage;

        String[] parts = fullMessage.split("\\|", 2);
        if (parts.length > 1) {
            timePart = parts[0].trim();
            messagePart = parts[1].trim();
        }

        // Style Logic
        String styleColor = "#f0f0f0"; 
        String styleBorder = "#dddddd";
        
        // Highlight logic
        boolean isUrgent = messagePart.toUpperCase().contains("URGENT") || timePart.equals("DUE NOW");
        
        if (isUrgent) {
            styleColor = "#f8d7da"; // Red background
            styleBorder = "#dc3545"; 
        } else if (messagePart.contains("System")) {
            styleColor = "#d1ecf1"; // Blue background
            styleBorder = "#bee5eb";
        }

        item.setStyle("-fx-background-color: " + styleColor + "; -fx-border-color: " + styleBorder + "; -fx-border-radius: 5;");
        
        Label msgLabel = new Label(messagePart);
        msgLabel.setWrapText(true);
        // Fixed font weight string to avoid CSS warnings
        String fontWeight = isUrgent ? "bold" : "normal";
        msgLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: " + fontWeight + ";");
        
        Label timeLabel = new Label(timePart);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        
        item.getChildren().addAll(msgLabel, timeLabel);
        return item;
    }
}