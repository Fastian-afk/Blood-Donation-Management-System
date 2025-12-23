package app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BloodUsageReportView {
    private Stage stage;
    private TableView<BloodReport> reportTable;
    
    public BloodUsageReportView(Stage stage) {
        this.stage = stage;
    }
    
    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        Label titleLabel = new Label("📊 Blood Usage Report");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Filter Section
        VBox filterSection = createFilterSection();
        
        // Stats Cards
        HBox statsBox = createStatsCards();
        
        // Report Table
        VBox tableSection = createTableSection();
        
        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(20);
        content.getChildren().addAll(titleLabel, filterSection, statsBox, tableSection);
        scrollPane.setContent(content);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        stage.setScene(new javafx.scene.Scene(scrollPane, 500, 650));
    }
    
    private VBox createFilterSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; -fx-border-radius: 10;");
        
        Label sectionLabel = new Label("🔍 Filter Report");
        sectionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox filterBox = new HBox(20);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll("All", "Blood Type Distribution", "Donor Activity", "Hospital Requests", "Inventory Status");
        reportTypeCombo.setValue("All");
        reportTypeCombo.setPrefWidth(200);
        
        ComboBox<String> dateRangeCombo = new ComboBox<>();
        dateRangeCombo.getItems().addAll("Last 7 Days", "Last Month", "Last Quarter", "Last Year");
        dateRangeCombo.setValue("Last Month");
        dateRangeCombo.setPrefWidth(150);
        
        Button generateBtn = new Button("📋 Generate Report");
        generateBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white; -fx-padding: 8px 20px; -fx-background-radius: 5; -fx-cursor: hand;");
        generateBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Generated");
            alert.setContentText("Report generated successfully for: " + reportTypeCombo.getValue());
            alert.showAndWait();
        });
        
        Button exportBtn = new Button("📥 Export PDF");
        exportBtn.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-padding: 8px 20px; -fx-background-radius: 5; -fx-cursor: hand;");
        exportBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Success");
            alert.setContentText("Report exported as PDF successfully!");
            alert.showAndWait();
        });
        
        filterBox.getChildren().addAll(
            new Label("Report Type:"), reportTypeCombo,
            new Label("Date Range:"), dateRangeCombo,
            generateBtn, exportBtn
        );
        
        section.getChildren().addAll(sectionLabel, filterBox);
        return section;
    }
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        
        statsBox.getChildren().addAll(
            createStatCard("Total Units Used", "2,450", "#667eea"),
            createStatCard("O+ Usage", "580", "#f093fb"),
            createStatCard("Average per Donor", "3.2", "#4facfe"),
            createStatCard("Hospitals Served", "45", "#43e97b")
        );
        
        return statsBox;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLbl, valueLbl);
        return card;
    }
    
    private VBox createTableSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; -fx-border-radius: 10;");
        
        Label sectionLabel = new Label("📋 Blood Type Distribution");
        sectionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        reportTable = new TableView<>();
        reportTable.setPrefHeight(350);
        
        TableColumn<BloodReport, String> typeCol = new TableColumn<>("Blood Type");
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().bloodType));
        
        TableColumn<BloodReport, Integer> usedCol = new TableColumn<>("Units Used");
        usedCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().unitsUsed));
        
        TableColumn<BloodReport, Integer> availableCol = new TableColumn<>("Units Available");
        availableCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().available));
        
        TableColumn<BloodReport, Integer> requestsCol = new TableColumn<>("Requests");
        requestsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().requests));
        
        TableColumn<BloodReport, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().status));
        
        // Add delete column
        TableColumn<BloodReport, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<BloodReport, Void>() {
            private final Button deleteBtn = new Button("🗑️ Delete");
            {
                deleteBtn.setStyle("-fx-padding: 5px 10px; -fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    reportTable.getItems().remove(getIndex());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Deleted");
                    alert.setContentText("Row deleted successfully!");
                    alert.showAndWait();
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        
        reportTable.getColumns().addAll(typeCol, usedCol, availableCol, requestsCol, statusCol, actionCol);
        
        ObservableList<BloodReport> data = FXCollections.observableArrayList(
            new BloodReport("O+", 580, 245, 15, "Good"),
            new BloodReport("O-", 320, 98, 8, "Low"),
            new BloodReport("A+", 420, 167, 12, "Good"),
            new BloodReport("A-", 180, 45, 5, "Critical"),
            new BloodReport("B+", 350, 123, 10, "Good"),
            new BloodReport("B-", 200, 56, 4, "Low"),
            new BloodReport("AB+", 280, 89, 7, "Good"),
            new BloodReport("AB-", 140, 34, 3, "Critical")
        );
        reportTable.setItems(data);
        
        // Add Row button
        Button addRowBtn = new Button("➕ Add Row");
        addRowBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-padding: 8px 20px; -fx-background-radius: 5; -fx-cursor: hand;");
        addRowBtn.setOnAction(e -> {
            reportTable.getItems().add(new BloodReport("O+", 0, 0, 0, "New"));
        });
        
        VBox tableContainer = new VBox(10);
        tableContainer.getChildren().addAll(reportTable, addRowBtn);
        
        section.getChildren().addAll(sectionLabel, tableContainer);
        return section;
    }
    
    public static class BloodReport {
        public String bloodType;
        public int unitsUsed;
        public int available;
        public int requests;
        public String status;
        
        public BloodReport(String bloodType, int unitsUsed, int available, int requests, String status) {
            this.bloodType = bloodType;
            this.unitsUsed = unitsUsed;
            this.available = available;
            this.requests = requests;
            this.status = status;
        }
    }
}