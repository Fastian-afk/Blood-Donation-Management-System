package app.controllers;

import app.models.Appointment;
import app.models.Reminder;
import app.database.AppointmentDAO;
import app.database.ReminderDAO; 
import app.database.DonorDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; 
import java.util.List;

/**
 * Now schedules a reminder entry in the database upon successful appointment scheduling.
 */

public class AppointmentController {
    
    private AppointmentDAO appointmentDAO;
    private ReminderDAO reminderDAO; 
    
    public AppointmentController() {
        this.appointmentDAO = new AppointmentDAO();
        this.reminderDAO = new ReminderDAO();
    }
    
    /**
     * Schedule a new appointment and queue a reminder.
     */
    
    public String scheduleAppointment(String donorId, String centerId, LocalDateTime appointmentDate) {
        
        // Validation
        if (donorId == null || donorId.trim().isEmpty()) {
            System.err.println("❌ Donor ID cannot be empty");
            return null;
        }
        
        if (appointmentDate == null || appointmentDate.isBefore(LocalDateTime.now())) {
            System.err.println("❌ Invalid appointment date");
            return null;
        }
        
        if (appointmentDAO.hasAppointmentOnDate(donorId, appointmentDate.toLocalDate())) {
            System.err.println("❌ Donor already has a pending appointment on this date");
            return null;
        }
        
        // Create appointment
        Appointment appointment = new Appointment(donorId, centerId, appointmentDate);
        
        // Save to database
        boolean saved = appointmentDAO.saveAppointment(appointment);
        
        if (saved) {
            System.out.println("✅ Appointment scheduled successfully: " + appointment.getAppointmentId());
            
            // SD2 REMINDER LOGIC
            scheduleReminder(appointment);

            return appointment.getAppointmentId();
        }
        
        return null;
    }
    
    /**
     * Creates and saves a reminder 24 hours before the appointment time.
     */
    private void scheduleReminder(Appointment appointment) {
        LocalDateTime scheduledTime = appointment.getAppointmentDate().minusHours(24);
        String message = String.format(
            "REMINDER: Your blood donation appointment (%s) is scheduled for %s at %s. Please arrive on time.",
            appointment.getAppointmentId(),
            appointment.getAppointmentDate().toLocalDate().toString(),
            appointment.getAppointmentDate().toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
        );
        
        Reminder reminder = new Reminder(
            appointment.getDonorId(),
            appointment.getAppointmentId(),
            message,
            scheduledTime
        );
        
        if (reminderDAO.saveReminder(reminder)) {
            System.out.println("✅ Reminder scheduled for " + scheduledTime.toLocalDate());
        } else {
            System.err.println("❌ Failed to save reminder to DB.");
        }
    }
    
    public boolean cancelAppointment(String appointmentId) {
        // ... (Logic remains the same)
        Appointment appointment = appointmentDAO.findAppointmentById(appointmentId);
        
        if (appointment == null) {
            System.err.println("❌ Appointment not found");
            return false;
        }
        
        if (!appointment.canBeCancelled()) {
            System.err.println("❌ Appointment cannot be cancelled (less than 24 hours remaining or not pending)");
            return false;
        }
        
        appointment.cancel();
        boolean updated = appointmentDAO.updateAppointment(appointment);
        
        if (updated) {
            System.out.println("✅ Appointment cancelled successfully");
        }
        
        return updated;
    }
    
    public boolean completeAppointment(String appointmentId) {
        Appointment appointment = appointmentDAO.findAppointmentById(appointmentId);
        
        if (appointment == null) {
            return false;
        }
        
        appointment.complete();
        return appointmentDAO.updateAppointment(appointment);
    }
    
    public List<Appointment> getDonorAppointments(String donorId) {
        return appointmentDAO.findAppointmentsByDonor(donorId);
    }
    
    public List<Appointment> getUpcomingAppointments() {
        return appointmentDAO.findUpcomingAppointments();
    }
    
    public List<Appointment> getAppointmentsByDate(java.time.LocalDate date) {
        return appointmentDAO.findAppointmentsByDate(date);
    }
    
    public int getTotalAppointmentCount() {
        return appointmentDAO.getTotalAppointmentCount();
    }
    
    public int getTodaysAppointmentCount() {
        return appointmentDAO.findAppointmentsByDate(java.time.LocalDate.now()).size();
    }
}