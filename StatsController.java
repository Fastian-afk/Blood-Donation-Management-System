package app.controllers;

import app.database.*;
import app.models.*;
import app.ui.LoginHelper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StatsController - FINAL VERSION
 */

public class StatsController {

    private DonorDAO donorDAO;
    private BloodUnitDAO unitDAO;
    private BloodRequestDAO requestDAO;
    private AppointmentDAO appointmentDAO;
    private DonationDAO donationDAO; 

    public StatsController() {
        this.donorDAO = new DonorDAO();
        this.unitDAO = new BloodUnitDAO();
        this.requestDAO = new BloodRequestDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.donationDAO = new DonationDAO();
    }

    // --- Admin & Staff Stats ---
    public Map<String, String> getAdminStats() {
        Map<String, String> stats = new HashMap<>();
        
        int donors = donorDAO.getTotalDonorCount();
        stats.put("donors", String.valueOf(donors));

        int units = unitDAO.getAllUnits().size();
        stats.put("units", String.valueOf(units));
        
        long pending = requestDAO.getAllRequests().stream()
                .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()))
                .count();
        stats.put("pending", String.valueOf(pending));

        int todayAppts = appointmentDAO.findAppointmentsByDate(LocalDate.now()).size();
        stats.put("appointments", String.valueOf(todayAppts));

        return stats;
    }

    // --- Hospital Stats ---
    public Map<String, String> getHospitalStats() {
        Map<String, String> stats = new HashMap<>();
        String userId = LoginHelper.getCurrentUserId();
        String hospId = requestDAO.findHospitalIdByUserId(userId);

        if (hospId != null) {
            int pending = requestDAO.countPendingRequestsByHospital(hospId);
            int fulfilled = requestDAO.countFulfilledRequestsByHospital(hospId);
            int received = requestDAO.sumReceivedUnitsByHospital(hospId);

            stats.put("pending", String.valueOf(pending));
            stats.put("fulfilled", String.valueOf(fulfilled));
            stats.put("received", String.valueOf(received));
        } else {
            stats.put("pending", "0");
            stats.put("fulfilled", "0");
            stats.put("received", "0");
        }

        long available = unitDAO.getAllUnits().stream()
                .filter(u -> "Available".equalsIgnoreCase(u.getStatus()))
                .count();
        stats.put("stock", String.valueOf(available));

        return stats;
    }

    // --- Donor Stats ---
    public Map<String, String> getDonorStats() {
        Map<String, String> stats = new HashMap<>();
        
        // 1. Get User ID
        String userId = LoginHelper.getCurrentUserId();
        
        // 2. Find Donor ID
        String donorId = donationDAO.findDonorIdByUserId(userId);
        
        // 3. Fetch Donor
        Donor donor = (donorId != null) ? donorDAO.findDonorById(donorId) : null;

        if (donor != null) {
            // 1. Blood Type (FIXED: Using getBloodGroup)
            stats.put("bloodType", donor.getBloodGroup());
            
            // 2. Eligibility (FIXED: Calculated)
            LocalDate lastDonation = donor.getLastDonationDate();
            if (lastDonation == null) {
                stats.put("eligibility", "Eligible");
            } else {
                LocalDate nextEligible = lastDonation.plusDays(56);
                if (nextEligible.isAfter(LocalDate.now())) {
                    stats.put("eligibility", "Wait (" + nextEligible.toString() + ")");
                } else {
                    stats.put("eligibility", "Eligible");
                }
            }

            // 3. Total Donations
            int count = donationDAO.countDonationsByDonor(donor.getDonorId());
            stats.put("donations", String.valueOf(count));

            // 4. Next Appointment
            List<Appointment> apps = appointmentDAO.findAppointmentsByDonor(donor.getDonorId());
            String nextAppt = "None";
            
            for (Appointment app : apps) {
                 if (app.getAppointmentDate().isAfter(java.time.LocalDateTime.now()) && 
                     !"Cancelled".equalsIgnoreCase(app.getStatus())) {
                     nextAppt = app.getAppointmentDate().format(DateTimeFormatter.ofPattern("MMM dd"));
                     break;
                 }
            }
            stats.put("appointment", nextAppt);
        } else {
            stats.put("bloodType", "-");
            stats.put("eligibility", "Unknown");
            stats.put("donations", "0");
            stats.put("appointment", "None");
        }

        return stats;
    }
}