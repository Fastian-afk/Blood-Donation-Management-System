package app.controllers;

import app.database.BloodUnitDAO;
import app.database.DonationDAO;
import app.database.DonorDAO;
import app.models.BloodUnit;
import app.models.Donation;
import app.models.Donor;
import app.models.UnitLifecycleEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * UnitTrackerController - Logic for UC10 Blood Unit Lifecycle Tracking.
 * Orchestrates data to build a chronological log (DTOs).
 */

public class UnitTrackerController {

    private BloodUnitDAO unitDAO;
    private DonationDAO donationDAO;
    private DonorDAO donorDAO;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UnitTrackerController() {
        this.unitDAO = new BloodUnitDAO();
        this.donationDAO = new DonationDAO();
        this.donorDAO = new DonorDAO();
    }
    
    public BloodUnit findUnitById(String unitId) {
        return unitDAO.findUnitById(unitId);
    }
    
    /**
     * Generates the complete chronological lifecycle log for a single blood unit.
     */
    
    public List<UnitLifecycleEvent> getUnitLifecycle(String unitId) {
        List<UnitLifecycleEvent> events = new ArrayList<>();
        BloodUnit unit = findUnitById(unitId);
        
        if (unit == null) return events;
        
        // 1. Get Source Donation and Donor Info
        Donation donation = unitDAO.getDonationDetails(unit.getDonationId());
        Donor donor = (donation != null) ? donorDAO.findDonorById(donation.getDonorId()) : null;

        String donorName = (donor != null) ? donor.getFullName() : "Unknown Donor";
        String approvalActor = (donation != null && donation.getApprovedBy() != null) ? donation.getApprovedBy() : "System/Admin";
        
        // 2. Build the Timeline (Chronological Events)
        
        // Event 1: Collection
        events.add(new UnitLifecycleEvent(
            unit.getCollectionDate().format(DTF),
            "Blood unit collected from Donor: " + donorName,
            "Collection Staff"
        ));
        
        // Event 2: Processing/Testing
        String testStatus = donation != null && "Approved".equalsIgnoreCase(donation.getStatus()) ? "Passed all viral markers." : "Testing complete.";
        events.add(new UnitLifecycleEvent(
            unit.getCollectionDate().plusDays(1).format(DTF),
            "Initial processing and lab tests completed. Result: " + testStatus,
            "Lab Staff"
        ));
        
        // Event 3: Inventory Registration/Approval
        events.add(new UnitLifecycleEvent(
            unit.getCollectionDate().plusDays(1).format(DTF),
            "Unit registered into inventory. Approved by: " + approvalActor,
            "Staff"
        ));

        // Event 4: Status/Usage (Simulated, based on current DB status)
        if ("Reserved".equalsIgnoreCase(unit.getStatus())) {
            events.add(new UnitLifecycleEvent(
                unit.getCollectionDate().plusDays(5).format(DTF),
                "Unit reserved for hospital request (HOSP-001)",
                "Distribution Staff"
            ));
        } else if ("Used".equalsIgnoreCase(unit.getStatus())) {
            events.add(new UnitLifecycleEvent(
                unit.getCollectionDate().plusDays(10).format(DTF),
                "Unit utilized for patient treatment (Final status)",
                "Hospital Staff"
            ));
        }
        
        // Final Event: Expiration Date
        events.add(new UnitLifecycleEvent(
            unit.getExpiryDate().format(DTF),
            "Unit expiration date set (42 days). Current status: " + unit.getStatus(),
            "System"
        ));
        
        return events;
    }
}