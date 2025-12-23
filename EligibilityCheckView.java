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

/**
 * EligibilityCheckView - FINAL COMPLETE VERSION
 * - Includes Real Database Search.
 * - Includes Full Health Questionnaire.
 * - Includes Medical Test Entry.
 */

public class EligibilityCheckView {
    
    private Stage stage;
    private TextField donorIdField;
    private Label donorNameLabel;
    private Label donorBloodTypeLabel;
    private Label donorAgeLabel;
    
    // Questionnaire Checkboxes
    private CheckBox q1, q2, q3, q4, q5;
    
    // Medical Fields
    private TextField systolicField, diastolicField, pulseField, tempField, hbField;
    private TextArea notesArea;
    private VBox donorInfoBox;
    
    // Controller
    private DonorController donorController;
    private Donor currentDonor;

    public EligibilityCheckView(Stage stage) {
        this.stage = stage;
        this.donorController = new DonorController();
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Top Bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Main Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        // Title
        Label titleLabel = new Label("Donor Eligibility Check");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333333"));
        
        // Main Form Card
        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(40));
        formCard.setMaxWidth(900);
        formCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        // --- Step 1: Donor Information ---
        Label step1Label = new Label("🩸 Step 1: Donor Information");
        step1Label.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        step1Label.setTextFill(Color.web("#667eea"));
        
        HBox donorSearchBox = new HBox(15);
        donorSearchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Donor ID:");
        searchLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        searchLabel.setPrefWidth(100);
        
        donorIdField = new TextField();
        donorIdField.setPromptText("Enter Donor ID (e.g., DNR-001)");
        donorIdField.setPrefHeight(45);
        donorIdField.setPrefWidth(350);
        donorIdField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        Button searchButton = new Button("🔍 Search");
        searchButton.setPrefHeight(45);
        searchButton.setPrefWidth(120);
        searchButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        donorSearchBox.getChildren().addAll(searchLabel, donorIdField, searchButton);
        
        // Donor Info Display (initially hidden)
        donorInfoBox = new VBox(15);
        donorInfoBox.setPadding(new Insets(20));
        donorInfoBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-radius: 10;");
        donorInfoBox.setVisible(false);
        
        donorNameLabel = new Label("Name: -");
        donorNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        donorBloodTypeLabel = new Label("Blood Type: -");
        donorBloodTypeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        donorAgeLabel = new Label("Age: -");
        donorAgeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        donorInfoBox.getChildren().addAll(donorNameLabel, donorBloodTypeLabel, donorAgeLabel);
        
        // --- Step 2: Health Questionnaire (RESTORED) ---
        Label step2Label = new Label("📋 Step 2: Health Questionnaire");
        step2Label.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        step2Label.setTextFill(Color.web("#667eea"));
        
        VBox questionnaireBox = new VBox(15);
        
        q1 = createQuestionCheckBox("Are you feeling well and healthy today?");
        q2 = createQuestionCheckBox("Have you taken any antibiotics in the last 7 days?");
        q3 = createQuestionCheckBox("Have you had a dental procedure in the last 24 hours?");
        q4 = createQuestionCheckBox("Have you traveled to a malaria-risk area in the last 3 months?");
        q5 = createQuestionCheckBox("Have you had a tattoo or piercing in the last 6 months?");
        
        q1.setSelected(true); 
        
        questionnaireBox.getChildren().addAll(q1, q2, q3, q4, q5);
        
        // --- Step 3: Medical Tests ---
        Label step3Label = new Label("⚕️ Step 3: Medical Tests");
        step3Label.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        step3Label.setTextFill(Color.web("#667eea"));
        
        GridPane testsGrid = new GridPane();
        testsGrid.setHgap(20);
        testsGrid.setVgap(20);
        
        // Blood Pressure
        Label bpLabel = new Label("Blood Pressure (mmHg):");
        bpLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        HBox bpBox = new HBox(10);
        systolicField = new TextField();
        systolicField.setPromptText("Systolic");
        systolicField.setPrefWidth(100);
        systolicField.setStyle(createInputStyle());
        
        Label slashLabel = new Label("/");
        slashLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        diastolicField = new TextField();
        diastolicField.setPromptText("Diastolic");
        diastolicField.setPrefWidth(100);
        diastolicField.setStyle(createInputStyle());
        
