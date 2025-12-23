package app.database;

import app.models.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationDAO - FINAL VERSION
 * Adds fetching and clearing logic for dynamic notifications.
 */

public class NotificationDAO {

    private Connection connection;

    public NotificationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean saveBatchNotifications(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) return true;
        
        String query = "INSERT INTO notifications (notification_id, user_id, sender_id, message, created_at, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (Notification notification : notifications) {
                pstmt.setString(1, notification.getNotificationId());
                pstmt.setString(2, notification.getUserId());
                pstmt.setString(3, notification.getSenderId());
                pstmt.setString(4, notification.getMessage());
                pstmt.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));
                pstmt.setBoolean(6, notification.isRead());
                pstmt.addBatch();
            }
            int[] results = pstmt.executeBatch();
            return results.length > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all notifications for a specific user (Recent first).
     */
    
    public List<Notification> getNotificationsByUserId(String userId) {
        List<Notification> list = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Notification n = new Notification(
                    rs.getString("user_id"),
                    rs.getString("sender_id"),
                    rs.getString("message")
                );
                n.setNotificationId(rs.getString("notification_id"));
                n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                n.setRead(rs.getBoolean("is_read"));
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Deletes all notifications for a user (Clear All function).
     */
    
    public boolean clearNotificationsForUser(String userId) {
        String query = "DELETE FROM notifications WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}