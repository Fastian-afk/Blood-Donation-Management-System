package app.database;

import app.ui.AdminAnalyticsView.HospitalStats;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

/**
 * HospitalActivityDAO - FINAL VERSION
 * Persists Admin Analytics data.
 */

public class HospitalActivityDAO {

    private Connection connection;

    public HospitalActivityDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public ObservableList<HospitalStats> getAllHospitalStats() {
        ObservableList<HospitalStats> statsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM hospital_activity";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                statsList.add(new HospitalStats(
                    rs.getInt("activity_id"),
                    rs.getString("hospital_name"),
                    rs.getInt("requests"),
                    rs.getInt("fulfilled"),
                    rs.getString("fulfillment_rate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statsList;
    }

    public HospitalStats addHospitalStats(HospitalStats stats) {
        String query = "INSERT INTO hospital_activity (hospital_name, requests, fulfilled, fulfillment_rate) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, stats.getHospital());
            pstmt.setInt(2, stats.getRequests());
            pstmt.setInt(3, stats.getFulfilled());
            pstmt.setString(4, stats.getRate());
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    stats.setActivityId(rs.getInt(1));
                    return stats;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteHospitalStats(int activityId) {
        String query = "DELETE FROM hospital_activity WHERE activity_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, activityId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}