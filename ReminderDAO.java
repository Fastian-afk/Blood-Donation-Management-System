package app.database;

import app.models.Reminder;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ReminderDAO - Handles persistence for the reminders table (SD2 compliance).
 */

public class ReminderDAO {

    private Connection connection;

    public ReminderDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Saves a new reminder to the database.
     */
    
    public boolean saveReminder(Reminder reminder) {
        String query = "INSERT INTO reminders (reminder_id, donor_id, appointment_id, message, scheduled_time, sent) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reminder.getReminderId());
            pstmt.setString(2, reminder.getDonorId());
            pstmt.setString(3, reminder.getAppointmentId());
            pstmt.setString(4, reminder.getMessage());
            pstmt.setTimestamp(5, Timestamp.valueOf(reminder.getScheduledTime()));
            pstmt.setBoolean(6, reminder.isSent());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all unsent reminders for a donor.
     */
    
    public List<Reminder> getUnsentRemindersByDonor(String donorId) {
        List<Reminder> reminders = new ArrayList<>();
        String query = "SELECT * FROM reminders WHERE donor_id = ? AND sent = FALSE ORDER BY scheduled_time ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reminders.add(extractReminderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reminders;
    }

    private Reminder extractReminderFromResultSet(ResultSet rs) throws SQLException {
        Reminder reminder = new Reminder();
        reminder.setReminderId(rs.getString("reminder_id"));
        reminder.setDonorId(rs.getString("donor_id"));
        reminder.setAppointmentId(rs.getString("appointment_id"));
        reminder.setMessage(rs.getString("message"));
        reminder.setScheduledTime(rs.getTimestamp("scheduled_time").toLocalDateTime());
        reminder.setSent(rs.getBoolean("sent"));
        return reminder;
    }
}