package app.database;

import app.models.Donor;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DonorDAO - FINAL VERSION
 * Now JOINS with the 'users' table to fetch the donor's email.
 */

public class DonorDAO {
    
    private Connection connection;
    
    // Base query to get all donor info + email
    private final String SELECT_DONOR_WITH_EMAIL = """
        SELECT d.*, u.email 
        FROM donors d 
        JOIN users u ON d.user_id = u.user_id 
    """;
    
    public DonorDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public boolean saveDonor(Donor donor) {
        // This query remains the same, as email is in the users table
        String query = """
            INSERT INTO donors (donor_id, user_id, full_name, gender, age, 
                               blood_group, contact_number, address, last_donation_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donor.getDonorId());
            pstmt.setString(2, donor.getUserId());
            pstmt.setString(3, donor.getFullName());
            pstmt.setString(4, donor.getGender());
            pstmt.setInt(5, donor.getAge());
            pstmt.setString(6, donor.getBloodGroup());
            pstmt.setString(7, donor.getContactNumber());
            pstmt.setString(8, donor.getAddress());
            
            if (donor.getLastDonationDate() != null) {
                pstmt.setDate(9, Date.valueOf(donor.getLastDonationDate()));
            } else {
                pstmt.setNull(9, Types.DATE);
            }
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error saving donor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Donor findDonorById(String donorId) {
        String query = SELECT_DONOR_WITH_EMAIL + " WHERE d.donor_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractDonorFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding donor: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public Donor findDonorByEmail(String email) {
        String query = SELECT_DONOR_WITH_EMAIL + " WHERE u.email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractDonorFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding donor by email: " + e.getMessage());
        }
        return null;
    }
    
    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        String query = SELECT_DONOR_WITH_EMAIL + " ORDER BY d.full_name ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                donors.add(extractDonorFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all donors: " + e.getMessage());
        }
        return donors;
    }
    
    public boolean updateDonor(Donor donor) {
        // This query only updates the donors table. Email is updated separately.
        String query = """
            UPDATE donors 
            SET full_name = ?, gender = ?, age = ?, blood_group = ?,
                contact_number = ?, address = ?, last_donation_date = ?
            WHERE donor_id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donor.getFullName());
            pstmt.setString(2, donor.getGender());
            pstmt.setInt(3, donor.getAge());
            pstmt.setString(4, donor.getBloodGroup());
            pstmt.setString(5, donor.getContactNumber());
            pstmt.setString(6, donor.getAddress());
            
            if (donor.getLastDonationDate() != null) {
                pstmt.setDate(7, Date.valueOf(donor.getLastDonationDate()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setString(8, donor.getDonorId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating donor: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteDonor(String donorId) {
        String findUserQuery = "SELECT user_id FROM donors WHERE donor_id = ?";
        String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
        
        try (PreparedStatement findPstmt = connection.prepareStatement(findUserQuery)) {
            findPstmt.setString(1, donorId);
            ResultSet rs = findPstmt.executeQuery();
            
            if (rs.next()) {
                String userId = rs.getString("user_id");
                try (PreparedStatement deletePstmt = connection.prepareStatement(deleteUserQuery)) {
                    deletePstmt.setString(1, userId);
                    int result = deletePstmt.executeUpdate();
                    return result > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting donor: " + e.getMessage());
            return false;
        }
    }
    
    public int getTotalDonorCount() {
        String query = "SELECT COUNT(*) as count FROM donors";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting donor count: " + e.getMessage());
        }
        return 0;
    }
    
    private Donor extractDonorFromResultSet(ResultSet rs) throws SQLException {
        Donor donor = new Donor();
        donor.setDonorId(rs.getString("donor_id"));
        donor.setUserId(rs.getString("user_id"));
        donor.setFullName(rs.getString("full_name"));
        donor.setEmail(rs.getString("email")); // <-- ADDED THIS
        donor.setGender(rs.getString("gender"));
        donor.setAge(rs.getInt("age"));
        donor.setBloodGroup(rs.getString("blood_group"));
        donor.setContactNumber(rs.getString("contact_number"));
        donor.setAddress(rs.getString("address"));
        
        Date lastDonation = rs.getDate("last_donation_date");
        if (lastDonation != null) {
            donor.setLastDonationDate(lastDonation.toLocalDate());
        }
        return donor;
    }
}