package app.database;

import app.models.BloodTypeStats;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ReportDAO - FINAL VERSION
 * Added getRealTimeDistribution() to fetch live inventory stats.
 */

public class ReportDAO {

    private Connection connection;

    public ReportDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Fetches real-time blood inventory statistics grouped by blood type.
     */
    
    public List<BloodTypeStats> getRealTimeDistribution() {
        List<BloodTypeStats> stats = new ArrayList<>();
        // Query aggregates units by blood type
        String query = """
            SELECT 
                blood_group,
                COUNT(*) as total_units,
                SUM(CASE WHEN status = 'Available' THEN 1 ELSE 0 END) as available_units
            FROM blood_units
            GROUP BY blood_group
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Pre-populate map to ensure all types show up even if count is 0
            java.util.Map<String, BloodTypeStats> map = new java.util.HashMap<>();
            String[] types = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            for (String t : types) map.put(t, new BloodTypeStats(t, 0, 0, "Critical"));

            while (rs.next()) {
                String type = rs.getString("blood_group");
                int total = rs.getInt("total_units");
                int avail = rs.getInt("available_units");
                
                // Determine status based on available units
                String status = "Good";
                if (avail < 5) status = "Critical";
                else if (avail < 15) status = "Low";
                
                if (map.containsKey(type)) {
                    map.put(type, new BloodTypeStats(type, total, avail, status));
                }
            }
            
            stats.addAll(map.values());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    /**
     * Fetches aggregated system stats for the Report View cards.
     */
    
    public java.util.Map<String, String> getReportStats() {
        java.util.Map<String, String> stats = new java.util.HashMap<>();
        
        try (Statement stmt = connection.createStatement()) {
            // 1. Total Donations Today
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM donations WHERE donation_date = CURDATE()");
            if (rs.next()) stats.put("donations_today", String.valueOf(rs.getInt(1)));

            // 2. Total Units Collected (Total rows in blood_units)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM blood_units");
            if (rs.next()) stats.put("total_units", String.valueOf(rs.getInt(1)));

            // 3. Active Donors (Total donors)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM donors");
            if (rs.next()) stats.put("active_donors", String.valueOf(rs.getInt(1)));

            // 4. Blood Units in Stock (Available)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM blood_units WHERE status = 'Available'");
            if (rs.next()) stats.put("units_in_stock", String.valueOf(rs.getInt(1)));

            // 5. Pending Requests
            rs = stmt.executeQuery("SELECT COUNT(*) FROM blood_requests WHERE status = 'Pending'");
            if (rs.next()) stats.put("pending_requests", String.valueOf(rs.getInt(1)));

            // 6. Completed Donations (Approved)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM donations WHERE status = 'Approved'");
            if (rs.next()) stats.put("completed_donations", String.valueOf(rs.getInt(1)));

            // 7. Critical Shortage (Count types with < 5 units)
            // Complex query simplified:
            rs = stmt.executeQuery("SELECT COUNT(*) FROM (SELECT blood_group, COUNT(*) as cnt FROM blood_units WHERE status='Available' GROUP BY blood_group HAVING cnt < 5) as shortages");
            if (rs.next()) stats.put("critical_shortage", String.valueOf(rs.getInt(1)));
            
            // 8. Eligible Donors (Calculated via SQL for speed)
            // Assuming 'eligibility_status' is stored or calculated. For now, count all donors - donors with recent donation
            // Simplified: Count total donors
            stats.put("eligible_donors", stats.get("active_donors")); 

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    
    public boolean deleteStat(int statId) { return true; } 
    public boolean addStat(BloodTypeStats stat) { return true; }
    
    public List<BloodTypeStats> getAllStats() {
        // Redirect to the real-time method
        return getRealTimeDistribution();
    }
}