package app.ui;

import app.controllers.DonorController;
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

/**
 * DonorRegistrationView - UI for UC1: Register Donor
 * UPDATED to match new schema and DonorController.
 * Now collects username, password, gender, and age.
 */

public class DonorRegistrationView {
    
    private Stage stage;
    private DonorController donorController;
    
    public DonorRegistrationView(Stage stage) {
        this.stage = stage;
        this.donorController = new DonorController();
    }
    
    public void show() {
        // Root Container with Scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Title Section
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Button backButton = new Button("← Back to Login");
        backButton.setStyle(
            "-fx-background-color: #e0e0e0; " +
            "-fx-text-fill: #333333; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("New Donor Registration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333333"));
        
        titleBox.getChildren().addAll(backButton, titleLabel);
        
        // Registration Form Card
        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(40));
        formCard.setMaxWidth(800);
        formCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        // --- Section 1: Login Credentials ---
        Label loginInfoLabel = new Label("🔑 Account Information");
        loginInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        loginInfoLabel.setTextFill(Color.web("#667eea"));
        
        // Email Field (will also be used as username)
        VBox emailBox = createFormField("Email Address *", "example@email.com (This will be your username)");
        TextField emailField = (TextField) ((VBox) emailBox.getChildren().get(1)).getChildren().get(0);
        
        // Password Field
        VBox passBox = createFormField("Password *", "Create a secure password!");
        // Convert TextField to PasswordField
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a secure password!");
        passwordField.setPrefHeight(45);
        passwordField.setStyle(createInputStyle());
        ((VBox) passBox.getChildren().get(1)).getChildren().set(0, passwordField); // Replace
        

        // --- Section 2: Personal Information ---
        Label personalInfoLabel = new Label("👤 Personal Information");
        personalInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        personalInfoLabel.setTextFill(Color.web("#667eea"));
        
        // Name Field
        VBox nameBox = createFormField("Full Name *", "Enter your full name");
        TextField nameField = (TextField) ((VBox) nameBox.getChildren().get(1)).getChildren().get(0);
        
        // Contact Number Field
        VBox contactBox = createFormField("Contact Number *", "+92 3XX XXXXXXX");
        TextField contactField = (TextField) ((VBox) contactBox.getChildren().get(1)).getChildren().get(0);
        
        // --- Section 3: Medical Information ---
        Label medicalLabel = new Label("⚕️ Medical Information");
        medicalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        medicalLabel.setTextFill(Color.web("#667eea"));
        
        // Grid for DOB, Gender, Blood Type
        GridPane medicalGrid = new GridPane();
        medicalGrid.setHgap(20);
        medicalGrid.setVgap(15);
        
        // Date of Birth
        Label dobLabel = new Label("Date of Birth *");
        dobLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPrefHeight(45);
        dobPicker.setPrefWidth(250);
        dobPicker.setStyle(createInputStyle());
        medicalGrid.add(dobLabel, 0, 0);
        medicalGrid.add(dobPicker, 1, 0);
        
        // Gender
        Label genderLabel = new Label("Gender *");
        genderLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPrefHeight(45);
        genderComboBox.setPrefWidth(250);
        genderComboBox.setStyle("-fx-font-size: 14px;");
        medicalGrid.add(genderLabel, 0, 1);
        medicalGrid.add(genderComboBox, 1, 1);
        
        // Blood Type Selection
        Label bloodTypeLabel = new Label("Blood Group *");
        bloodTypeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        HBox bloodTypeBox = new HBox(10);
        ToggleGroup bloodTypeGroup = new ToggleGroup();
        
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String type : bloodTypes) {
            ToggleButton btn = new ToggleButton(type);
            btn.setToggleGroup(bloodTypeGroup);
            btn.setPrefWidth(60); // Smaller buttons
            btn.setPrefHeight(40);
            btn.setStyle(
                "-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-cursor: hand;"
            );
            btn.selectedProperty().addListener((obs, oldVal, newVal) -> 
                btn.setStyle(newVal ? createInputStyleSelected() : createInputStyleToggle())
            );
            bloodTypeBox.getChildren().add(btn);
        }
        