        Label bpNormalLabel = new Label("(Normal: 90-140 / 60-90)");
        bpNormalLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        bpNormalLabel.setTextFill(Color.web("#666666"));
        
        bpBox.getChildren().addAll(systolicField, slashLabel, diastolicField, bpNormalLabel);
        
        // Pulse Rate
        Label pulseLabel = new Label("Pulse Rate (bpm):");
        pulseLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        HBox pulseBox = new HBox(10);
        pulseField = new TextField();
        pulseField.setPromptText("Enter pulse rate");
        pulseField.setPrefWidth(150);
        pulseField.setStyle(createInputStyle());
        
        Label pulseNormalLabel = new Label("(Normal: 50-100 bpm)");
        pulseNormalLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        pulseNormalLabel.setTextFill(Color.web("#666666"));
        
        pulseBox.getChildren().addAll(pulseField, pulseNormalLabel);
        
        // Temperature
        Label tempLabel = new Label("Temperature (°C):");
        tempLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        HBox tempBox = new HBox(10);
        tempField = new TextField();
        tempField.setPromptText("Enter temperature");
        tempField.setPrefWidth(150);
        tempField.setStyle(createInputStyle());
        
        Label tempNormalLabel = new Label("(Normal: 36.1-37.2°C)");
        tempNormalLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        tempNormalLabel.setTextFill(Color.web("#666666"));
        
        tempBox.getChildren().addAll(tempField, tempNormalLabel);
        
        // Hemoglobin
        Label hbLabel = new Label("Hemoglobin (g/dL):");
        hbLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        HBox hbBox = new HBox(10);
        hbField = new TextField();
        hbField.setPromptText("Enter hemoglobin level");
        hbField.setPrefWidth(150);
        hbField.setStyle(createInputStyle());
        
        Label hbNormalLabel = new Label("(Min: Male 13.0, Female 12.5)");
        hbNormalLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        hbNormalLabel.setTextFill(Color.web("#666666"));
        
        hbBox.getChildren().addAll(hbField, hbNormalLabel);
        
        testsGrid.add(bpLabel, 0, 0);
        testsGrid.add(bpBox, 0, 1);
        testsGrid.add(pulseLabel, 0, 2);
        testsGrid.add(pulseBox, 0, 3);
        testsGrid.add(tempLabel, 0, 4);
        testsGrid.add(tempBox, 0, 5);
        testsGrid.add(hbLabel, 0, 6);
        testsGrid.add(hbBox, 0, 7);
        
        // --- Step 4: Additional Notes ---
        Label step4Label = new Label("📝 Step 4: Additional Notes");
        step4Label.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        step4Label.setTextFill(Color.web("#667eea"));
        
