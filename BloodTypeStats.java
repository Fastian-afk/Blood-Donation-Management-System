package app.models;

import javafx.beans.property.*;

/**
 * BloodTypeStats - Model for ReportView Persistence
 * This class must be saved in the app.models package.
 */

public class BloodTypeStats {
    
    private final IntegerProperty statId; 
    private final StringProperty bloodType;
    private final IntegerProperty totalUnits;
    private final IntegerProperty available;
    private final StringProperty status;
    
    // Constructor for creating new objects (no ID yet)
    public BloodTypeStats(String bloodType, int totalUnits, int available, String status) {
        this(0, bloodType, totalUnits, available, status);
    }

    // Constructor for objects from DB (with ID)
    public BloodTypeStats(int statId, String bloodType, int totalUnits, int available, String status) {
        this.statId = new SimpleIntegerProperty(statId);
        this.bloodType = new SimpleStringProperty(bloodType);
        this.totalUnits = new SimpleIntegerProperty(totalUnits);
        this.available = new SimpleIntegerProperty(available);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
    public int getStatId() { return statId.get(); }
    public String getBloodType() { return bloodType.get(); }
    public int getTotalUnits() { return totalUnits.get(); }
    public int getAvailable() { return available.get(); }
    public String getStatus() { return status.get(); }
    
    // Setters
    public void setStatId(int id) { this.statId.set(id); }

    // Property Getters (for TableView)
    public IntegerProperty statIdProperty() { return statId; }
    public StringProperty bloodTypeProperty() { return bloodType; }
    public IntegerProperty totalUnitsProperty() { return totalUnits; }
    public IntegerProperty availableProperty() { return available; }
    public StringProperty statusProperty() { return status; }
}