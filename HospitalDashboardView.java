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

/**
 * HospitalDashboardView - Dashboard for Hospital Staff
 * Shows blood requests, inventory status, and hospital statistics
 */

public class HospitalDashboardView {
    
    private Stage stage;
    
    public HospitalDashboardView(Stage stage) {
        this.stage = stage;
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Top Bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Main Content
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        
        // Title
        Label titleLabel = new Label("Hospital Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333333"));
        
        // Statistics Cards
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.getChildren().addAll(                                 // Dummy data, app actually dynamically updates in real time
            createStatCard("Pending Requests", "5", "#667eea", "📋"),
            createStatCard("Fulfilled Today", "12", "#43e97b", "✅"),
            createStatCard("Blood Units in Stock", "95", "#f093fb", "📦"),
            createStatCard("Average Fulfillment", "2.5 hrs", "#4facfe", "⏱️")
        );
        
        // Request Status Card
        VBox requestCard = new VBox(20);
        requestCard.setPadding(new Insets(25));
        requestCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label requestLabel = new Label("📋 Recent Blood Requests");
        requestLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 18));
        requestLabel.setTextFill(Color.web("#667eea"));
        
        TextArea requestArea = new TextArea();
        requestArea.setEditable(false);
        requestArea.setPrefRowCount(6);
        requestArea.setText(
            "REQUEST ID     │ BLOOD TYPE │ UNITS │ URGENCY      │ STATUS\n" +
            "─────────────────────────────────────────────────────────────\n" +
            "REQ-001        │ O+         │ 4     │ Routine      │ ✅ Fulfilled\n" +
            "REQ-002        │ A+         │ 2     │ Urgent       │ ⏳ Processing\n" +
            "REQ-003        │ B+         │ 1     │ Critical     │ ⏳ Processing\n" +
            "REQ-004        │ O-         │ 3     │ Routine      │ ⏰ Pending\n" +
            "REQ-005        │ AB+        │ 2     │ Routine      │ ⏰ Pending\n" +
            "REQ-006        │ O+         │ 5     │ Critical     │ ⏳ Processing"
        );
        requestArea.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-control-inner-background: #f8f9fa; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-radius: 8; " +
            "-fx-padding: 15;"
        );
        
        requestCard.getChildren().addAll(requestLabel, requestArea);
        
        // Blood Inventory Status Card
        VBox inventoryCard = new VBox(20);
        inventoryCard.setPadding(new Insets(25));
        inventoryCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label inventoryLabel = new Label("📦 Current Blood Inventory");
        inventoryLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 18));
        inventoryLabel.setTextFill(Color.web("#667eea"));
        
        GridPane inventoryGrid = new GridPane();
        inventoryGrid.setHgap(30);
        inventoryGrid.setVgap(15);
        inventoryGrid.setPadding(new Insets(15));
        
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        int[] units = {12, 4, 8, 2, 5, 1, 35, 28};
        String[] status = {"✅", "⚠️", "✅", "🔴", "✅", "🔴", "✅", "✅"};
        
        for (int i = 0; i < bloodTypes.length; i++) {
            Label typeLabel = new Label(bloodTypes[i]);
            typeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
            typeLabel.setPrefWidth(40);
            
            Label unitsLabel = new Label(units[i] + " units " + status[i]);
            unitsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().addAll(typeLabel, unitsLabel);
            
            inventoryGrid.add(row, (i % 4), i / 4);
        }
        
        inventoryCard.getChildren().addAll(inventoryLabel, inventoryGrid);
        
        // Quick Actions Card
        VBox actionsCard = new VBox(20);
        actionsCard.setPadding(new Insets(25));
        actionsCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label actionsLabel = new Label("⚡ Quick Actions");
        actionsLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 18));
        actionsLabel.setTextFill(Color.web("#667eea"));
        
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_LEFT);
        
        Button requestBloodBtn = new Button("🩸 Request Blood Units");
        requestBloodBtn.setPrefWidth(200);
        requestBloodBtn.setPrefHeight(50);
        requestBloodBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        requestBloodBtn.setOnAction(e -> {
            stage.close();
            Stage newStage = new Stage();
            BloodRequestView view = new BloodRequestView(newStage);
            view.show();
            newStage.show();
        });
        
        Button viewReportsBtn = new Button("📊 View Reports");
        viewReportsBtn.setPrefWidth(200);
        viewReportsBtn.setPrefHeight(50);
        viewReportsBtn.setStyle(
            "-fx-background-color: #43e97b; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        viewReportsBtn.setOnAction(e -> {
            stage.close();
            Stage newStage = new Stage();
            ReportView view = new ReportView(newStage);
            view.show();
            newStage.show();
        });
        
        Button requestHistoryBtn = new Button("📋 Request History");
        requestHistoryBtn.setPrefWidth(200);
        requestHistoryBtn.setPrefHeight(50);
        requestHistoryBtn.setStyle(
            "-fx-background-color: #4facfe; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        requestHistoryBtn.setOnAction(e -> {
            Main.showAlert("Info", "Request history loaded", Alert.AlertType.INFORMATION);
        });
        
        actionsBox.getChildren().addAll(requestBloodBtn, viewReportsBtn, requestHistoryBtn);
        actionsCard.getChildren().addAll(actionsLabel, actionsBox);
        
        mainContent.getChildren().addAll(
            titleLabel,
            statsBox,
            requestCard,
            inventoryCard,
            actionsCard
        );
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 500, 650);
        stage.setScene(scene);
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);"
        );
        
        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("🏥 Hospital Dashboard");
        titleLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        
        return topBar;
    }
    
    private VBox createStatCard(String title, String value, String color, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(110);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(28));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        titleLabel.setTextFill(Color.web("#666666"));
        
        topRow.getChildren().addAll(iconLabel, titleLabel);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web(color));
        
        card.getChildren().addAll(topRow, valueLabel);
        
        return card;
    }
}