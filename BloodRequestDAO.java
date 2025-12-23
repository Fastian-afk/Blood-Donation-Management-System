package app.database;

import app.models.BloodRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BloodRequestDAO {
    private Connection connection;

    public BloodRequestDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public String findHospitalIdByUserId(String userId) {
        String query = "SELECT hospital_id FROM hospitals WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("hospital_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public int countPendingRequestsByHospital(String hospitalId) {
        String query = "SELECT COUNT(*) FROM blood_requests WHERE hospital_id = ? AND status = 'Pending'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countFulfilledRequestsByHospital(String hospitalId) {
        String query = "SELECT COUNT(*) FROM blood_requests WHERE hospital_id = ? AND status = 'Fulfilled'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int sumReceivedUnitsByHospital(String hospitalId) {
        String query = "SELECT SUM(quantity) FROM blood_requests WHERE hospital_id = ? AND status = 'Fulfilled'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    

    public boolean saveRequest(BloodRequest req) {
        String query = "INSERT INTO blood_requests (request_id, hospital_id, patient_name, requested_group, quantity, urgency, status, requested_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, req.getRequestId());
            pstmt.setString(2, req.getHospitalId());
            pstmt.setString(3, req.getPatientName());
            pstmt.setString(4, req.getBloodType());
            pstmt.setInt(5, req.getQuantity());
            pstmt.setString(6, req.getUrgency());
            pstmt.setString(7, req.getStatus());
            pstmt.setTimestamp(8, Timestamp.valueOf(req.getRequestDate()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<BloodRequest> getRequestsByHospital(String hospitalId) {
        List<BloodRequest> list = new ArrayList<>();
        String query = "SELECT * FROM blood_requests WHERE hospital_id = ? ORDER BY requested_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(extractRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    

    public List<BloodRequest> getUrgentRequests(int limit) {
        List<BloodRequest> list = new ArrayList<>();
        String query = "SELECT * FROM blood_requests WHERE status = 'Pending' ORDER BY CASE urgency WHEN 'Critical' THEN 1 WHEN 'Emergency' THEN 2 ELSE 3 END, requested_date ASC LIMIT ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(extractRequest(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    

    // --- NEW: For Admin Dashboard ---
    public List<BloodRequest> getAllRequests() {
        List<BloodRequest> list = new ArrayList<>();
        String query = "SELECT * FROM blood_requests ORDER BY requested_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(extractRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private BloodRequest extractRequest(ResultSet rs) throws SQLException {
        BloodRequest req = new BloodRequest();
        req.setRequestId(rs.getString("request_id"));
        req.setHospitalId(rs.getString("hospital_id"));
        req.setPatientName(rs.getString("patient_name"));
        req.setBloodType(rs.getString("requested_group"));
        req.setQuantity(rs.getInt("quantity"));
        req.setUrgency(rs.getString("urgency"));
        req.setStatus(rs.getString("status"));
        req.setRequestDate(rs.getTimestamp("requested_date").toLocalDateTime());
        return req;
    }
}