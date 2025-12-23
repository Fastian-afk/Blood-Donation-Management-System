package app.database;

import app.models.AuditLog;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AuditLogDAO - Data Access Object for the 'audit_logs' table.
 */

public class AuditLogDAO {
    
    private Connection connection;

    public AuditLogDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Retrieves all audit logs from the database.
     * @return A list of AuditLog objects.
     */
    
    public List<AuditLog> getAllLogs() {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs ORDER BY timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading audit logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Adds a new audit log.
     * @return The newly created AuditLog object, or null on failure.
     */
    
    public AuditLog addLog(String userId, String actionType, String description) {
        String query = "INSERT INTO audit_logs (user_id, action_type, description, timestamp) VALUES (?, ?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, actionType);
            pstmt.setString(3, description);
            pstmt.setTimestamp(4, Timestamp.valueOf(now));
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    long newId = rs.getLong(1);
                    return new AuditLog(newId, userId, actionType, description, now);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding audit log: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes an audit log by its ID.
     * @return true if successful, false otherwise.
     */
    
    public boolean deleteLog(long logId) {
        String query = "DELETE FROM audit_logs WHERE log_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, logId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting audit log: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private AuditLog extractLogFromResultSet(ResultSet rs) throws SQLException {
        return new AuditLog(
            rs.getLong("log_id"),
            rs.getString("user_id"),
            rs.getString("action_type"),
            rs.getString("description"),
            rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }
}