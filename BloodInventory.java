package app.models;

import java.time.LocalDate;

public class BloodInventory {
    private String bloodType;
    private int quantity;
    private LocalDate expiryDate;
    
    public BloodInventory(String bloodType, int quantity, LocalDate expiryDate) {
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }
    
    public String getBloodType() { return bloodType; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    
    public void setQuantity(int quantity) { this.quantity = quantity; }
}