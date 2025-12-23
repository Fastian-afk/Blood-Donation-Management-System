package app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class QualityCheckView {
    private Stage stage;
    
    public QualityCheckView(Stage stage) {
        this.stage = stage;
    }
    
    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        Label titleLabel = new Label("🔬 Quality Check Records");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Check Form Section
        VBox checkFormSection = createCheckFormSection();
        
        // Quality Check History Table
        VBox historySection = createHistorySection();
        
        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(20);
        content.getChildren().addAll(titleLabel, checkFormSection, historySection);
        scrollPane.setContent(content);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        stage.setScene(new javafx.scene.Scene(scrollPane, 500, 650));
    }
    
    private VBox createCheckFormSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; -fx-border-radius: 10;");
        
        Label sectionLabel = new Label("✅ New Quality Check");
        sectionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        TextField unitIdField = new TextField();
        unitIdField.setPromptText("Enter Unit ID");
        grid.add(new Label("Unit ID:"), 0, 0);
        grid.add(unitIdField, 1, 0);
        
        ComboBox<String> bloodTypeCombo = new ComboBox<>();
        bloodTypeCombo.getItems().addAll("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-");
        bloodTypeCombo.setPrefWidth(200);
        grid.add(new Label("Blood Type:"), 0, 1);
        grid.add(bloodTypeCombo, 1, 1);
        
        Spinner<Double> hemoglobinSpinner = new Spinner<>(0, 20, 13.5, 0.1);
        grid.add(new Label("Hemoglobin Level (g/dL):"), 0, 2);
        grid.add(hemoglobinSpinner, 1, 2);
        
        Spinner<Integer> bacterialSpinner = new Spinner<>(0, 1000, 0, 10);
        grid.add(new Label("Bacterial Count:"), 0, 3);
        grid.add(bacterialSpinner, 1, 3);
        
        Spinner<Double> phSpinner = new Spinner<>(6, 8, 6.8, 0.1);
        grid.add(new Label("pH Level:"), 0, 4);
        grid.add(phSpinner, 1, 4);
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("PASS", "FAIL");
        statusCombo.setValue("PASS");
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);
        
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter any notes or failure reasons...");
        reasonArea.setPrefHeight(80);
        reasonArea.setWrapText(true);
        grid.add(new Label("Notes:"), 0, 6);
        grid.add(reasonArea, 1, 6);
        
        Button submitBtn = new Button("✅ Submit Quality Check");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white; -fx-padding: 10px 30px; -fx-background-radius: 8; -fx-cursor: hand;");
        submitBtn.setOnAction(e -> {
            if (unitIdField.getText().isEmpty() || bloodTypeCombo.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Please fill all required fields!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Quality check recorded successfully!");
                alert.showAndWait();
                unitIdField.clear();
                reasonArea.clear();
            }
        });
        
        section.getChildren().addAll(sectionLabel, grid, submitBtn);
        return section;
    }
    
    private VBox createHistorySection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; -fx-border-radius: 10;");
        
        Label sectionLabel = new Label("📋 Quality Check History");
        sectionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        TableView<QualityCheckRecord> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<QualityCheckRecord, String> unitCol = new TableColumn<>("Unit ID");
        unitCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().unitId));
        
        TableColumn<QualityCheckRecord, String> bloodCol = new TableColumn<>("Blood Type");
        bloodCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().bloodType));
        
        TableColumn<QualityCheckRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().status));
        
        TableColumn<QualityCheckRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().date));
        
        table.getColumns().addAll(unitCol, bloodCol, statusCol, dateCol);
        
        ObservableList<QualityCheckRecord> data = FXCollections.observableArrayList(
            new QualityCheckRecord("BU-001", "O+", "PASS", "2024-11-12"),
            new QualityCheckRecord("BU-002", "A+", "PASS", "2024-11-12"),
            new QualityCheckRecord("BU-003", "B+", "FAIL", "2024-11-11")
        );
        table.setItems(data);
        
        section.getChildren().addAll(sectionLabel, table);
        return section;
    }
    
    public static class QualityCheckRecord {
        public String unitId;
        public String bloodType;
        public String status;
        public String date;
        
        public QualityCheckRecord(String unitId, String bloodType, String status, String date) {
            this.unitId = unitId;
            this.bloodType = bloodType;
            this.status = status;
            this.date = date;
        }
    }
}