        notesArea = new TextArea();
        notesArea.setPromptText("Enter any additional observations or concerns...");
        notesArea.setPrefRowCount(4);
        notesArea.setWrapText(true);
        notesArea.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button approveButton = new Button("✅ Mark as Eligible");
        approveButton.setPrefWidth(180);
        approveButton.setPrefHeight(50);
        approveButton.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button deferButton = new Button("⏳ Defer Donor");
        deferButton.setPrefWidth(180);
        deferButton.setPrefHeight(50);
        deferButton.setStyle("-fx-background-color: #ffa726; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button rejectButton = new Button("❌ Reject");
        rejectButton.setPrefWidth(150);
        rejectButton.setPrefHeight(50);
        rejectButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        
        buttonBox.getChildren().addAll(approveButton, deferButton, rejectButton);
        
        // --- Search Logic ---
        searchButton.setOnAction(e -> {
            String donorId = donorIdField.getText().trim();
            if (donorId.isEmpty()) {
                Main.showAlert("Error", "Please enter a Donor ID", Alert.AlertType.WARNING);
                return;
            }
            
            // Real DB Fetch
            currentDonor = donorController.searchDonor(donorId);
            
            if (currentDonor != null) {
                donorNameLabel.setText("Name: " + currentDonor.getFullName());
                donorBloodTypeLabel.setText("Blood Type: " + currentDonor.getBloodGroup());
                donorAgeLabel.setText("Age: " + currentDonor.getAge() + " years");
                donorInfoBox.setVisible(true);
                Main.showAlert("Success", "Donor found!", Alert.AlertType.INFORMATION);
            } else {
                Main.showAlert("Not Found", "Donor not found in system.", Alert.AlertType.ERROR);
                donorInfoBox.setVisible(false);
                currentDonor = null;
            }
        });
        
        // Approve Action
        approveButton.setOnAction(e -> {
            if (currentDonor == null) {
                Main.showAlert("Error", "Please search for a donor first.", Alert.AlertType.ERROR);
                return;
            }
            if (!validateMedicalTests(systolicField, diastolicField, pulseField, tempField, hbField)) {
                return;
            }
            
            // Questionnaire Check: "Feeling well" must be checked. Others must NOT be checked.
            if (!q1.isSelected() || q2.isSelected() || q3.isSelected() || q4.isSelected() || q5.isSelected()) {
                Main.showAlert("Warning", "Questionnaire answers indicate potential ineligibility! Please review.", Alert.AlertType.WARNING);
                return;
            }
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Eligibility");
            confirm.setHeaderText("Mark donor as ELIGIBLE?");
            confirm.setContentText("This will allow the donor to proceed with blood donation!");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Main.showAlert("Success", "Donor marked as ELIGIBLE for donation!", Alert.AlertType.INFORMATION);
                    clearForm();
                }
            });
        });
        
        // Defer Action
        deferButton.setOnAction(e -> showDeferDialog());
        
        // Reject Action
        rejectButton.setOnAction(e -> showRejectDialog());
        
        // Add all components
        formCard.getChildren().addAll(
            step1Label,
            donorSearchBox,
            donorInfoBox,
            new Separator(),
            step2Label,
            questionnaireBox,
            new Separator(),
            step3Label,
            testsGrid,
            new Separator(),
            step4Label,
            notesArea,
            buttonBox
        );
        
        mainContent.getChildren().addAll(titleLabel, formCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 1000, 700));
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        
        Button backButton = new Button("⬅️ Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("🩸 BDMS - Eligibility Check");
        titleLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        
        return topBar;
    }
    
    private CheckBox createQuestionCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        checkBox.setStyle("-fx-text-fill: #333333;");
        return checkBox;
    }
    
    private String createInputStyle() {
        return "-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8; -fx-padding: 10;";
    }
    
    private boolean validateMedicalTests(TextField systolic, TextField diastolic, 
                                          TextField pulse, TextField temp, TextField hb) {
        if (systolic.getText().isEmpty() || diastolic.getText().isEmpty() || pulse.getText().isEmpty() || 
            temp.getText().isEmpty() || hb.getText().isEmpty()) {
            Main.showAlert("Validation Error", "Please fill all medical test fields!", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
    private void showDeferDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Defer Donor");
        dialog.setHeaderText("Specify deferral details");
        VBox content = new VBox(15); content.setPadding(new Insets(20));
        Label reasonLabel = new Label("Deferral Reason:");
        ComboBox<String> reasonBox = new ComboBox<>();
        reasonBox.getItems().addAll("Low Hemoglobin", "High Blood Pressure", "Recent Medication", "Other");
        DatePicker datePicker = new DatePicker();
        content.getChildren().addAll(reasonLabel, reasonBox, new Label("Next Eligible Date:"), datePicker);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) Main.showAlert("Success", "Donor deferred!", Alert.AlertType.INFORMATION);
        });
    }
    
    private void showRejectDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Donor");
        dialog.setHeaderText("Specify rejection reason!");
        dialog.setContentText("Reason:");
        dialog.showAndWait().ifPresent(reason -> {
            if (!reason.isEmpty()) {
                Main.showAlert("Success", "Donor marked as INELIGIBLE!", Alert.AlertType.INFORMATION);
                clearForm();
            }
        });
    }
    
    private void clearForm() {
        donorIdField.clear();
        systolicField.clear();
        diastolicField.clear();
        pulseField.clear();
        tempField.clear();
        hbField.clear();
        notesArea.clear();
        donorInfoBox.setVisible(false);
        // Reset checkboxes
        q1.setSelected(true);
        q2.setSelected(false);
        q3.setSelected(false);
        q4.setSelected(false);
        q5.setSelected(false);
    }
}