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
 * DistributionView - UI for distributing blood units to hospitals.
 */

public class DistributionView {

    private Stage stage;

    public DistributionView(Stage stage) {
        this.stage = stage;
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
        
        Label titleLabel = new Label("🚚 Blood Unit Distribution");
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

        Label formTitle = new Label("Create New Dispatch");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        formTitle.setTextFill(Color.web("#667eea"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        // Hospital Selection
        Label hospitalLabel = new Label("Hospital / Recipient:");
        ComboBox<String> hospitalCombo = new ComboBox<>();
        hospitalCombo.getItems().addAll(
                "PIMS Hospital", 
                "Shifa International", 
                "CMH Rawalpindi", 
                "Holy Family Hospital",
                "Benazir Bhutto Hospital", 
                "DHQ Hospital"             
            );
        hospitalCombo.setPromptText("Select a hospital");
        hospitalCombo.setPrefWidth(350);
        hospitalCombo.setPrefHeight(45);
        grid.add(hospitalLabel, 0, 0);
        grid.add(hospitalCombo, 1, 0);

        // Request ID
        Label requestIdLabel = new Label("Blood Request ID:");
        TextField requestIdField = new TextField();
        requestIdField.setPromptText("e.g., REQ-12345");
        requestIdField.setPrefHeight(45);
        grid.add(requestIdLabel, 0, 1);
        grid.add(requestIdField, 1, 1);

        // Blood Unit ID
        Label unitIdLabel = new Label("Blood Unit ID(s):");
        TextField unitIdField = new TextField();
        unitIdField.setPromptText("e.g., UNIT-001, UNIT-002");
        unitIdField.setPrefHeight(45);
        grid.add(unitIdLabel, 0, 2);
        grid.add(unitIdField, 1, 2);
        
        // Dispatch Method
        Label transportLabel = new Label("Transport Method:");
        ComboBox<String> transportCombo = new ComboBox<>();
        transportCombo.getItems().addAll("Refrigerated Van", "Emergency Vehicle", "Hospital Pickup");
        transportCombo.setValue("Refrigerated Van");
        transportCombo.setPrefHeight(45);
        grid.add(transportLabel, 0, 3);
        grid.add(transportCombo, 1, 3);

        Button dispatchButton = new Button("Dispatch Blood Units");
        dispatchButton.setPrefWidth(350);
        dispatchButton.setPrefHeight(50);
        dispatchButton.setStyle(
            "-fx-background-color: #D92525; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );

        // Action
        dispatchButton.setOnAction(e -> {
            if (hospitalCombo.getValue() == null || unitIdField.getText().isEmpty()) {
                Main.showAlert("Error", "Please select a hospital and enter Unit IDs.", Alert.AlertType.WARNING);
                return;
            }

            Main.showAlert("Success", "Dispatch created successfully!\n\nUnits: " + unitIdField.getText() +
                                      "\nDestination: " + hospitalCombo.getValue(),
                                      Alert.AlertType.INFORMATION);
            
            // Clear fields
            hospitalCombo.setValue(null);
            requestIdField.clear();
            unitIdField.clear();
        });

        formCard.getChildren().addAll(formTitle, grid, dispatchButton);
        mainContent.getChildren().addAll(titleLabel, formCard);
        
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
        
        Button backButton = new Button("⬅️ Back");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("🚚 Blood Distribution");
        titleLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        
        return topBar;
    }
}