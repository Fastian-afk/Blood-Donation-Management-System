package app.ui;

import app.controllers.DonationController;
import app.controllers.DonorController;
import app.models.Donation;
import app.models.Donor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import app.Main;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DonationProcessView - FINAL VERSION
 * SD7: Implements Lab Test Entry.
 * - Added ComboBoxes for medical tests.
 * - Validation: Cannot approve if any test is Positive.
 * - Auto-fills rejection reason based on test results.
 */

public class DonationProcessView {
    
    private Stage stage;
    private DonationController donationController;
    private DonorController donorController;

    // UI elements
    private VBox detailsBox;
    private HBox buttonBox;
    private Label donationIdLabel, donorNameLabel, bloodTypeLabel, donationDateLabel, quantityLabel, statusLabel;
    private TextField donationIdField;
    
    // Lab Test Inputs
    private ComboBox<String> hivCombo, hepBCombo, hepCCombo, syphilisCombo, malariaCombo;

    public DonationProcessView(Stage stage) {
        this.stage = stage;
        this.donationController = new DonationController();
        this.donorController = new DonorController();
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
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("Process Blood Donation");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333333"));
        
        VBox processCard = new VBox(25);
        processCard.setPadding(new Insets(40));
        processCard.setMaxWidth(900);
        processCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label searchLabel = new Label("🔍 Search Donation");
        searchLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        searchLabel.setTextFill(Color.web("#667eea"));
        
        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        donationIdField = new TextField();
        donationIdField.setPromptText("Enter Donation ID (e.g., DON-001)");
        donationIdField.setPrefHeight(45);
        donationIdField.setPrefWidth(400);
        donationIdField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(45);
        searchButton.setPrefWidth(120);
        searchButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        searchBox.getChildren().addAll(donationIdField, searchButton);
        
        // --- Donation Details (Dynamic) ---
        createDetailsBox(); 
        detailsBox.setVisible(false); 
        
        // --- Action Buttons (Dynamic) ---
        createButtonBox(); 
        buttonBox.setVisible(false); 
        
        // --- Search Action ---
        searchButton.setOnAction(e -> {
            String donationId = donationIdField.getText().trim();
            if (donationId.isEmpty() || !donationId.startsWith("DON-")) {
                Main.showAlert("Error", "Invalid Donation ID format. Must start with 'DON-'.", Alert.AlertType.WARNING);
                return;
            }
            
            Donation donation = donationController.findDonationById(donationId);
            
            if (donation != null) {
                Donor donor = donationController.findDonorForDonation(donation);
                populateDonationDetails(donation, donor);
                
                // Reset lab tests to Negative by default when loading new record
                resetLabTests();
                
                detailsBox.setVisible(true);
                buttonBox.setVisible(true);
            } else {
                Main.showAlert("Not Found", "Donation ID not found in system.", Alert.AlertType.ERROR);
                detailsBox.setVisible(false);
                buttonBox.setVisible(false);
            }
        });
        
        processCard.getChildren().addAll(searchLabel, searchBox, detailsBox, buttonBox);
        mainContent.getChildren().addAll(titleLabel, processCard);
        scrollPane.setContent(mainContent);
        
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 500, 650);
        stage.setScene(scene);
    }

    private void createDetailsBox() {
        detailsBox = new VBox(20);
        detailsBox.setPadding(new Insets(25));
        detailsBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");
        
        Label donationInfoLabel = new Label("👤 Donation Information");
        donationInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(30);
        infoGrid.setVgap(12);
        
        donationIdLabel = new Label("-");
        donorNameLabel = new Label("-");
        bloodTypeLabel = new Label("-");
        donationDateLabel = new Label("-");
        quantityLabel = new Label("-");
        statusLabel = new Label("-");
        
        infoGrid.add(new Label("Donation ID:"), 0, 0);
        infoGrid.add(donationIdLabel, 1, 0);
        infoGrid.add(new Label("Donor Name:"), 0, 1);
        infoGrid.add(donorNameLabel, 1, 1);
        infoGrid.add(new Label("Blood Type:"), 0, 2);
        infoGrid.add(bloodTypeLabel, 1, 2);
        infoGrid.add(new Label("Donation Date:"), 0, 3);
        infoGrid.add(donationDateLabel, 1, 3);
        infoGrid.add(new Label("Quantity:"), 0, 4);
        infoGrid.add(quantityLabel, 1, 4);
        infoGrid.add(new Label("Current Status:"), 0, 5);
        infoGrid.add(statusLabel, 1, 5);
        
        // --- Lab Test Entry Section (SD7 Implementation) ---
        Label testResultsLabel = new Label("🔬 Lab Test Results Entry");
        testResultsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        testResultsLabel.setTextFill(Color.web("#333333"));
        
        GridPane testGrid = new GridPane();
        testGrid.setHgap(20);
        testGrid.setVgap(15);
        
        // Initialize ComboBoxes
        hivCombo = createTestCombo();
        hepBCombo = createTestCombo();
        hepCCombo = createTestCombo();
        syphilisCombo = createTestCombo();
        malariaCombo = createTestCombo();

        testGrid.add(new Label("HIV I/II:"), 0, 0);
        testGrid.add(hivCombo, 1, 0);
        
        testGrid.add(new Label("Hepatitis B (HBsAg):"), 0, 1);
        testGrid.add(hepBCombo, 1, 1);
        
        testGrid.add(new Label("Hepatitis C (HCV):"), 0, 2);
        testGrid.add(hepCCombo, 1, 2);
        
        testGrid.add(new Label("Syphilis (VDRL):"), 2, 0);
        testGrid.add(syphilisCombo, 3, 0);
        
        testGrid.add(new Label("Malaria:"), 2, 1);
        testGrid.add(malariaCombo, 3, 1);

        detailsBox.getChildren().addAll(donationInfoLabel, infoGrid, new Separator(), testResultsLabel, testGrid);
    }
    
    private ComboBox<String> createTestCombo() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Negative", "Positive");
        combo.setValue("Negative");
        combo.setPrefWidth(150);

        // 1. Style the dropdown list items
        combo.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Logic: Positive = Red, Negative = Green
                    if ("Positive".equals(item)) {
                        setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-background-color: #ffcdd2;"); // Red text on light red bg
                    } else {
                        setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold; -fx-background-color: #c8e6c9;"); // Green text on light green bg
                    }
                }
            }
        });

        // 2. Style the selected value (the button itself)
        combo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Positive".equals(item)) {
                        setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;"); // Red text
                    } else {
                        setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;"); // Green text
                    }
                }
            }
        });

        return combo;
    }
    
    private void resetLabTests() {
        hivCombo.setValue("Negative");
        hepBCombo.setValue("Negative");
        hepCCombo.setValue("Negative");
        syphilisCombo.setValue("Negative");
        malariaCombo.setValue("Negative");
    }

    private void populateDonationDetails(Donation donation, Donor donor) {
        donationIdLabel.setText(donation.getDonationId());
        donationDateLabel.setText(donation.getDonationDate().toString());
        quantityLabel.setText(donation.getQuantity() + " ml");
        statusLabel.setText(donation.getStatus());
        
        if (donor != null) {
            donorNameLabel.setText(donor.getFullName());
            bloodTypeLabel.setText(donor.getBloodGroup());
        } else {
            donorNameLabel.setText("Unknown");
            bloodTypeLabel.setText("Unknown");
        }
        
        // Color coding status
        if (donation.getStatus().equals("Pending")) statusLabel.setTextFill(Color.web("#ff9800"));
        else if (donation.getStatus().equals("Approved")) statusLabel.setTextFill(Color.web("#4caf50"));
        else statusLabel.setTextFill(Color.web("#f44336"));
    }
    
    /**
     * Check if any lab test is positive.
     */
    
    private List<String> getPositiveTests() {
        List<String> positives = new ArrayList<>();
        if ("Positive".equals(hivCombo.getValue())) positives.add("HIV");
        if ("Positive".equals(hepBCombo.getValue())) positives.add("Hepatitis B");
        if ("Positive".equals(hepCCombo.getValue())) positives.add("Hepatitis C");
        if ("Positive".equals(syphilisCombo.getValue())) positives.add("Syphilis");
        if ("Positive".equals(malariaCombo.getValue())) positives.add("Malaria");
        return positives;
    }

    private void createButtonBox() {
        buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button approveButton = new Button("✅ Approve Donation");
        approveButton.setPrefWidth(200);
        approveButton.setPrefHeight(50);
        approveButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button rejectButton = new Button("❌ Reject Donation");
        rejectButton.setPrefWidth(200);
        rejectButton.setPrefHeight(50);
        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        buttonBox.getChildren().addAll(approveButton, rejectButton);
        
        // --- Approve Action ---
        approveButton.setOnAction(e -> {
            List<String> positiveTests = getPositiveTests();
            
            // Validation: Cannot approve if tests are positive
            if (!positiveTests.isEmpty()) {
                Main.showAlert("Medical Alert", 
                    "Cannot approve donation.\n\nDonor tested POSITIVE for: " + String.join(", ", positiveTests) + 
                    ".\n\nPlease use the Reject button.", 
                    Alert.AlertType.ERROR);
                return;
            }

            String donationId = donationIdField.getText().trim();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Approve Donation");
            confirm.setHeaderText("Confirm Donation Approval");
            confirm.setContentText("All lab tests are Negative. Mark donation as APPROVED?");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = donationController.approveDonation(donationId);
                    if (success) {
                        Main.showAlert("Success", "Donation APPROVED successfully!", Alert.AlertType.INFORMATION);
                        detailsBox.setVisible(false);
                        buttonBox.setVisible(false);
                        donationIdField.clear();
                    } else {
                        Main.showAlert("Error", "Failed to approve donation!", Alert.AlertType.ERROR);
                    }
                }
            });
        });
        
        // --- Reject Action ---
        rejectButton.setOnAction(e -> {
            String donationId = donationIdField.getText().trim();
            List<String> positiveTests = getPositiveTests();
            
            String defaultReason = "";
            if (!positiveTests.isEmpty()) {
                defaultReason = "Lab failure: Positive for " + String.join(", ", positiveTests);
            }

            TextInputDialog dialog = new TextInputDialog(defaultReason);
            dialog.setTitle("Reject Donation");
            dialog.setHeaderText("Specify Rejection Reason!");
            dialog.setContentText("Reason:");
            
            dialog.showAndWait().ifPresent(reason -> {
                if (reason != null && !reason.trim().isEmpty()) {
                    boolean success = donationController.rejectDonation(donationId, reason);
                    if (success) {
                        Main.showAlert("Success", "Donation REJECTED!", Alert.AlertType.INFORMATION);
                        detailsBox.setVisible(false);
                        buttonBox.setVisible(false);
                        donationIdField.clear();
                    }
                }
            });
        });
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        
        Button backButton = new Button("⬅️ Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("🩸 BDMS - Process Donation");
        titleLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        
        return topBar;
    }
}