package app.models;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Donor Model Class
 * UPDATED: Added email field to be populated from the users table.
 */

public class Donor {
    
    // Private fields - Encapsulation
    private String donorId;
    private String userId; // Foreign key to users table
    private String fullName;
    private String email; // Populated from users table
    private String gender;
    private int age;
    private String bloodGroup;
    private String contactNumber;
    private String address;
    private LocalDate lastDonationDate;
    
    public Donor() {
        this.donorId = "DNR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public Donor(String userId, String fullName, String email, String gender, int age, 
                 String bloodGroup, String contactNumber, String address) {
        this();
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.contactNumber = contactNumber;
        this.address = address;
    }
    
    // Getters and Setters
    
    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    
    public boolean validate() {
        if (fullName == null || fullName.trim().isEmpty()) return false;
        if (contactNumber == null || contactNumber.length() < 10) return false;
        if (bloodGroup == null || bloodGroup.isEmpty()) return false;
        if (age < 18 || age > 65) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Donor{" +
                "donorId='" + donorId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                '}';
    }
}