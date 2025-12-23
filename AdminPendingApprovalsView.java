package app.ui;

import app.controllers.DonationController;
import app.controllers.DonorController;
import app.models.Donation;
import app.models.Donor;
import app.Main;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * AdminPendingApprovalsView - FINAL VERSION
 * - Displays REAL pending donations from DB.
 * - Approve/Reject buttons are fully functional and update the DB.
 */

public class AdminPendingApprovalsView {
    private Stage stage;
    private TableView<DonationRecord> approvalsTable;
    private DonationController donationController;
    private DonorController donorController;
    private ObservableList<DonationRecord> tableData;
    
    public AdminPendingApprovalsView(Stage stage) {
        this.stage = stage;
        this.donationController = new DonationController();
        this.donorController = new DonorController();
        this.tableData = FXCollections.observableArrayList();
        loadData();
    }

    // Load real pending donations from DB
    private void loadData() {
        tableData.clear();
        List<Donation> pending = donationController.getPendingDonations();
        
        for (Donation d : pending) {
            // Fetch donor to get the name and blood type
            Donor donor = donorController.searchDonor(d.getDonorId());
            String name = (donor != null) ? donor.getFullName() : "Unknown";
            String blood = (donor != null) ? donor.getBloodGroup() : "?";
            
            tableData.add(new DonationRecord(
                d.getDonationId(),
                name,
                blood,
                d.getDonationDate().toString(),
                d.getStatus()
            ));
        }
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setTop(createTopBar());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        
        Label titleLabel = new Label("☑ Pending Donation Approvals");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        // Stats box (Dynamic count)
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
            createStatCard("Pending Action", String.valueOf(tableData.size()), "#ffa726")
        );
        
        // Approvals Table
        VBox tableCard = new VBox(20);
        tableCard.setPadding(new Insets(30));
        tableCard.setMaxWidth(1000);
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label tableTitle = new Label("📋 Donation Approvals");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        approvalsTable = new TableView<>();
        approvalsTable.setItems(tableData);
        approvalsTable.setPrefHeight(400);
        
        TableColumn<DonationRecord, String> donationIdCol = new TableColumn<>("Donation ID");
        donationIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().donationId));
        
        TableColumn<DonationRecord, String> donorCol = new TableColumn<>("Donor Name");
        donorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().donorName));
        
        TableColumn<DonationRecord, String> bloodCol = new TableColumn<>("Blood Type");
        bloodCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().bloodType));
        
        TableColumn<DonationRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().date));
        
        TableColumn<DonationRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().status));
        
        TableColumn<DonationRecord, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(param -> new TableCell<DonationRecord, Void>() {
            private final HBox hbox = new HBox(10);
            private final Button approveBtn = new Button("✔ Approve");
            private final Button rejectBtn = new Button("✘ Reject");
            
            {
                approveBtn.setStyle("-fx-padding: 5px 15px; -fx-background-color: #43e97b; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                rejectBtn.setStyle("-fx-padding: 5px 15px; -fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                
                approveBtn.setOnAction(e -> {
                    DonationRecord record = getTableView().getItems().get(getIndex());
                    if (donationController.approveDonation(record.donationId)) {
                        Main.showAlert("Success", "Donation " + record.donationId + " Approved!", Alert.AlertType.INFORMATION);
                        loadData(); // Refresh table
                    } else {
                        Main.showAlert("Error", "Failed to approve donation.", Alert.AlertType.ERROR);
                    }
                });
                
                rejectBtn.setOnAction(e -> {
                    DonationRecord record = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Reject Donation");
                    dialog.setHeaderText("Reason for Rejection:");
                    dialog.setContentText("Reason:");
                    dialog.showAndWait().ifPresent(reason -> {
                        if (donationController.rejectDonation(record.donationId, reason)) {
                            Main.showAlert("Rejected", "Donation " + record.donationId + " Rejected.", Alert.AlertType.INFORMATION);
                            loadData(); // Refresh table
                        }
                    });
                });
                
                hbox.getChildren().addAll(approveBtn, rejectBtn);
                hbox.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        approvalsTable.getColumns().addAll(donationIdCol, donorCol, bloodCol, dateCol, statusCol, actionCol);
        
        tableCard.getChildren().addAll(tableTitle, approvalsTable);
        mainContent.getChildren().addAll(titleLabel, statsBox, tableCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        card.getChildren().addAll(titleLbl, valueLbl);
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
        Label titleLabel = new Label("☑ Approvals");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    public static class DonationRecord {
        public String donationId, donorName, bloodType, date, status;
        public DonationRecord(String donationId, String donorName, String bloodType, String date, String status) {
            this.donationId = donationId; this.donorName = donorName; this.bloodType = bloodType;
            this.date = date; this.status = status;
        }
    }
}