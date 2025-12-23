package app.controllers;

import app.database.AuditLogDAO;
import app.models.AuditLog;
import java.util.List;

/**
 * AuditLogController - GRASP Controller Pattern
 * Handles business logic for the audit log / recent activities.
 */
public class AuditLogController {

    private AuditLogDAO auditLogDAO;

    public AuditLogController() {
        this.auditLogDAO = new AuditLogDAO();
    }

    /**
     * Gets all audit logs.
     * @return A list of all logs.
     */
    public List<AuditLog> getAllLogs() {
        return auditLogDAO.getAllLogs();
    }

    /**
     * Creates a new audit log.
     * @return The new AuditLog object if successful.
     */
    public AuditLog addLog(String userId, String actionType, String description) {
        if (userId == null || actionType == null || description == null) {
            System.err.println("❌ Log fields cannot be null");
            return null;
        }
        return auditLogDAO.addLog(userId, actionType, description);
    }

    /**
     * Deletes an audit log.
     * @return true if successful.
     */
    public boolean deleteLog(long logId) {
        return auditLogDAO.deleteLog(logId);
    }
}