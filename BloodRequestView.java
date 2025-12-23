package app.ui;

import app.controllers.BloodRequestController;
import app.models.BloodRequest;
import app.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

/**
 * BloodRequestView - FINAL VERSION
 * Connects to DB via BloodRequestController. 
 * Loads/Saves requests persistently.
 */

public class BloodRequestView {
    
    private Stage stage;
    private BloodRequestController controller;
    private TableView<BloodRequestRecord> requestTable;
    private ObservableList<BloodRequestRecord> tableData;
    
    public BloodRequestView(Stage stage) {
        this.stage = stage;
        this.controller = new BloodRequestController();
        this.tableData = FXCollections.observableArrayList();
        loadData(); // Initial DB load
    }

    private void loadData() {
        tableData.clear();
        var requests = controller.getMyRequests();
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
        root.setTop(createTopBar());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("Blood Request Form");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333333"));
        
        // Form Card
        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(40));
        formCard.setMaxWidth(900);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        // Hospital Info (Auto-filled)
        String hospId = controller.getCurrentHospitalId();
        Label hospInfo = new Label("🏥 Hospital ID: " + (hospId != null ? hospId : "Unknown (Not Logged In)"));
        hospInfo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        hospInfo.setTextFill(Color.web("#667eea"));

        // Inputs
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15);
        
        TextField patientNameField = new TextField();
        patientNameField.setPromptText("Patient Name");
        
        ComboBox<String> bloodTypeBox = new ComboBox<>();
        bloodTypeBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        bloodTypeBox.setPromptText("Blood Type");
        
        Spinner<Integer> qtySpinner = new Spinner<>(1, 50, 1);
        
        ComboBox<String> urgencyBox = new ComboBox<>();
        urgencyBox.getItems().addAll("Routine", "Urgent", "Critical");
        urgencyBox.setValue("Routine");

        grid.add(new Label("Patient Name:"), 0, 0); grid.add(patientNameField, 1, 0);
        grid.add(new Label("Blood Type:"), 0, 1); grid.add(bloodTypeBox, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2); grid.add(qtySpinner, 1, 2);
        grid.add(new Label("Urgency:"), 0, 3); grid.add(urgencyBox, 1, 3);
        
        Button submitBtn = new Button("Submit Request");
        submitBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        submitBtn.setPrefWidth(200);
        
        // Submit Action
        submitBtn.setOnAction(e -> {
            if (patientNameField.getText().isEmpty() || bloodTypeBox.getValue() == null) {
                Main.showAlert("Error", "Please fill all fields", Alert.AlertType.WARNING);
                return;
            }
            boolean success = controller.createRequest(
                patientNameField.getText(),
                bloodTypeBox.getValue(),
                qtySpinner.getValue(),
                urgencyBox.getValue()
            );
            if (success) {
                Main.showAlert("Success", "Request submitted!", Alert.AlertType.INFORMATION);
                loadData(); // Refresh table
                patientNameField.clear();
            } else {
                Main.showAlert("Error", "Failed to save request!", Alert.AlertType.ERROR);
            }
        });

        // History Table
        requestTable = createTable();
        VBox tableBox = new VBox(10, new Label("📜 Request History"), requestTable);
        
        formCard.getChildren().addAll(hospInfo, grid, submitBtn, new Separator(), tableBox);
        mainContent.getChildren().addAll(titleLabel, formCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new Scene(root, 900, 750));
    }
    
    private TableView<BloodRequestRecord> createTable() {
        TableView<BloodRequestRecord> table = new TableView<>();
        table.setItems(tableData);
        table.setPrefHeight(250);
        
        TableColumn<BloodRequestRecord, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<BloodRequestRecord, String> typeCol = new TableColumn<>("Blood");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        
        TableColumn<BloodRequestRecord, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<BloodRequestRecord, String> urgCol = new TableColumn<>("Urgency");
        urgCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        
        TableColumn<BloodRequestRecord, String> statCol = new TableColumn<>("Status");
        statCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<BloodRequestRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        table.getColumns().addAll(idCol, typeCol, qtyCol, urgCol, statCol, dateCol);
        return table;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("⬅️ Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("🩸 BDMS - Request Blood");
        titleLabel.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    public static class BloodRequestRecord {
        private String id, bloodType, urgency, status, date;
        private int quantity;
        
        public BloodRequestRecord(String id, String bloodType, int quantity, String urgency, String status, String date) {
            this.id = id; this.bloodType = bloodType; this.quantity = quantity;
            this.urgency = urgency; this.status = status; this.date = date;
        }
        // Getters for PropertyValueFactory
        public String getId() { return id; }
        public String getBloodType() { return bloodType; }
        public int getQuantity() { return quantity; }
        public String getUrgency() { return urgency; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }
}