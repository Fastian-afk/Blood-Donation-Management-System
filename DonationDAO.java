package app.database;

import app.models.Donation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DonationDAO - FINAL VERSION
 * Added findPendingDonations() for Admin view.
 */

public class DonationDAO {

    private Connection connection;

    public DonationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public String findDonorIdByUserId(String userId) {
        String query = "SELECT donor_id FROM donors WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("donor_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveDonation(Donation donation) {
        String query = "INSERT INTO donations (donation_id, donor_id, donation_date, quantity, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donation.getDonationId());
            pstmt.setString(2, donation.getDonorId());
            pstmt.setDate(3, Date.valueOf(donation.getDonationDate()));
            pstmt.setInt(4, donation.getQuantity());
            pstmt.setString(5, donation.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public int countDonationsByDonor(String donorId) {
        String query = "SELECT COUNT(*) FROM donations WHERE donor_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    

    public Donation findDonationById(String donationId) {
        String query = "SELECT * FROM donations WHERE donation_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return extractDonationFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Donation> findPendingDonations() {
        List<Donation> list = new ArrayList<>();
        String query = "SELECT * FROM donations WHERE status = 'Pending' ORDER BY donation_id ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(extractDonationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateDonationStatus(Donation donation) {
        String query = "UPDATE donations SET status = ?, approval_date = ?, approved_by = ?, rejection_reason = ? WHERE donation_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donation.getStatus());
            if (donation.getApprovalDate() != null) pstmt.setDate(2, Date.valueOf(donation.getApprovalDate()));
            else pstmt.setNull(2, Types.DATE);
            pstmt.setString(3, donation.getApprovedBy());
            pstmt.setString(4, donation.getRejectionReason());
            pstmt.setString(5, donation.getDonationId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Donation> findDonationsByDonorId(String donorId) {
        List<Donation> list = new ArrayList<>();
        String query = "SELECT * FROM donations WHERE donor_id = ? ORDER BY donation_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(extractDonationFromResultSet(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean deleteDonation(String donationId) {
        String query = "DELETE FROM donations WHERE donation_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Donation extractDonationFromResultSet(ResultSet rs) throws SQLException {
        Donation d = new Donation();
        d.setDonationId(rs.getString("donation_id"));
        d.setDonorId(rs.getString("donor_id"));
        d.setDonationDate(rs.getDate("donation_date").toLocalDate());
        d.setQuantity(rs.getInt("quantity"));
        d.setStatus(rs.getString("status"));
        Date appDate = rs.getDate("approval_date");
        if (appDate != null) d.setApprovalDate(appDate.toLocalDate());
        d.setApprovedBy(rs.getString("approved_by"));
        d.setRejectionReason(rs.getString("rejection_reason"));
        return d;
    }
}