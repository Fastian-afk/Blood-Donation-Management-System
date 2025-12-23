package app.ui;

import app.controllers.NotificationController;
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

/**
 * BroadcastNotificationView - UI for UC9: Send Notifications
 * This is a new feature for Admins.
 */

public class BroadcastNotificationView {

    private Stage stage;
    private NotificationController notificationController;
    private ComboBox<String> targetGroupCombo;
    private TextArea messageArea;

    public BroadcastNotificationView(Stage stage) {
        this.stage = stage;
        this.notificationController = new NotificationController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Top Bar
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Main Content
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("📣 Send Broadcast Notification");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333333"));

        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(40));
        formCard.setMaxWidth(700);
        formCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );

        // --- Target Group ---
        Label targetLabel = new Label("Select Target Group:");
        targetLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        targetGroupCombo = new ComboBox<>();
        targetGroupCombo.getItems().addAll(
            "All Donors",
            "All Staff",
            "All Hospitals",
            "--- Urgent Blood Needs ---",
            "O+ Donors", "O- Donors",
            "A+ Donors", "A- Donors",
            "B+ Donors", "B- Donors",
            "AB+ Donors", "AB- Donors"
        );
        targetGroupCombo.setPrefHeight(45);
        targetGroupCombo.setPrefWidth(Double.MAX_VALUE);
        targetGroupCombo.setStyle("-fx-font-size: 14px;");

        // --- Message ---
        Label messageLabel = new Label("Message:");
        messageLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here... (e.g., Urgent need for O- blood)");
        messageArea.setPrefRowCount(6);
        messageArea.setWrapText(true);
        messageArea.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #dddddd; " +
            "-fx-border-radius: 8;"
        );

        // --- Send Button ---
        Button sendButton = new Button("SEND BROADCAST");
        sendButton.setPrefWidth(Double.MAX_VALUE);
        sendButton.setPrefHeight(50);
        sendButton.setStyle(
            "-fx-background-color: #D92525; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );

        // --- Button Action ---
        sendButton.setOnAction(e -> handleSendBroadcast());

        formCard.getChildren().addAll(targetLabel, targetGroupCombo, messageLabel, messageArea, sendButton);
        mainContent.getChildren().addAll(titleLabel, formCard);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 500, 650);
        stage.setScene(scene);
    }

    private void handleSendBroadcast() {
        String targetGroup = targetGroupCombo.getValue();
        String message = messageArea.getText().trim();

        if (targetGroup == null || targetGroup.equals("--- Urgent Blood Needs ---")) {
            Main.showAlert("Error", "Please select a valid target group!", Alert.AlertType.WARNING);
            return;
        }

        if (message.isEmpty()) {
            Main.showAlert("Error", "Please enter a message to send!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Broadcast");
        confirm.setHeaderText("Send message to " + targetGroup + "?");
        confirm.setContentText("Message: " + message);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Call the controller to send the broadcast
                boolean success = notificationController.sendBroadcast(targetGroup, message);
                
                if (success) {
                    Main.showAlert("Success", "Broadcast sent successfully to " + targetGroup + ".", Alert.AlertType.INFORMATION);
                    messageArea.clear();
                    targetGroupCombo.setValue(null);
                } else {
                    Main.showAlert("Error", "Failed to send broadcast! Check console for details.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #D92525;");
        
        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("📣 BDMS - Send Notification");
        titleLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        
        return topBar;
    }
}