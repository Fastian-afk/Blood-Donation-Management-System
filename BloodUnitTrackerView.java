package app.ui;

import app.controllers.UnitTrackerController;
import app.models.BloodUnit;
import app.models.UnitLifecycleEvent;
import app.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * BloodUnitTrackerView - UI for UC10: Visualize Blood Unit Lifecycle.
 */

public class BloodUnitTrackerView {

    private Stage stage;
    private UnitTrackerController controller;
    private TextField unitIdField;
    private Label donorInfoLabel, expiryInfoLabel, statusLabel, bloodGroupLabel;
    private ListView<String> eventListView;
    private VBox resultsContainer;

    public BloodUnitTrackerView(Stage stage) {
        this.stage = stage;
        this.controller = new UnitTrackerController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setTop(createTopBar());

        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("🔬 Blood Unit Lifecycle Tracker");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        VBox searchCard = createSearchCard();
        VBox resultsCard = createResultsContainer();

        mainContent.getChildren().addAll(titleLabel, searchCard, resultsContainer);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        stage.setScene(new Scene(root, 900, 700));
    }
    
    private VBox createSearchCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setMaxWidth(700);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        unitIdField = new TextField();
        unitIdField.setPromptText("Enter Blood Unit ID (e.g., BU-001 to BU-050)");
        unitIdField.setPrefWidth(300);
        unitIdField.setPrefHeight(40);
        unitIdField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8;");

        Button trackBtn = new Button("🔍 Track Unit");
        trackBtn.setPrefHeight(40);
        trackBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        trackBtn.setOnAction(e -> fetchUnitDetails(unitIdField.getText()));

        searchBox.getChildren().addAll(new Label("Unit ID:"), unitIdField, trackBtn);
        card.getChildren().add(searchBox);
        return card;
    }

    private VBox createResultsContainer() {
        resultsContainer = new VBox(20);
        resultsContainer.setMaxWidth(700);
        resultsContainer.setPadding(new Insets(25));
        resultsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #dee2e6; -fx-border-radius: 15;");
        resultsContainer.setVisible(false);
        
        // Key Info Display
        bloodGroupLabel = new Label("Blood Group: N/A");
        bloodGroupLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        bloodGroupLabel.setTextFill(Color.web("#D92525"));

        donorInfoLabel = new Label("Source Donor: N/A");
        expiryInfoLabel = new Label("Collection/Expiry: N/A");
        statusLabel = new Label("Current Status: UNKNOWN");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox infoBox = new VBox(5, bloodGroupLabel, donorInfoLabel, expiryInfoLabel, statusLabel);
        
        eventListView = new ListView<>();
        eventListView.setPrefHeight(300);
        eventListView.setPlaceholder(new Label("No lifecycle events recorded."));

        resultsContainer.getChildren().addAll(
            new Label("Unit Details:"),
            new Separator(),
            infoBox,
            new Label("\nChronological Events:"),
            eventListView
        );
        return resultsContainer;
    }
    
    private void fetchUnitDetails(String unitId) {
        if (unitId == null || unitId.trim().isEmpty()) {
            Main.showAlert("Error", "Please enter a Unit ID.", Alert.AlertType.WARNING);
            return;
        }

        BloodUnit unit = controller.findUnitById(unitId);

        if (unit != null) {
            resultsContainer.setVisible(true);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // Update Info
            bloodGroupLabel.setText("Blood Group: " + unit.getBloodGroup());
            donorInfoLabel.setText("Donation ID: " + unit.getDonationId() + " | Center: " + unit.getCenterId());
            expiryInfoLabel.setText("Collected: " + unit.getCollectionDate().format(dtf) + " | Expires: " + unit.getExpiryDate().format(dtf));
            statusLabel.setText("Current Status: " + unit.getStatus().toUpperCase());
            
            // Color coding status
            if ("AVAILABLE".equalsIgnoreCase(unit.getStatus())) {
                statusLabel.setTextFill(Color.GREEN);
            } else if ("EXPIRED".equalsIgnoreCase(unit.getStatus())) {
                statusLabel.setTextFill(Color.RED);
            } else {
                statusLabel.setTextFill(Color.ORANGE);
            }

            // Load Events
            List<UnitLifecycleEvent> events = controller.getUnitLifecycle(unitId);
            ObservableList<String> eventStrings = FXCollections.observableArrayList();
            
            for (UnitLifecycleEvent event : events) {
                eventStrings.add(event.getTimestamp() + " | " + event.getDescription() + " (By: " + event.getActionBy() + ")");
            }
            
            eventListView.setItems(eventStrings);
            
        } else {
            Main.showAlert("Not Found", "Blood Unit ID not found in the system.", Alert.AlertType.ERROR);
            resultsContainer.setVisible(false);
        }
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("⬅️ Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("🔬 Unit Tracker");
        titleLabel.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
}