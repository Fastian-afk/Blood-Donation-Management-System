package app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import app.Main;
import app.controllers.SettingsController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AdminSystemSettingsView - FINAL VERSION
 * - "Edit" buttons now open a dialog and save changes to the DB.
 * - Checkboxes save state to the DB.
 */

public class AdminSystemSettingsView {
    private Stage stage;
    private SettingsController settingsController;
    private Map<String, Label> settingLabels = new HashMap<>(); // To update labels after edit

    public AdminSystemSettingsView(Stage stage) {
        this.stage = stage;
        this.settingsController = new SettingsController();
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setTop(createTopBar());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        
        Label titleLabel = new Label("⚙️ System Settings");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        // Load settings from DB
        Map<String, String> currentSettings = settingsController.getSettings();

        VBox donationSettings = createDonationSettings(currentSettings);
        VBox notificationSettings = createNotificationSettings(currentSettings);
        VBox bloodBankSettings = createBloodBankSettings(currentSettings);
        
        mainContent.getChildren().addAll(titleLabel, donationSettings, notificationSettings, bloodBankSettings);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
    }
    
    private VBox createDonationSettings(Map<String, String> settings) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(800);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label sectionTitle = new Label("💉 Donation Settings");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        VBox settingsList = new VBox(15);
        settingsList.getChildren().addAll(
            createSettingRow("min_hemoglobin", "Minimum Hemoglobin", settings.getOrDefault("min_hemoglobin", "12.5 g/dL")),
            createSettingRow("expiry_days", "Blood Unit Expiry Days", settings.getOrDefault("expiry_days", "42 days")),
            createSettingRow("min_age", "Minimum Age", settings.getOrDefault("min_age", "18 years")),
            createSettingRow("max_age", "Maximum Age", settings.getOrDefault("max_age", "65 years")),
            createSettingRow("min_weight", "Minimum Weight", settings.getOrDefault("min_weight", "50 kg")),
            createSettingRow("donation_interval", "Donation Interval", settings.getOrDefault("donation_interval", "56 days"))
        );
        
        card.getChildren().addAll(sectionTitle, settingsList);
        return card;
    }
    
    private VBox createNotificationSettings(Map<String, String> settings) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(800);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label sectionTitle = new Label("🔔 Notification Settings");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        VBox settingsList = new VBox(15);
        
        CheckBox emailNotif = new CheckBox("Enable Email Notifications");
        emailNotif.setSelected(Boolean.parseBoolean(settings.getOrDefault("enable_email", "true")));
        emailNotif.setStyle("-fx-font-size: 14px;");
        
        CheckBox smsNotif = new CheckBox("Enable SMS Reminders");
        smsNotif.setSelected(Boolean.parseBoolean(settings.getOrDefault("enable_sms", "false")));
        smsNotif.setStyle("-fx-font-size: 14px;");
        
        Button saveBtn = new Button("💾 Save Notification Settings");
        saveBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        
        saveBtn.setOnAction(e -> {
            settingsController.updateSetting("enable_email", String.valueOf(emailNotif.isSelected()));
            settingsController.updateSetting("enable_sms", String.valueOf(smsNotif.isSelected()));
            Main.showAlert("Success", "Notification settings saved!", Alert.AlertType.INFORMATION);
        });
        
        settingsList.getChildren().addAll(emailNotif, smsNotif, saveBtn);
        card.getChildren().addAll(sectionTitle, settingsList);
        return card;
    }
    
    private VBox createBloodBankSettings(Map<String, String> settings) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(800);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label sectionTitle = new Label("🩸 Blood Bank Settings");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        VBox settingsList = new VBox(15);
        settingsList.getChildren().addAll(
            createSettingRow("critical_stock", "Critical Stock Level", settings.getOrDefault("critical_stock", "50 units")),
            createSettingRow("low_stock_alert", "Low Stock Alert", settings.getOrDefault("low_stock_alert", "100 units")),
            createSettingRow("storage_temp", "Storage Temperature", settings.getOrDefault("storage_temp", "2-6°C"))
        );
        
        card.getChildren().addAll(sectionTitle, settingsList);
        return card;
    }
    
    private HBox createSettingRow(String settingKey, String title, String currentValue) {
        HBox row = new HBox(20);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 8;");
        row.setAlignment(Pos.CENTER_LEFT);
        
        VBox info = new VBox(5);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label valueLbl = new Label("Current: " + currentValue);
        valueLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        
        // Store the label so we can update it later
        settingLabels.put(settingKey, valueLbl);
        
        info.getChildren().addAll(titleLbl, valueLbl);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button actionBtn = new Button("Edit");
        actionBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-background-radius: 5; -fx-cursor: hand;");
        
        actionBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(currentValue);
            dialog.setTitle("Update Setting");
            dialog.setHeaderText("Update " + title);
            dialog.setContentText("New Value:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newValue -> {
                if (!newValue.trim().isEmpty()) {
                    boolean success = settingsController.updateSetting(settingKey, newValue);
                    if (success) {
                        valueLbl.setText("Current: " + newValue);
                        Main.showAlert("Success", "Setting updated successfully!", Alert.AlertType.INFORMATION);
                    } else {
                        Main.showAlert("Error", "Failed to update setting.", Alert.AlertType.ERROR);
                    }
                }
            });
        });
        
        row.getChildren().addAll(info, spacer, actionBtn);
        return row;
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("⚙️ Settings");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
}