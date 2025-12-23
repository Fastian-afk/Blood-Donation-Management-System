package app.models;

import java.time.LocalDate;

/**
 * BloodUnit Model - Represents a single unit of blood product.
 * Used for inventory and tracking (UC10).
 */

public class BloodUnit {
    private String unitId;
    private String donationId;
    private String bloodGroup;
    private LocalDate collectionDate;
    private LocalDate expiryDate;
    private String status; // Available, Reserved, Used, Expired
    private String centerId;

    public BloodUnit() {}
    
    // Getters and Setters
    public String getUnitId() { return unitId; }
    public void setUnitId(String unitId) { this.unitId = unitId; }
    public String getDonationId() { return donationId; }
    public void setDonationId(String donationId) { this.donationId = donationId; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCenterId() { return centerId; }
    public void setCenterId(String centerId) { this.centerId = centerId; }
}