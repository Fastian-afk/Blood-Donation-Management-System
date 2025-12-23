package app.controllers;

import app.models.Donor;
import app.database.DonorDAO;
import app.database.UserDAO;
import java.time.LocalDate;
import java.util.List;

/**
 * DonorController - UPDATED (Final Fix for Donor Lookup)
 */

public class DonorController {
    
    private DonorDAO donorDAO;
    private UserDAO userDAO;
    
    public DonorController() {
        this.donorDAO = new DonorDAO();
        this.userDAO = new UserDAO();
    }
    
    public boolean registerDonor(String fullName, String email, String password, 
                                  String contactNumber, String bloodGroup, LocalDate dateOfBirth, 
                                  String address, String gender) {
        
        // --- Validation and User Creation (Omitted for brevity, assumed correct) ---
        if (email == null || !email.contains("@")) { return false; }
        int age = LocalDate.now().getYear() - dateOfBirth.getYear();
        if (age < 18 || age > 65) { return false; }

        String username = email;
        String role = "Donor";
        
        if (userDAO.userExists(username, email)) {
            System.err.println("❌ Email or Username already registered");
            return false;
        }
        
        String newUserId = userDAO.createUser(username, password, role, email);
        if (newUserId == null) { return false; }
        
        // --- Create the Donor record ---
        Donor donor = new Donor(newUserId, fullName, email, gender, age, bloodGroup, contactNumber, address);
        boolean saved = donorDAO.saveDonor(donor);
        
        if (saved) { System.out.println("✅ Donor registered successfully."); }
        else { System.err.println("❌ Failed to save donor details."); }
        
        return saved;
    }
    
    /**
     * Finds a Donor object using the email (username) of the logged-in user.
     */
    
    public Donor findDonorByEmail(String email) {
        return donorDAO.findDonorByEmail(email);
    }

    // --- Other methods are below ---

    public boolean updateDonorRecord(String donorId, String fullName, String email, 
                                      String contactNumber, String address) {
        
        Donor donor = donorDAO.findDonorById(donorId);
        if (donor == null) { return false; }
        
        boolean emailChanged = !donor.getEmail().equals(email);
        boolean emailUpdated = true;
        
        if (emailChanged) {
            if (email != null && email.contains("@")) {
                emailUpdated = userDAO.updateUserEmail(donor.getUserId(), email);
            } else { return false; }
        }
        
        donor.setFullName(fullName);
        donor.setContactNumber(contactNumber);
        donor.setAddress(address);
        if(emailUpdated) { donor.setEmail(email); }

        boolean donorDetailsUpdated = donorDAO.updateDonor(donor);
        
        return donorDetailsUpdated && emailUpdated;
    }
    
    public Donor searchDonor(String donorId) {
        return donorDAO.findDonorById(donorId);
    }
    
    public List<Donor> getAllDonors() {
        return donorDAO.getAllDonors();
    }
    
    public List<Donor> getDonorsByBloodGroup(String bloodGroup) {
        return donorDAO.getAllDonors().stream()
                .filter(d -> d.getBloodGroup().equals(bloodGroup))
                .toList();
    }
    
    public boolean deleteDonor(String donorId) {
        boolean deleted = donorDAO.deleteDonor(donorId);
        if (deleted) { System.out.println("✅ Donor deleted successfully"); }
        else { System.err.println("❌ Donor not found for deletion"); }
        return deleted;
    }
    
    public int getTotalDonorCount() {
        return donorDAO.getTotalDonorCount();
    }
    
    public boolean isEmailAvailable(String email) {
        return !userDAO.userExists(email, email);
    }
}