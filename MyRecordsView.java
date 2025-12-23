package app.ui;

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
import app.controllers.DonorController;
import app.models.Donor;
import app.database.DonationDAO; // Added for ID lookup

/**
 * MyRecordsView - FINAL VERSION
 * Now loads donor profile using User ID instead of Email.
 */

public class MyRecordsView {
    
    private Stage stage;
    private DonorController donorController;
    private DonationDAO donationDAO; // Helper to find Donor ID
    private Donor currentDonor;
    
    private TextField nameField, emailField, contactField, addressField;
    private TextArea medicalHistoryArea;
    private Label bloodTypeLabel, ageLabel, eligibilityLabel, donorIdLabel;
    
    public MyRecordsView(Stage stage) {
        this.stage = stage;
        this.donorController = new DonorController();
        this.donationDAO = new DonationDAO(); // Initialize helper DAO
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        // Search Bar (Visible only for Staff/Admin)
        HBox searchBar = new HBox(15);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setPadding(new Insets(20));
        searchBar.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label searchLabel = new Label("Search Donor:");
        searchLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter Donor ID (e.g., DNR-001)");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);
        searchField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        Button searchButton = new Button("🔍 Search");
        searchButton.setPrefHeight(40);
        searchButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        searchBar.getChildren().addAll(searchLabel, searchField, searchButton);
        
        // Record Card
        VBox recordCard = new VBox(25);
        recordCard.setPadding(new Insets(40));
        recordCard.setMaxWidth(800);
        recordCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        recordCard.setVisible(false);
        
        Label titleLabel = new Label("Donor Information");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#667eea"));
        
        HBox idBox = new HBox(10);
        Label idLabelTitle = new Label("Donor ID:");
        idLabelTitle.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        donorIdLabel = new Label("-");
        donorIdLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        donorIdLabel.setTextFill(Color.web("#667eea"));
        idBox.getChildren().addAll(idLabelTitle, donorIdLabel);
        
        GridPane infoGrid = createInfoGrid();
        HBox bloodInfoBox = createBloodInfoBox();
        
        Label medicalLabel = new Label("Medical History:");
        medicalLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        medicalHistoryArea = new TextArea();
        medicalHistoryArea.setPrefRowCount(4);
        medicalHistoryArea.setWrapText(true);
        medicalHistoryArea.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        HBox buttonBox = createButtonBox();
        
        recordCard.getChildren().addAll(titleLabel, idBox, new Separator(), infoGrid, bloodInfoBox, medicalLabel, medicalHistoryArea, buttonBox);
        
        // --- LOADING LOGIC ---
        if ("Donor".equals(LoginHelper.getCurrentRole())) {
            searchBar.setVisible(false);
            
            // 1. Get User ID
            String userId = LoginHelper.getCurrentUserId();
            
            // 2. Find Donor ID (The FIX)
            String donorId = donationDAO.findDonorIdByUserId(userId);
            
            // 3. Load Profile
            if (donorId != null) {
                currentDonor = donorController.searchDonor(donorId);
                if (currentDonor != null) {
                    displayDonorInfo();
                    recordCard.setVisible(true);
                    setFieldsEditable(false);
                }
            } else {
                Main.showAlert("Error", "Could not load your donor record!", Alert.AlertType.ERROR);
            }
        } else {
            // For Staff/Admin
            mainContent.getChildren().add(searchBar);
        }
        
        mainContent.getChildren().add(recordCard);
        
        // Search Action
        searchButton.setOnAction(e -> {
            String donorId = searchField.getText().trim();
            if (donorId.isEmpty()) {
                Main.showAlert("Error", "Please enter a Donor ID!", Alert.AlertType.WARNING);
                return;
            }
            currentDonor = donorController.searchDonor(donorId);
            if (currentDonor != null) {
                displayDonorInfo();
                recordCard.setVisible(true);
                setFieldsEditable(false);
            } else {
                Main.showAlert("Not Found", "Donor not found in system!", Alert.AlertType.ERROR);
                recordCard.setVisible(false);
            }
        });
        
        // Button Actions
        // Edit
        Button editButton = (Button) buttonBox.getChildren().get(0);
        editButton.setOnAction(e -> {
            setFieldsEditable(true);
            buttonBox.getChildren().get(0).setVisible(false); // Edit
            buttonBox.getChildren().get(1).setVisible(true);  // Save
            buttonBox.getChildren().get(2).setVisible(true);  // Cancel
        });
        
        // Save
        Button saveButton = (Button) buttonBox.getChildren().get(1);
        saveButton.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                Main.showAlert("Error", "Name and Email cannot be empty!", Alert.AlertType.ERROR);
                return;
            }
            boolean success = donorController.updateDonorRecord(
                currentDonor.getDonorId(),
                nameField.getText().trim(),
                emailField.getText().trim(),
                contactField.getText().trim(),
                addressField.getText().trim()
            );
            if (success) {
                Main.showAlert("Success", "Donor information updated successfully!", Alert.AlertType.INFORMATION);
                currentDonor = donorController.searchDonor(currentDonor.getDonorId());
                displayDonorInfo();
                setFieldsEditable(false);
                buttonBox.getChildren().get(0).setVisible(true);
                buttonBox.getChildren().get(1).setVisible(false);
                buttonBox.getChildren().get(2).setVisible(false);
            } else {
                Main.showAlert("Error", "Failed to update donor information!", Alert.AlertType.ERROR);
            }
        });
        
        // Cancel
        Button cancelButton = (Button) buttonBox.getChildren().get(2);
        cancelButton.setOnAction(e -> {
            displayDonorInfo();
            setFieldsEditable(false);
            buttonBox.getChildren().get(0).setVisible(true);
            buttonBox.getChildren().get(1).setVisible(false);
            buttonBox.getChildren().get(2).setVisible(false);
        });
        
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 950, 750));
    }
    
    private GridPane createInfoGrid() {
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20); infoGrid.setVgap(15);
        
        Label nameLabel = new Label("Full Name:"); nameLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        nameField = new TextField(); nameField.setPrefHeight(40);
        nameField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        Label emailLabel = new Label("Email:"); emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        emailField = new TextField(); emailField.setPrefHeight(40);
        emailField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        Label contactLabel = new Label("Contact:"); contactLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        contactField = new TextField(); contactField.setPrefHeight(40);
        contactField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        Label addressLabel = new Label("Address:"); addressLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        addressField = new TextField(); addressField.setPrefHeight(40);
        addressField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        infoGrid.add(nameLabel, 0, 0); infoGrid.add(nameField, 1, 0, 2, 1);
        infoGrid.add(emailLabel, 0, 1); infoGrid.add(emailField, 1, 1, 2, 1);
        infoGrid.add(contactLabel, 0, 2); infoGrid.add(contactField, 1, 2, 2, 1);
        infoGrid.add(addressLabel, 0, 3); infoGrid.add(addressField, 1, 3, 2, 1);
        return infoGrid;
    }

    private HBox createBloodInfoBox() {
        HBox bloodInfoBox = new HBox(30);
        bloodInfoBox.setPadding(new Insets(15));
        bloodInfoBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");
        
        VBox bloodTypeBox = new VBox(5);
        Label bloodTypeLabelTitle = new Label("Blood Type");
        bloodTypeLabelTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        bloodTypeLabelTitle.setTextFill(Color.web("#666666"));
        bloodTypeLabel = new Label("-");
        bloodTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        bloodTypeLabel.setTextFill(Color.web("#667eea"));
        bloodTypeBox.getChildren().addAll(bloodTypeLabelTitle, bloodTypeLabel);
        
        VBox ageBox = new VBox(5);
        Label ageLabelTitle = new Label("Age");
        ageLabelTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        ageLabelTitle.setTextFill(Color.web("#666666"));
        ageLabel = new Label("-");
        ageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        ageLabel.setTextFill(Color.web("#4facfe"));
        ageBox.getChildren().addAll(ageLabelTitle, ageLabel);
        
        VBox eligibilityBox = new VBox(5);
        Label eligibilityLabelTitle = new Label("Status");
        eligibilityLabelTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        eligibilityLabelTitle.setTextFill(Color.web("#666666"));
        eligibilityLabel = new Label("-");
        eligibilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        eligibilityBox.getChildren().addAll(eligibilityLabelTitle, eligibilityLabel);
        
        bloodInfoBox.getChildren().addAll(bloodTypeBox, ageBox, eligibilityBox);
        return bloodInfoBox;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button editButton = new Button("✏️ Edit Info");
        editButton.setPrefWidth(150); editButton.setPrefHeight(45);
        editButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button saveButton = new Button("💾 Save Changes");
        saveButton.setPrefWidth(180); saveButton.setPrefHeight(45);
        saveButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        saveButton.setVisible(false);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(120); cancelButton.setPrefHeight(45);
        cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-font-size: 15px; -fx-background-radius: 8; -fx-cursor: hand;");
        cancelButton.setVisible(false);
        
        buttonBox.getChildren().addAll(editButton, saveButton, cancelButton);
        return buttonBox;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("🩸 BDMS - My Records");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    private void displayDonorInfo() {
        if (currentDonor == null) return;
        donorIdLabel.setText(currentDonor.getDonorId());
        nameField.setText(currentDonor.getFullName()); // FIX: getFullName
        emailField.setText(currentDonor.getEmail());
        contactField.setText(currentDonor.getContactNumber());
        addressField.setText(currentDonor.getAddress() != null ? currentDonor.getAddress() : "");
        bloodTypeLabel.setText(currentDonor.getBloodGroup());
        ageLabel.setText(String.valueOf(currentDonor.getAge()));
        
        // Calculated Eligibility (same as Dashboard logic)
        java.time.LocalDate lastDonation = currentDonor.getLastDonationDate();
        if (lastDonation == null || lastDonation.plusDays(56).isBefore(java.time.LocalDate.now())) {
            eligibilityLabel.setText("Eligible");
            eligibilityLabel.setTextFill(Color.web("#4caf50"));
        } else {
            eligibilityLabel.setText("Wait");
            eligibilityLabel.setTextFill(Color.web("#f44336"));
        }
        
        medicalHistoryArea.setText("None (Confidential)"); // Placeholder as schema doesn't store history
    }
    
    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        contactField.setEditable(editable);
        addressField.setEditable(editable);
        medicalHistoryArea.setEditable(editable);
        String style = editable ? "-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #667eea; -fx-border-width: 2;" : "-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd;";
        nameField.setStyle(style);
        emailField.setStyle(style);
        contactField.setStyle(style);
        addressField.setStyle(style);
    }
}