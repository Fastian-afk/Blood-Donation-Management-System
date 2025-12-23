package app.database;

import app.models.BloodUnit;
import app.models.Donation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

/**
 * BloodUnitDAO - FINAL MERGED VERSION
 * Contains ALL methods: Dashboard Counts, Unit Tracker, Donation Integration, and Manual Stock.
 */
public class BloodUnitDAO {

    private Connection connection;

    public BloodUnitDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Fetches inventory counts grouped by blood type.
     * REQUIRED for Main.java dashboard.
     */
    public Map<String, Integer> getInventoryCounts() {
        Map<String, Integer> counts = new HashMap<>();
        String query = "SELECT blood_group, COUNT(*) as total FROM blood_units WHERE status = 'Available' GROUP BY blood_group";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                counts.put(rs.getString("blood_group"), rs.getInt("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting inventory counts: " + e.getMessage());
            e.printStackTrace();
        }
        return counts;
    }

    /**
     * Manually adds a batch of blood units to the inventory.
     */
    public boolean addManualInventory(String bloodType, int quantity) {
        String query = "INSERT INTO blood_units (unit_id, blood_group, collection_date, expiry_date, status, center_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            LocalDate today = LocalDate.now();
            LocalDate expiry = today.plusDays(42);
            
            for (int i = 0; i < quantity; i++) {
                String newUnitId = "BU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                pstmt.setString(1, newUnitId);
                pstmt.setString(2, bloodType);
                pstmt.setDate(3, Date.valueOf(today));
                pstmt.setDate(4, Date.valueOf(expiry));
                pstmt.setString(5, "Available");
                pstmt.setString(6, "CTR-1"); 
                
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            return results.length > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding manual inventory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a unit from an approved donation.
     */
    public boolean createUnitFromDonation(BloodUnit unit) {
        String query = "INSERT INTO blood_units (unit_id, donation_id, blood_group, collection_date, expiry_date, status, center_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, unit.getUnitId());
            pstmt.setString(2, unit.getDonationId());
            pstmt.setString(3, unit.getBloodGroup());
            pstmt.setDate(4, Date.valueOf(unit.getCollectionDate()));
            pstmt.setDate(5, Date.valueOf(unit.getExpiryDate()));
            pstmt.setString(6, unit.getStatus());
            pstmt.setString(7, unit.getCenterId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error saving blood unit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public BloodUnit findUnitById(String unitId) {
        String query = "SELECT * FROM blood_units WHERE unit_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, unitId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return extractBloodUnitFromResultSet(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<BloodUnit> getAllUnits() {
        List<BloodUnit> units = new ArrayList<>();
        String query = "SELECT * FROM blood_units";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) units.add(extractBloodUnitFromResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return units;
    }

    public boolean updateUnitStatus(String unitId, String newStatus) {
        String query = "UPDATE blood_units SET status = ? WHERE unit_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, unitId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public Donation getDonationDetails(String donationId) {
        String query = "SELECT donation_date, status, approved_by, donor_id FROM donations WHERE donation_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Donation d = new Donation();
                d.setDonationId(donationId);
                d.setDonorId(rs.getString("donor_id"));
                d.setDonationDate(rs.getDate("donation_date").toLocalDate());
                d.setStatus(rs.getString("status"));
                d.setApprovedBy(rs.getString("approved_by"));
                return d;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    private BloodUnit extractBloodUnitFromResultSet(ResultSet rs) throws SQLException {
        BloodUnit unit = new BloodUnit();
        unit.setUnitId(rs.getString("unit_id"));
        unit.setDonationId(rs.getString("donation_id"));
        unit.setBloodGroup(rs.getString("blood_group"));
        unit.setCollectionDate(rs.getDate("collection_date").toLocalDate());
        unit.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        unit.setStatus(rs.getString("status"));
        unit.setCenterId(rs.getString("center_id"));
        return unit;
    }
}