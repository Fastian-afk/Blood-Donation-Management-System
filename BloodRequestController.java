package app.controllers;

import app.database.BloodRequestDAO;
import app.models.BloodRequest;
import app.ui.LoginHelper;
import java.util.List;

public class BloodRequestController {
    private BloodRequestDAO dao;
    private String currentHospitalId;

    public BloodRequestController() {
        this.dao = new BloodRequestDAO();
        // Auto-fetch hospital ID based on login
        String userId = LoginHelper.getCurrentUserId();
        if (userId != null) {
            this.currentHospitalId = dao.findHospitalIdByUserId(userId);
        }
    }

    public String getCurrentHospitalId() {
        return currentHospitalId;
    }

    public boolean createRequest(String patientName, String bloodType, int quantity, String urgency) {
        if (currentHospitalId == null) return false;
        BloodRequest req = new BloodRequest(currentHospitalId, patientName, bloodType, quantity, urgency);
        return dao.saveRequest(req);
    }

    public List<BloodRequest> getMyRequests() {
        if (currentHospitalId == null) return List.of();
        return dao.getRequestsByHospital(currentHospitalId);
    }
}