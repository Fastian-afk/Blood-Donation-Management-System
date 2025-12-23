package app.controllers;

import app.database.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReportController {
    
    public Map<String, Integer> getBloodUsageReport() {
        Map<String, Integer> report = new HashMap<>();
        String query = "SELECT blood_type, SUM(quantity) as total FROM donations GROUP BY blood_type";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                report.put(rs.getString("blood_type"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }
    
    public int getTotalDonors() {
        String query = "SELECT COUNT(*) FROM donors";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalDonations() {
        String query = "SELECT COUNT(*) FROM donations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean exportData(String format) {
        // Implement CSV or PDF export
        return true;
    }
}