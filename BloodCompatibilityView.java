package app.ui;

import app.controllers.BloodCompatibilityController;
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
 * BloodCompatibilityView - UI for UC5: Match Blood Compatibility
 */

public class BloodCompatibilityView {

    private Stage stage;
    private BloodCompatibilityController compatibilityController;
    private VBox resultsBox;
    private Label resultsTitle;

    public BloodCompatibilityView(Stage stage) {
        this.stage = stage;
        this.compatibilityController = new BloodCompatibilityController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        HBox topBar = createTopBar();
        root.setTop(topBar);

        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("🩸 Blood Compatibility Checker");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333333"));

        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(40));
        formCard.setMaxWidth(500);
        formCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );

        Label recipientLabel = new Label("Select Recipient Blood Type:");
        recipientLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        ComboBox<String> bloodTypeCombo = new ComboBox<>();
        bloodTypeCombo.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        bloodTypeCombo.setPrefHeight(50);
        bloodTypeCombo.setPrefWidth(Double.MAX_VALUE);
        bloodTypeCombo.setStyle("-fx-font-size: 16px;");

        Button checkButton = new Button("Check Compatibility");
        checkButton.setPrefWidth(Double.MAX_VALUE);
        checkButton.setPrefHeight(50);
        checkButton.setStyle(
            "-fx-background-color: #D92525; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );

        resultsBox = new VBox(10);
        resultsBox.setPadding(new Insets(20));
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-radius: 10;"
        );
        
        resultsTitle = new Label("Compatible Donors:");
        resultsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        resultsTitle.setTextFill(Color.web("#333333"));
        
        resultsBox.getChildren().add(resultsTitle);
        resultsBox.setVisible(false);

        checkButton.setOnAction(e -> {
            String recipientType = bloodTypeCombo.getValue();
            if (recipientType == null) {
                Main.showAlert("Error", "Please select a recipient blood type.", Alert.AlertType.WARNING);
                return;
            }

            String[] compatibleDonors = compatibilityController.getCompatibleDonors(recipientType);

            resultsBox.getChildren().clear();
            resultsBox.getChildren().add(resultsTitle);
            resultsTitle.setText("Recipient: " + recipientType);

            FlowPane typesPane = new FlowPane();
            typesPane.setHgap(10);
            typesPane.setVgap(10);
            typesPane.setAlignment(Pos.CENTER);

            for (String donorType : compatibleDonors) {
                typesPane.getChildren().add(createBloodTag(donorType));
            }

            resultsBox.getChildren().add(typesPane);
            resultsBox.setVisible(true);
        });

        formCard.getChildren().addAll(recipientLabel, bloodTypeCombo, checkButton);
        mainContent.getChildren().addAll(titleLabel, formCard, resultsBox);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 500, 650);
        stage.setScene(scene);
    }

    private Label createBloodTag(String bloodType) {
        Label tag = new Label(bloodType);
        tag.setPadding(new Insets(8, 15, 8, 15));
        tag.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tag.setTextFill(Color.WHITE);
        tag.setStyle(
            "-fx-background-color: #D92525; " +
            "-fx-background-radius: 20;"
        );
        return tag;
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
        
        Label titleLabel = new Label("🩸 BDMS - Compatibility Check");
        titleLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        
        return topBar;
    }
}