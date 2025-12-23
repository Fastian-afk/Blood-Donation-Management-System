package app.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class BloodRequest {
    private String requestId;
    private String hospitalId;
    private String patientName;
    private String bloodType; // maps to requested_group
    private int quantity;
    private String urgency;
    private String status;
    private LocalDateTime requestDate;

    // Constructor for new requests
    public BloodRequest(String hospitalId, String patientName, String bloodType, int quantity, String urgency) {
        this.requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.hospitalId = hospitalId;
        this.patientName = patientName;
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.urgency = urgency;
        this.status = "Pending";
        this.requestDate = LocalDateTime.now();
    }

    // Constructor for DB loading
    public BloodRequest() {}

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
}