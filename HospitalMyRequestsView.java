package app.ui;

import app.controllers.BloodRequestController;
import app.models.BloodRequest;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import app.Main;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * HospitalMyRequestsView - FINAL VERSION
 * - Connects to DB via BloodRequestController.
 * - "Submit Request" now saves to DB.
 * - Table loads real data from 'blood_requests' table.
 */

public class HospitalMyRequestsView {
    
    private Stage stage;
    private TableView<BloodRequestRecord> requestTable;
    private ObservableList<BloodRequestRecord> tableData;
    private BloodRequestController controller;
    
    public HospitalMyRequestsView(Stage stage) {
        this.stage = stage;
        this.controller = new BloodRequestController();
        this.tableData = FXCollections.observableArrayList();
        loadData(); // Load real data on startup
    }
    
    private void loadData() {
        tableData.clear();
        List<BloodRequest> requests = controller.getMyRequests();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (BloodRequest req : requests) {
            tableData.add(new BloodRequestRecord(
                req.getRequestId(),
                req.getBloodType(),
                req.getQuantity(),
                req.getUrgency(),
                req.getStatus(),
                req.getRequestDate().format(dtf)
            ));
        }
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        
        Label titleLabel = new Label("📋 My Blood Requests");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        
        // New Request Form
        VBox formCard = createRequestForm();
        
        // Requests Table
        VBox tableCard = createRequestsTable();
        
        mainContent.getChildren().addAll(titleLabel, formCard, tableCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
    }
    
    private VBox createRequestForm() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label formTitle = new Label("➕ New Blood Request");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        ComboBox<String> bloodTypeCombo = new ComboBox<>();
        bloodTypeCombo.getItems().addAll("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-");
        bloodTypeCombo.setPrefWidth(200);
        grid.add(new Label("Blood Type:"), 0, 0);
        grid.add(bloodTypeCombo, 1, 0);
        
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        grid.add(new Label("Quantity (Units):"), 0, 1);
        grid.add(quantitySpinner, 1, 1);
        
        ComboBox<String> urgencyCombo = new ComboBox<>();
        urgencyCombo.getItems().addAll("Routine", "Urgent", "Critical");
        urgencyCombo.setValue("Routine");
        grid.add(new Label("Urgency:"), 0, 2);
        grid.add(urgencyCombo, 1, 2);
        
        TextField patientNameField = new TextField();
        patientNameField.setPromptText("Patient Name");
        grid.add(new Label("Patient Name:"), 0, 3);
        grid.add(patientNameField, 1, 3);
        
        Button submitBtn = new Button("📤 Submit Request");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7); -fx-text-fill: white; -fx-padding: 10px 30px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        
        // --- SUBMIT ACTION ---
        submitBtn.setOnAction(e -> {
            if (bloodTypeCombo.getValue() == null || patientNameField.getText().isEmpty()) {
                Main.showAlert("Error", "Please fill all fields!", Alert.AlertType.ERROR);
                return;
            }
            
            boolean success = controller.createRequest(
                patientNameField.getText(),
                bloodTypeCombo.getValue(),
                quantitySpinner.getValue(),
                urgencyCombo.getValue()
            );
            
            if (success) {
                Main.showAlert("Success", "Request submitted successfully!", Alert.AlertType.INFORMATION);
                loadData(); // Refresh table
                requestTable.refresh();
                // Clear fields
                bloodTypeCombo.setValue(null);
                patientNameField.clear();
                quantitySpinner.getValueFactory().setValue(1);
            } else {
                Main.showAlert("Error", "Failed to save request!", Alert.AlertType.ERROR);
            }
        });
        
        card.getChildren().addAll(formTitle, grid, submitBtn);
        return card;
    }
    
    private VBox createRequestsTable() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label tableTitle = new Label("📋 Request History");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        requestTable = new TableView<>();
        requestTable.setPrefHeight(300);
        requestTable.setItems(tableData); // Bind to real data
        
        TableColumn<BloodRequestRecord, String> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<BloodRequestRecord, String> bloodCol = new TableColumn<>("Blood Type");
        bloodCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        
        TableColumn<BloodRequestRecord, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<BloodRequestRecord, String> urgencyCol = new TableColumn<>("Urgency");
        urgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        
        TableColumn<BloodRequestRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<BloodRequestRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        requestTable.getColumns().addAll(idCol, bloodCol, qtyCol, urgencyCol, statusCol, dateCol);
        
        card.getChildren().addAll(tableTitle, requestTable);
        return card;
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("📋 My Requests");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    // Inner class for Table
    public static class BloodRequestRecord {
        private String id, bloodType, urgency, status, date;
        private int quantity;
        
        public BloodRequestRecord(String id, String bloodType, int quantity, String urgency, String status, String date) {
            this.id = id; this.bloodType = bloodType; this.quantity = quantity;
            this.urgency = urgency; this.status = status; this.date = date;
        }
        public String getId() { return id; }
        public String getBloodType() { return bloodType; }
        public int getQuantity() { return quantity; }
        public String getUrgency() { return urgency; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }
}