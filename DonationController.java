package app.controllers;

import app.database.DonationDAO;
import app.database.DonorDAO;
import app.database.BloodUnitDAO;
import app.models.Donation;
import app.models.Donor;
import app.models.BloodUnit; 
import app.ui.LoginHelper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID; 

/**
 * DonationController - FINAL UPDATE (UC7/SD7 Integration)
 */

public class DonationController {
    
    private DonationDAO donationDAO;
    private DonorDAO donorDAO;
    private BloodUnitDAO unitDAO;
    
    public DonationController() {
        this.donationDAO = new DonationDAO();
        this.donorDAO = new DonorDAO();
        this.unitDAO = new BloodUnitDAO();
    }
    
    public Donation findDonationById(String donationId) {
        return donationDAO.findDonationById(donationId);
    }
    
    public Donor findDonorForDonation(Donation donation) {
        if (donation == null) return null;
        return donorDAO.findDonorById(donation.getDonorId());
    }
    
    public List<Donation> getPendingDonations() {
        return donationDAO.findPendingDonations();
    }
    
    /**
     * Approves a donation and generates a corresponding Blood Unit.
     */
    public boolean approveDonation(String donationId) {
        Donation donation = donationDAO.findDonationById(donationId);
        if (donation == null) {
            System.err.println("❌ Donation not found");
            return false;
        }
        
        // 1. Update Donation Status in DB
        donation.setStatus("Approved");
        donation.setApprovalDate(LocalDate.now());
        donation.setApprovedBy(LoginHelper.getCurrentUsername());
        donation.setRejectionReason(null);
        
        boolean donationUpdated = donationDAO.updateDonationStatus(donation);

        // 2. Generate Blood Unit Record (generateBloodUnits)
        boolean unitCreated = false;
        if (donationUpdated) {
            
            Donor donor = donorDAO.findDonorById(donation.getDonorId());
            if (donor != null) {
                
                BloodUnit newUnit = new BloodUnit();
                newUnit.setUnitId("BU-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase());
                newUnit.setDonationId(donationId);
                newUnit.setBloodGroup(donor.getBloodGroup());
                newUnit.setCollectionDate(donation.getDonationDate());
                newUnit.setExpiryDate(donation.getDonationDate().plusDays(42));
                newUnit.setStatus("Available");
                newUnit.setCenterId("CTR-1"); 
                
                unitCreated = unitDAO.createUnitFromDonation(newUnit);
            }
        }
        
        if (donationUpdated && unitCreated) {
            System.out.println("✅ Donation APPROVED and NEW Blood Unit added to inventory.");
            return true;
        } else {
            System.err.println("❌ Approval failed. Status Update: " + donationUpdated + ", Unit Creation: " + unitCreated);
            return false;
        }
    }
    
    public boolean rejectDonation(String donationId, String reason) {
        Donation donation = donationDAO.findDonationById(donationId);
        if (donation == null) return false;
        
        donation.setStatus("Rejected");
        donation.setRejectionReason(reason);
        donation.setApprovalDate(null);
        donation.setApprovedBy(null);
        
        return donationDAO.updateDonationStatus(donation);
    }
}