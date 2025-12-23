package app.models;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Donation Model Class
 * Represents a donation event record from the 'donations' table.
 */

public class Donation {

    private String donationId;
    private String donorId;
    private LocalDate donationDate;
    private int quantity; // in ml
    private String status; // Pending, Approved, Rejected
    private LocalDate approvalDate;
    private String approvedBy; // staff_id
    private String rejectionReason;

    /**
     * Default Constructor
     */
    
    public Donation() {
        this.donationId = "DON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.donationDate = LocalDate.now();
        this.status = "Pending";
        this.quantity = 500; // Default quantity
    }

    /**
     * Parameterized Constructor
     */
    
    public Donation(String donorId, int quantity) {
        this();
        this.donorId = donorId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getDonationId() { return donationId; }
    public void setDonationId(String donationId) { this.donationId = donationId; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}