package app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import app.Main;

public class HospitalContactBankView {
    private Stage stage;
    
    public HospitalContactBankView(Stage stage) {
        this.stage = stage;
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
        
        Label titleLabel = new Label("📞 Contact Blood Bank");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        
        // Contact Form
        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(30));
        formCard.setMaxWidth(600);
        formCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        ComboBox<String> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(
            "Blood Availability",
            "Emergency Request",
            "Complaint",
            "Feedback",
            "Other"
        );
        subjectCombo.setPrefWidth(400);
        grid.add(new Label("Subject:"), 0, 0);
        grid.add(subjectCombo, 1, 0);
        
        TextField nameField = new TextField();
        nameField.setPromptText("Your Name");
        nameField.setPrefWidth(400);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        
        TextField phoneField = new TextField();
        phoneField.setPromptText("Contact Number");
        phoneField.setPrefWidth(400);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Your message...");
        messageArea.setPrefHeight(150);
        messageArea.setWrapText(true);
        grid.add(new Label("Message:"), 0, 3);
        grid.add(messageArea, 1, 3);
        
        Button sendBtn = new Button("📤 Send Message");
        sendBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-weight: bold;"
        );
        
        sendBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty() || messageArea.getText().isEmpty()) {
                Main.showAlert("Error", "Please fill all fields!", Alert.AlertType.ERROR);
                return;
            }
            
            Main.showAlert("Success", 
                "Message sent to Blood Bank!\n\n" +
                "We will get back to you within 24 hours!",
                Alert.AlertType.INFORMATION);
            
            nameField.clear();
            phoneField.clear();
            messageArea.clear();
            subjectCombo.setValue(null);
        });
        
        formCard.getChildren().addAll(grid, sendBtn);
        
        // Blood Bank Info
        VBox infoCard = new VBox(15);
        infoCard.setPadding(new Insets(30));
        infoCard.setMaxWidth(600);
        infoCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label infoTitle = new Label("🏥 Blood Bank Information");
        infoTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label phone = new Label("📞 Phone: 021-3000-0000");
        Label email = new Label("📧 Email: info@bdms.com");
        Label hours = new Label("🕐 Hours: 24/7");
        Label emergency = new Label("🚨 Emergency: 021-9999-9999");
        
        infoCard.getChildren().addAll(infoTitle, phone, email, hours, emergency);
        
        mainContent.getChildren().addAll(titleLabel, formCard, infoCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("📞 Contact Bank");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
}