        // Address Section
        Label addressLabel = new Label("🏠 Address Information");
        addressLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 18));
        addressLabel.setTextFill(Color.web("#667eea"));
        
        VBox addressBox = createTextAreaField("Complete Address", "Enter your complete address");
        TextArea addressField = (TextArea) ((VBox) addressBox.getChildren().get(1)).getChildren().get(0);
        
        // Terms and Conditions
        CheckBox termsCheckBox = new CheckBox("I agree to the terms and conditions of BDMS and consent to donate blood.");
        termsCheckBox.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        termsCheckBox.setTextFill(Color.web("#666666"));
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button registerButton = new Button("Register Account");
        registerButton.setPrefWidth(200);
        registerButton.setPrefHeight(50);
        registerButton.setStyle(
            "-fx-background-color: #D92525; " + // Red theme
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        Button clearButton = new Button("Clear Form");
        clearButton.setPrefWidth(150);
        clearButton.setPrefHeight(50);
        clearButton.setStyle(
            "-fx-background-color: #f0f0f0; " +
            "-fx-text-fill: #333333; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        buttonBox.getChildren().addAll(registerButton, clearButton);
        
        // Register Button Action
        registerButton.setOnAction(e -> {
            if (!validateInputs(nameField, emailField, passwordField, contactField, bloodTypeGroup, dobPicker, genderComboBox, termsCheckBox)) {
                return;
            }
            
            ToggleButton selectedBloodType = (ToggleButton) bloodTypeGroup.getSelectedToggle();
            
            // Call controller to register donor (with new schema fields)
            boolean success = donorController.registerDonor(
                nameField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText().trim(),
                contactField.getText().trim(),
                selectedBloodType.getText(),
                dobPicker.getValue(),
                addressField.getText().trim(),
                genderComboBox.getValue()
            );
            
            if (success) {
                Main.showAlert("Success", "Donor registered successfully!\nYou can now log in with your email and password.", Alert.AlertType.INFORMATION);
                clearForm(nameField, emailField, passwordField, contactField, bloodTypeGroup, dobPicker, addressField, genderComboBox, termsCheckBox);
                stage.close();
            } else {
                Main.showAlert("Error", "Failed to register donor! The email may already be in use.", Alert.AlertType.ERROR);
            }
        });
        
        // Clear Button Action
        clearButton.setOnAction(e -> {
            clearForm(nameField, emailField, passwordField, contactField, bloodTypeGroup, dobPicker, addressField, genderComboBox, termsCheckBox);
        });
        
        // Add all components to form card
        formCard.getChildren().addAll(
            loginInfoLabel,
            emailBox,
            passBox,
            new Separator(),
            personalInfoLabel,
            nameBox,
            contactBox,
            new Separator(),
            medicalLabel,
            medicalGrid,
            bloodTypeLabel,
            bloodTypeBox,
            new Separator(),
            addressLabel,
            addressBox,
            new Separator(),
            termsCheckBox,
            buttonBox
        );
        
        root.getChildren().addAll(titleBox, formCard);
        scrollPane.setContent(root);
        
        Scene scene = new Scene(scrollPane, 500, 650);
        stage.setScene(scene);
    }
    
    private String createInputStyle() {
        return "-fx-font-size: 14px; " +
               "-fx-background-radius: 8; " +
               "-fx-border-color: #dddddd; " +
               "-fx-border-radius: 8; " +
               "-fx-border-width: 1;";
    }
    
    private String createInputStyleToggle() {
        return "-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 8; " +
               "-fx-background-radius: 8; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-cursor: hand;";
    }
    
    private String createInputStyleSelected() {
        return "-fx-background-color: #D92525; -fx-border-color: #D92525; -fx-border-radius: 8; " +
               "-fx-background-radius: 8; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;";
    }
    
    /**
     * Create Form Field Helper
     */
    
    private VBox createFormField(String labelText, String promptText) {
        VBox box = new VBox(8);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web("#333333"));
        
        VBox fieldBox = new VBox();
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(45);
        field.setStyle(createInputStyle());
        
        fieldBox.getChildren().add(field);
        box.getChildren().addAll(label, fieldBox);
        
        return box;
    }
    
    /**
     * Create TextArea Field Helper
     */
    
    private VBox createTextAreaField(String labelText, String promptText) {
        VBox box = new VBox(8);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web("#333333"));
        
        VBox fieldBox = new VBox();
        TextArea field = new TextArea();
        field.setPromptText(promptText);
        field.setPrefRowCount(3);
        field.setWrapText(true);
        field.setStyle(createInputStyle());
        
        fieldBox.getChildren().add(field);
        box.getChildren().addAll(label, fieldBox);
        
        return box;
    }
    
    /**
     * Validate all inputs
     */
    
    private boolean validateInputs(TextField nameField, TextField emailField, PasswordField passField,
                                     TextField contactField, ToggleGroup bloodTypeGroup,
                                     DatePicker dobPicker, ComboBox<String> genderComboBox, 
                                     CheckBox termsCheckBox) {
        
        if (nameField.getText().trim().isEmpty()) {
            Main.showAlert("Validation Error", "Please enter your name!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            Main.showAlert("Validation Error", "Please enter a valid email address!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (passField.getText().trim().isEmpty()) {
            Main.showAlert("Validation Error", "Please enter a password!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (contactField.getText().trim().isEmpty() || contactField.getText().length() < 10) {
            Main.showAlert("Validation Error", "Please enter a valid contact number!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (bloodTypeGroup.getSelectedToggle() == null) {
            Main.showAlert("Validation Error", "Please select your blood group!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (dobPicker.getValue() == null) {
            Main.showAlert("Validation Error", "Please select your date of birth!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (genderComboBox.getValue() == null) {
            Main.showAlert("Validation Error", "Please select your gender!", Alert.AlertType.WARNING);
            return false;
        }
        
        // Check age
        int age = LocalDate.now().getYear() - dobPicker.getValue().getYear();
        if (age < 18 || age > 65) {
            Main.showAlert("Validation Error", "Donor must be between 18 and 65 years old!", Alert.AlertType.WARNING);
            return false;
        }
        
        if (!termsCheckBox.isSelected()) {
            Main.showAlert("Validation Error", "Please agree to the terms and conditions!", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    /**
     * Clear all form fields
     */
    
    private void clearForm(TextField nameField, TextField emailField, PasswordField passField, TextField contactField,
                           ToggleGroup bloodTypeGroup, DatePicker dobPicker, 
                           TextArea addressField, ComboBox<String> genderComboBox, CheckBox termsCheckBox) {
        
        nameField.clear();
        emailField.clear();
        passField.clear();
        contactField.clear();
        if (bloodTypeGroup.getSelectedToggle() != null) {
            bloodTypeGroup.getSelectedToggle().setSelected(false);
        }
        dobPicker.setValue(null);
        addressField.clear();
        genderComboBox.setValue(null);
        termsCheckBox.setSelected(false);
    }
}