package app.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Appointment Model Class
 * UPDATED to match bdms_schema.txt
 */

public class Appointment {
    
    private String appointmentId;
    private String donorId;
    private String centerId;
    private LocalDateTime appointmentDate;
    private String status; // 'Pending', 'Completed', 'Cancelled'
    
    public Appointment() {
        this.appointmentId = "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = "Pending"; // Default status as per schema
    }
    
    public Appointment(String donorId, String centerId, LocalDateTime appointmentDate) {
        this();
        this.donorId = donorId;
        this.centerId = centerId;
        this.appointmentDate = appointmentDate;
    }
    
    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    
    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    
    public String getCenterId() { return centerId; }
    public void setCenterId(String centerId) { this.centerId = centerId; }
    
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Business Logic
    public boolean isUpcoming() {
        return appointmentDate.isAfter(LocalDateTime.now()) && status.equals("Pending");
    }
    
    public boolean canBeCancelled() {
        // You can still cancel up to 24 hours before
        return appointmentDate.isAfter(LocalDateTime.now().plusHours(24)) && status.equals("Pending");
    }
    
    public void cancel() {
        if (canBeCancelled()) {
            this.status = "Cancelled";
        }
    }
    
    public void complete() {
        this.status = "Completed";
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", donorId='" + donorId + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", status='" + status + '\'' +
                '}';
    }
}