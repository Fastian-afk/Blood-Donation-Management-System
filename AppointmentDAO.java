package app.database;

import app.models.Appointment;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO - Updated to match bdms_schema.txt
 */

public class AppointmentDAO {
    
    private Connection connection;
    
    public AppointmentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public boolean saveAppointment(Appointment appointment) {
        String query = """
            INSERT INTO appointments (appointment_id, donor_id, center_id, 
                                     appointment_date, status)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, appointment.getAppointmentId());
            pstmt.setString(2, appointment.getDonorId());
            pstmt.setString(3, appointment.getCenterId());
            pstmt.setTimestamp(4, Timestamp.valueOf(appointment.getAppointmentDate()));
            pstmt.setString(5, appointment.getStatus()); 
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saving appointment: " + e.getMessage());
            return false;
        }
    }
    
    public Appointment findAppointmentById(String appointmentId) {
        String query = "SELECT * FROM appointments WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAppointmentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding appointment: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Appointment> findAppointmentsByDonor(String donorId) {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments WHERE donor_id = ? ORDER BY appointment_date DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding appointments by donor: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public List<Appointment> findUpcomingAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT * FROM appointments 
            WHERE appointment_date >= NOW() AND status = 'Pending'
            ORDER BY appointment_date ASC
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding upcoming appointments: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public List<Appointment> findAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT * FROM appointments 
            WHERE DATE(appointment_date) = ?
            ORDER BY appointment_date ASC
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding appointments by date: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public boolean updateAppointment(Appointment appointment) {
        String query = """
            UPDATE appointments 
            SET status = ?, appointment_date = ?
            WHERE appointment_id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, appointment.getStatus());
            pstmt.setTimestamp(2, Timestamp.valueOf(appointment.getAppointmentDate()));
            pstmt.setString(3, appointment.getAppointmentId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating appointment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteAppointment(String appointmentId) {
        String query = "DELETE FROM appointments WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, appointmentId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting appointment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasAppointmentOnDate(String donorId, LocalDate date) {
        String query = """
            SELECT COUNT(*) as count FROM appointments 
            WHERE donor_id = ? AND DATE(appointment_date) = ? AND status = 'Pending'
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, donorId);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking appointment: " + e.getMessage());
        }
        
        return false;
    }
    
    public int getTotalAppointmentCount() {
        String query = "SELECT COUNT(*) as count FROM appointments";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting appointment count: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getString("appointment_id"));
        appointment.setDonorId(rs.getString("donor_id"));
        appointment.setCenterId(rs.getString("center_id"));
        
        Timestamp appointmentTs = rs.getTimestamp("appointment_date");
        if (appointmentTs != null) {
            appointment.setAppointmentDate(appointmentTs.toLocalDateTime());
        }
        
        appointment.setStatus(rs.getString("status"));
        
        
        return appointment;
    }
}