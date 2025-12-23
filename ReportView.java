package app.ui;

import app.database.ReportDAO;
import app.models.BloodTypeStats;
import app.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Optional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ReportView - FINAL VERSION
 */

public class ReportView {
    
    private Stage stage;
    private TableView<BloodTypeStats> bloodTypeTable;
    private ObservableList<BloodTypeStats> allStatsData; 
    private FilteredList<BloodTypeStats> filteredStatsData; 
    private ComboBox<String> reportTypeCombo;
    private ReportDAO reportDAO;

    // References for Multi-Category Export
    private CheckBox donorsCheck, donationsCheck, inventoryCheck, requestsCheck;
    private ComboBox<String> formatCombo;
    private DatePicker startDatePicker, endDatePicker;
    
    public ReportView(Stage stage) {
        this.stage = stage;
        this.reportDAO = new ReportDAO();
        // Initialize lists
        this.allStatsData = FXCollections.observableArrayList();
        this.filteredStatsData = new FilteredList<>(allStatsData, p -> true);
        
        loadDataFromDB(); 
    }
    
    private void loadDataFromDB() {
        allStatsData.setAll(reportDAO.getAllStats()); // Fetch real data
        // filteredStatsData updates automatically because it wraps allStatsData
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setTop(createTopBar());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Reports & Analytics");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333333"));
        
        // Advanced Filter Module
        VBox advancedFilters = createAdvancedFilterModule();
        
        VBox reportContent = new VBox(20);
        reportContent.setPadding(new Insets(25));
        reportContent.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        GridPane statsGrid = createStatsGrid();
        
        Label bloodTypeLabel = new Label("Blood Type Distribution (Live)");
        bloodTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        bloodTypeLabel.setTextFill(Color.web("#333333"));
        
        bloodTypeTable = createBloodTypeTable();
        bloodTypeTable.setPrefHeight(300);
        
        bloodTypeTable.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                deleteSelectedRow();
            }
        });
        
        Button addRowButton = new Button("➕ Add Row");
        addRowButton.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        addRowButton.setOnAction(e -> showAddRowDialog());
        
        HBox tableHeader = new HBox(10, bloodTypeLabel, new Region(), addRowButton);
        HBox.setHgrow(tableHeader.getChildren().get(1), Priority.ALWAYS);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label trendsLabel = new Label("Recent Donation Activity");
        trendsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        trendsLabel.setTextFill(Color.web("#333333"));
        
        TextArea trendsArea = createTrendsArea(reportDAO.getReportStats());
        
        reportContent.getChildren().addAll(
            statsGrid,
            new Separator(),
            tableHeader, 
            bloodTypeTable,
            new Separator(),
            trendsLabel,
            trendsArea
        );
        
        mainContent.getChildren().addAll(titleLabel, advancedFilters, reportContent);
        
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 1000, 700));
    }
    
    private VBox createAdvancedFilterModule() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER_LEFT);

        // ROW 0: Report Type & Date Range
        reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll("All Stock", "Good Stock", "Low Stock", "Critical Stock");
        reportTypeCombo.setValue("All Stock");
        reportTypeCombo.setOnAction(e -> filterTable());
        grid.add(new Label("Report Type:"), 0, 0);
        grid.add(reportTypeCombo, 1, 0);

        startDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        endDatePicker = new DatePicker(LocalDate.now());
        
        grid.add(new Label("Start Date:"), 2, 0);
        grid.add(startDatePicker, 3, 0);
        grid.add(new Label("End Date:"), 4, 0);
        grid.add(endDatePicker, 5, 0);
        
        // ROW 1: Data Categories
        Label categoryLabel = new Label("Export Categories (UC12):");
        categoryLabel.setStyle("-fx-font-weight: bold;");

        donorsCheck = new CheckBox("Donors");
        donationsCheck = new CheckBox("Donations");
        inventoryCheck = new CheckBox("Inventory");
        requestsCheck = new CheckBox("Requests");
        
        formatCombo = new ComboBox<>();
        formatCombo.getItems().addAll("CSV", "PDF", "EXCEL");
        formatCombo.setValue("CSV");
        formatCombo.setPrefWidth(100);

        HBox checksBox = new HBox(15, donorsCheck, donationsCheck, inventoryCheck, requestsCheck);
        
        grid.add(categoryLabel, 0, 1);
        grid.add(checksBox, 1, 1, 4, 1);
        grid.add(new Label("Format:"), 5, 1);
        grid.add(formatCombo, 6, 1);

        // ROW 2: Action Buttons
        Button generateBtn = new Button("📊 Generate Report");
        generateBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button exportBtn = new Button("📥 Export CSV/PDF");
        exportBtn.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold;");

        generateBtn.setOnAction(e -> {
             // Reload data to simulate generating
             loadDataFromDB();
             Main.showAlert("Report Status", "Report generated for " + reportTypeCombo.getValue(), Alert.AlertType.INFORMATION);
        });
        
        exportBtn.setOnAction(e -> {
            List<String> categories = new ArrayList<>();
            if (donorsCheck.isSelected()) categories.add("Donors");
            if (donationsCheck.isSelected()) categories.add("Donations");
            if (inventoryCheck.isSelected()) categories.add("Inventory");
            if (requestsCheck.isSelected()) categories.add("Requests");

            if (categories.isEmpty()) {
                Main.showAlert("Error", "Please select at least one data category to export.", Alert.AlertType.ERROR);
                return;
            }
            Main.showAlert("Export Successful", "Exporting data to " + formatCombo.getValue(), Alert.AlertType.INFORMATION);
        });

        HBox buttonBox = new HBox(10, generateBtn, exportBtn);
        grid.add(buttonBox, 1, 2, 4, 1);

        section.getChildren().add(grid);
        return section;
    }

    private void filterTable() {
        String filter = reportTypeCombo.getValue();
        if (filter == null || filter.equals("All Stock")) {
            filteredStatsData.setPredicate(p -> true);
        } else if (filter.equals("Good Stock")) {
            filteredStatsData.setPredicate(stats -> stats.getStatus().equals("Good"));
        } else if (filter.equals("Low Stock")) {
            filteredStatsData.setPredicate(stats -> stats.getStatus().equals("Low"));
        } else if (filter.equals("Critical Stock")) {
            filteredStatsData.setPredicate(stats -> stats.getStatus().equals("Critical"));
        }
    }
    
    private TableView<BloodTypeStats> createBloodTypeTable() {
        TableView<BloodTypeStats> table = new TableView<>();
        
        // FIX: Use the filtered list connected to the DB data
        table.setItems(filteredStatsData);
        
        TableColumn<BloodTypeStats, String> typeCol = new TableColumn<>("Blood Type");
        typeCol.setCellValueFactory(cellData -> cellData.getValue().bloodTypeProperty());
        typeCol.setPrefWidth(150);
        
        TableColumn<BloodTypeStats, Integer> totalCol = new TableColumn<>("Total Units");
        totalCol.setCellValueFactory(cellData -> cellData.getValue().totalUnitsProperty().asObject());
        totalCol.setPrefWidth(150);
        
        TableColumn<BloodTypeStats, Integer> availCol = new TableColumn<>("Available");
        availCol.setCellValueFactory(cellData -> cellData.getValue().availableProperty().asObject());
        availCol.setPrefWidth(150);
        
        TableColumn<BloodTypeStats, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusCol.setPrefWidth(150);
        
        table.getColumns().addAll(typeCol, totalCol, availCol, statusCol);
        table.setStyle("-fx-font-size: 13px;");
        return table;
    }

    private void deleteSelectedRow() {
        BloodTypeStats selected = bloodTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Row");
        alert.setHeaderText("Delete " + selected.getBloodType() + " row?");
        alert.setContentText("Are you sure? This will remove it from the database.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reportDAO.deleteStat(selected.getStatId())) {
                allStatsData.remove(selected);
                Main.showAlert("Success", "Row deleted successfully!", Alert.AlertType.INFORMATION);
            } else {
                Main.showAlert("Error", "Failed to delete row from database.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAddRowDialog() {
        Dialog<BloodTypeStats> dialog = new Dialog<>();
        dialog.setTitle("Add New Row");
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField bloodTypeField = new TextField(); bloodTypeField.setPromptText("e.g., A+");
        TextField totalUnitsField = new TextField(); totalUnitsField.setPromptText("e.g., 100");
        TextField availableField = new TextField(); availableField.setPromptText("e.g., 80");
        TextField statusField = new TextField(); statusField.setPromptText("e.g., Good");
        
        grid.add(new Label("Blood Type:"), 0, 0); grid.add(bloodTypeField, 1, 0);
        grid.add(new Label("Total Units:"), 0, 1); grid.add(totalUnitsField, 1, 1);
        grid.add(new Label("Available:"), 0, 2); grid.add(availableField, 1, 2);
        grid.add(new Label("Status:"), 0, 3); grid.add(statusField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    return new BloodTypeStats(bloodTypeField.getText(), Integer.parseInt(totalUnitsField.getText()), Integer.parseInt(availableField.getText()), statusField.getText());
                } catch (NumberFormatException e) {
                    Main.showAlert("Error", "Please enter valid numbers for units.", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<BloodTypeStats> result = dialog.showAndWait();
        
        result.ifPresent(newStats -> {
            if (reportDAO.addStat(newStats)) {
                allStatsData.add(newStats);
                Main.showAlert("Success", "New row added and saved!", Alert.AlertType.INFORMATION);
            } else {
                Main.showAlert("Error", "Failed to save row to database!", Alert.AlertType.ERROR);
            }
        });
    }

    private GridPane createStatsGrid() {
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(20);
        statsGrid.setPadding(new Insets(20));
        
        // --- DYNAMIC DATA FETCH ---
        java.util.Map<String, String> data = reportDAO.getReportStats();
        
        // Default to "0" if map is empty or key missing
        String donationsToday = data.getOrDefault("donations_today", "0");
        String totalUnits = data.getOrDefault("total_units", "0");
        String activeDonors = data.getOrDefault("active_donors", "0");
        String unitsStock = data.getOrDefault("units_in_stock", "0");
        String pendingReq = data.getOrDefault("pending_requests", "0");
        String completedDon = data.getOrDefault("completed_donations", "0");
        String critical = data.getOrDefault("critical_shortage", "0");
        String eligible = data.getOrDefault("eligible_donors", "0");

        statsGrid.add(createStatBox("Total Donations Today", donationsToday, "#667eea"), 0, 0);
        statsGrid.add(createStatBox("Total Units Collected", totalUnits, "#f093fb"), 1, 0);
        statsGrid.add(createStatBox("Active Donors", activeDonors, "#4facfe"), 2, 0);
        statsGrid.add(createStatBox("Blood Units in Stock", unitsStock, "#43e97b"), 3, 0);
        
        statsGrid.add(createStatBox("Pending Requests", pendingReq, "#ffa726"), 0, 1);
        statsGrid.add(createStatBox("Completed Donations", completedDon, "#66bb6a"), 1, 1);
        statsGrid.add(createStatBox("Critical Shortage", critical, "#ff6b6b"), 2, 1); // Count of blood types with low stock
        statsGrid.add(createStatBox("Eligible Donors", eligible, "#ab47bc"), 3, 1);
        
        return statsGrid;
    }

    private TextArea createTrendsArea(java.util.Map<String, String> data) {
        TextArea trendsArea = new TextArea();
        trendsArea.setEditable(false);
        trendsArea.setPrefRowCount(5);
        
        // Fetch values or default to 0
        String donationsToday = data.getOrDefault("donations_today", "0");
        String totalUnits = data.getOrDefault("total_units", "0");
        
        // Dynamic String Construction
        StringBuilder sb = new StringBuilder();
        sb.append("Today's Donation Summary:\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("✅ ").append(donationsToday).append(" successful donations collected today\n");
        sb.append("📦 ").append(totalUnits).append(" total blood units currently in inventory\n");
        sb.append("📊 Active Donors: ").append(data.getOrDefault("active_donors", "0")).append("\n");
        sb.append("🚨 Critical Shortages: ").append(data.getOrDefault("critical_shortage", "0")).append(" blood types\n");

        trendsArea.setText(sb.toString());
        
        trendsArea.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-control-inner-background: #f8f9fa; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-radius: 8;"
        );
        return trendsArea;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("🩸 BDMS - Reports");
        titleLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    private VBox createStatBox(String title, String value, String color) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setPrefWidth(200);
        box.setPrefHeight(80);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 10;");
        box.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        titleLabel.setTextFill(Color.web("#666666"));
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.web(color));
        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }
}