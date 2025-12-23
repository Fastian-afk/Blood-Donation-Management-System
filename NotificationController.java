package app.controllers;

import app.database.NotificationDAO;
import app.database.UserDAO;
import app.models.Notification;
import app.ui.LoginHelper; // To get the sender's ID
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationController - GRASP Controller Pattern
 * Handles business logic for sending notifications.
 */

public class NotificationController {

    private NotificationDAO notificationDAO;
    private UserDAO userDAO;

    public NotificationController() {
        this.notificationDAO = new NotificationDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Sends a broadcast message to a target group.
     * * @param targetGroup The audience (e.g., "All Donors", "All Staff", "O- Donors").
     * @param message The message to send.
     * @return true if the broadcast was successfully saved to the DB.
     */
    
    public boolean sendBroadcast(String targetGroup, String message) {
        if (targetGroup == null || message == null || message.trim().isEmpty()) {
            System.err.println("❌ Target group and message cannot be empty.");
            return false;
        }

        String senderId = LoginHelper.getCurrentUserId();
        if (senderId == null) {
            System.err.println("❌ Error: No sender is logged in.");
            return false;
        }
        
        List<String> recipientUserIds = new ArrayList<>();

        // 1. Get the list of recipients
        switch (targetGroup) {
            case "All Donors":
                recipientUserIds = userDAO.findUserIdsByRole("Donor");
                break;
            case "All Staff":
                recipientUserIds = userDAO.findUserIdsByRole("Staff");
                break;
            case "All Hospitals":
                recipientUserIds = userDAO.findUserIdsByRole("Hospital");
                break;
            // Handle specific blood types
            case "O+ Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("O+"); break;
            case "O- Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("O-"); break;
            case "A+ Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("A+"); break;
            case "A- Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("A-"); break;
            case "B+ Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("B+"); break;
            case "B- Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("B-"); break;
            case "AB+ Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("AB+"); break;
            case "AB- Donors": recipientUserIds = userDAO.findUserIdsByBloodGroup("AB-"); break;
            default:
                System.err.println("❌ Unknown target group: " + targetGroup);
                return false;
        }

        if (recipientUserIds.isEmpty()) {
            System.out.println("ℹ️ No recipients found for target group: " + targetGroup);
            return true; // Technically successful, just no one to send to.
        }

        // 2. Create Notification objects for all recipients
        List<Notification> notifications = new ArrayList<>();
        for (String userId : recipientUserIds) {
            notifications.add(new Notification(userId, senderId, message));
        }

        // 3. Save the batch to the database
        return notificationDAO.saveBatchNotifications(notifications);
    }
}