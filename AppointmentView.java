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
import app.models.Appointment;
import app.controllers.AppointmentController;
import app.controllers.DonorController;
import app.models.Donor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * AppointmentView - FINAL VERSION
 * Fixed errors by using .getFullName() and .getBloodGroup()
 */

public class AppointmentView {
    
    private Stage stage;
    private AppointmentController appointmentController;
    private DonorController donorController;
    
    public AppointmentView(Stage stage) {
        this.stage = stage;
        this.appointmentController = new AppointmentController();
        this.donorController = new DonorController();
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("Schedule Blood Donation Appointment");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333333"));
        
        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(40));
        formCard.setMaxWidth(750);
        formCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label donorIdLabel = new Label("Donor ID *");
        donorIdLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        HBox donorIdBox = new HBox(10);
        TextField donorIdField = new TextField();
        donorIdField.setPromptText("Enter your Donor ID (e.g., DNR-00000001)");
        donorIdField.setPrefHeight(45);
        donorIdField.setPrefWidth(400);
        donorIdField.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #dddddd; " +
            "-fx-border-radius: 8;"
        );
        
        Button verifyBtn = new Button("Verify");
        verifyBtn.setPrefHeight(45);
        verifyBtn.setPrefWidth(100);
        verifyBtn.setStyle(
            "-fx-background-color: #2196f3; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        Label donorStatusLabel = new Label("");
        donorStatusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        
        donorIdBox.getChildren().addAll(donorIdField, verifyBtn);
        
        Label centerLabel = new Label("Select Donation Center *");
        centerLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        ComboBox<String> centerComboBox = new ComboBox<>();
        centerComboBox.getItems().addAll(
        		"CTR-1: Main Blood Bank - Islamabad",
                "CTR-2: PIMS Hospital Blood Bank",
                "CTR-3: Shifa International Hospital",
                "CTR-4: Polyclinic Hospital Blood Bank", 
                "CTR-5: CMH Blood Bank",                
                "CTR-6: Red Crescent Blood Center"       
        );
        centerComboBox.setPromptText("Choose a center");
        centerComboBox.setPrefHeight(45);
        centerComboBox.setMaxWidth(Double.MAX_VALUE);
        centerComboBox.setStyle("-fx-font-size: 14px;");
        
        Label dateLabel = new Label("Select Date *");
        dateLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(1));
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        datePicker.setPrefHeight(45);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        
        Label timeLabel = new Label("Select Time Slot *");
        timeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        GridPane timeSlotGrid = new GridPane();
        timeSlotGrid.setHgap(15);
        timeSlotGrid.setVgap(15);
        timeSlotGrid.setAlignment(Pos.CENTER);
        
        ToggleGroup timeSlotGroup = new ToggleGroup();
        
        String[] timeSlots = {
            "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
            "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM"
        };
        
        int col = 0;
        int row = 0;
        for (String slot : timeSlots) {
            ToggleButton timeBtn = new ToggleButton(slot);
            timeBtn.setToggleGroup(timeSlotGroup);
            timeBtn.setPrefWidth(120);
            timeBtn.setPrefHeight(50);
            timeBtn.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #dddddd; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand;"
            );
            
            timeBtn.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    timeBtn.setStyle(
                        "-fx-background-color: #D92525; " + // Red theme
                        "-fx-border-color: #D92525; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
                    );
                } else {
                    timeBtn.setStyle(
                        "-fx-background-color: white; " +
                        "-fx-border-color: #dddddd; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"
                    );
                }
            });
            
            timeSlotGrid.add(timeBtn, col, row);
            col++;
            if (col > 3) {
                col = 0;
                row++;
            }
        }
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button scheduleButton = new Button("Schedule Appointment");
        scheduleButton.setPrefWidth(220);
        scheduleButton.setPrefHeight(50);
        scheduleButton.setStyle(
            "-fx-background-color: #D92525; " + // Red theme
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        scheduleButton.setDisable(true);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(150);
        cancelButton.setPrefHeight(50);
        cancelButton.setStyle(
            "-fx-background-color: #f0f0f0; " +
            "-fx-text-fill: #333333; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        buttonBox.getChildren().addAll(scheduleButton, cancelButton);
        
        // --- Verify Donor Button Action ---
        verifyBtn.setOnAction(e -> {
            String donorId = donorIdField.getText().trim();
            if (donorId.isEmpty()) {
                Main.showAlert("Error", "Please enter a Donor ID", Alert.AlertType.WARNING);
                return;
            }
            
            Donor donor = donorController.searchDonor(donorId);
            if (donor != null) {
                // Use new getters
                donorStatusLabel.setText("✅ Verified: " + donor.getFullName() + " (" + donor.getBloodGroup() + ")");
                donorStatusLabel.setTextFill(Color.web("#4caf50"));
                scheduleButton.setDisable(false);
            } else {
                donorStatusLabel.setText("❌ Donor not found. Please register first.");
                donorStatusLabel.setTextFill(Color.web("#f44336"));
                scheduleButton.setDisable(true);
                Main.showAlert("Error", "Donor ID not found in system. Please register first.", Alert.AlertType.ERROR);
            }
        });
        
        // Schedule Button Action
        scheduleButton.setOnAction(e -> {
            if (!validateAppointmentInputs(centerComboBox, datePicker, timeSlotGroup)) {
                return;
            }
            
            String donorId = donorIdField.getText().trim();
            String centerSelection = centerComboBox.getValue();
            String centerId = centerSelection.substring(0, 5); // Extract "CTR-1"
            LocalDate date = datePicker.getValue();
            ToggleButton selectedTime = (ToggleButton) timeSlotGroup.getSelectedToggle();
            String timeSlot = selectedTime.getText();
            
            LocalTime time = parseTimeSlot(timeSlot);
            LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);
            
            String appointmentId = appointmentController.scheduleAppointment(donorId, centerId, appointmentDateTime);
            
            if (appointmentId != null) {
                showAppointmentConfirmation(appointmentId, centerSelection, date, timeSlot);
                stage.close();
            } else {
                Main.showAlert("Error", "Failed to schedule appointment. You may already have a pending appointment on this date.", Alert.AlertType.ERROR);
            }
        });
        
        cancelButton.setOnAction(e -> stage.close());
        
        formCard.getChildren().addAll(
            donorIdLabel,
            donorIdBox,
            donorStatusLabel,
            centerLabel,
            centerComboBox,
            dateLabel,
            datePicker,
            timeLabel,
            timeSlotGrid,
            buttonBox
        );
        
        mainContent.getChildren().addAll(titleLabel, formCard);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #D92525;"); // Red theme
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("🩸 BDMS - Schedule Appointment");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        
        return topBar;
    }
    
    private boolean validateAppointmentInputs(ComboBox<String> centerComboBox,
                                               DatePicker datePicker, ToggleGroup timeSlotGroup) {
        
        if (centerComboBox.getValue() == null) {
            Main.showAlert("Validation Error", "Please select a donation center!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (datePicker.getValue() == null) {
            Main.showAlert("Validation Error", "Please select a date!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (timeSlotGroup.getSelectedToggle() == null) {
            Main.showAlert("Validation Error", "Please select a time slot!", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    private LocalTime parseTimeSlot(String timeSlot) {
        String[] parts = timeSlot.split(" ");
        String[] timeParts = parts[0].split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        if (parts[1].equals("PM") && hour != 12) {
            hour += 12;
        } else if (parts[1].equals("AM") && hour == 12) {
            hour = 0;
        }
        
        return LocalTime.of(hour, minute);
    }
    
    private void showAppointmentConfirmation(String appointmentId, String center, LocalDate date, String time) {
        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Appointment Confirmed");
        confirmation.setHeaderText("✅ Your appointment has been scheduled successfully!");
        
        String message = String.format(
            "Appointment ID: %s\n\n" +
            "Center: %s\n" +
            "Date: %s\n" +
            "Time: %s\n\n" +
            "Thank you for your generosity!",
            appointmentId, center, date.toString(), time
        );
        
        confirmation.setContentText(message);
        confirmation.showAndWait();
    }
}