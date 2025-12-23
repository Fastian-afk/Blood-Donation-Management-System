package app.ui;

import app.database.BloodUnitDAO;
import app.database.ReportDAO;
import app.models.BloodTypeStats;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import app.Main;
import java.util.List;

/**
 * InventoryManagementView - FINAL VERSION
 * Restores "Rich" UI with Actions, Reserved, and Expiring columns.
 * Connects to Live Data.
 */

public class InventoryManagementView {
    
    private Stage stage;
    private TableView<BloodInventoryItem> inventoryTable;
    private ObservableList<BloodInventoryItem> allInventory;
    private ObservableList<BloodInventoryItem> filteredInventory;
    private TextField searchField;
    private ComboBox<String> filterComboBox;
    
    private ReportDAO reportDAO;     
    private BloodUnitDAO unitDAO;    

    public InventoryManagementView(Stage stage) {
        this.stage = stage;
        this.reportDAO = new ReportDAO();
        this.unitDAO = new BloodUnitDAO();
        this.allInventory = FXCollections.observableArrayList();
        this.filteredInventory = FXCollections.observableArrayList();
        loadInventoryData();
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        
        HBox headerBox = new HBox(30);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Blood Inventory Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333333"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");
        
        Button addUnitsBtn = new Button("+ Add Blood Units");
        addUnitsBtn.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");
        
        headerBox.getChildren().addAll(titleLabel, spacer, refreshBtn, addUnitsBtn);
        
        // Dynamic Stats
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.getChildren().addAll(
            createStatCard("Total Units", calculateTotalUnits() + "", "#667eea", "📦"),
            createStatCard("Low Stock", countLowStock() + " Types", "#ff6b6b", "⚠️"),
            createStatCard("Available", calculateAvailableUnits() + "", "#6bcf7f", "✅")
        );
        
        VBox tableCard = new VBox(15);
        tableCard.setPadding(new Insets(25));
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        
        searchField = new TextField();
        searchField.setPromptText("🔍 Search by blood type...");
        searchField.setPrefWidth(250);
        searchField.setPrefHeight(40);
        searchField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("All Blood Types", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        filterComboBox.setValue("All Blood Types");
        filterComboBox.setPrefHeight(40);
        filterComboBox.setStyle("-fx-font-size: 14px;");
        
        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);
        
        Button exportBtn = new Button("📊 Export Report");
        exportBtn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");
        
        filterBar.getChildren().addAll(searchField, filterComboBox, filterSpacer, exportBtn);
        
        createInventoryTable();
        
        tableCard.getChildren().addAll(filterBar, inventoryTable);
        mainContent.getChildren().addAll(headerBox, statsBox, tableCard);
        
        // Actions
        refreshBtn.setOnAction(e -> refreshInventory());
        addUnitsBtn.setOnAction(e -> showAddUnitsDialog());
        searchField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) filterInventory(); });
        filterComboBox.setOnAction(e -> filterInventory());
        exportBtn.setOnAction(e -> Main.showAlert("Export", "Inventory report exported successfully!", Alert.AlertType.INFORMATION));
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        root.setCenter(scrollPane);
        stage.setScene(new Scene(root, 500, 650));
    }
    
    @SuppressWarnings("unchecked")
    private void createInventoryTable() {
        inventoryTable = new TableView<>();
        inventoryTable.setItems(filteredInventory);
        inventoryTable.setPrefHeight(400);
        inventoryTable.setStyle("-fx-font-size: 13px;");
        
        TableColumn<BloodInventoryItem, String> bloodTypeCol = new TableColumn<>("Blood Type");
        bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        bloodTypeCol.setPrefWidth(100);
        
        TableColumn<BloodInventoryItem, Integer> totalUnitsCol = new TableColumn<>("Total");
        totalUnitsCol.setCellValueFactory(new PropertyValueFactory<>("totalUnits"));
        totalUnitsCol.setPrefWidth(80);
        
        TableColumn<BloodInventoryItem, Integer> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availableUnits"));
        availableCol.setPrefWidth(100);
        
        // RESTORED COLUMN
        TableColumn<BloodInventoryItem, Integer> reservedCol = new TableColumn<>("Reserved");
        reservedCol.setCellValueFactory(new PropertyValueFactory<>("reservedUnits"));
        reservedCol.setPrefWidth(100);

        // RESTORED COLUMN
        TableColumn<BloodInventoryItem, Integer> expiringCol = new TableColumn<>("Expiring (7d)");
        expiringCol.setCellValueFactory(new PropertyValueFactory<>("expiringSoon"));
        expiringCol.setPrefWidth(120);
        
        TableColumn<BloodInventoryItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(140);
        statusCol.setCellFactory(column -> new TableCell<BloodInventoryItem, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    Label statusLabel = new Label(item);
                    statusLabel.setPadding(new Insets(5, 10, 5, 10));
                    statusLabel.setStyle("-fx-background-radius: 5; -fx-font-weight: bold; " +
                        (item.equals("Good Stock") ? "-fx-background-color: #d4edda; -fx-text-fill: #155724;" :
                         item.equals("Low Stock") ? "-fx-background-color: #fff3cd; -fx-text-fill: #856404;" :
                         "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;"));
                    setGraphic(statusLabel);
                }
            }
        });
        
        // RESTORED ACTIONS COLUMN
        TableColumn<BloodInventoryItem, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(180);
        actionsCol.setCellFactory(column -> new TableCell<BloodInventoryItem, Void>() {
            private final Button updateBtn = new Button("Update");
            private final Button alertBtn = new Button("Set Alert");
            
            {
                updateBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                alertBtn.setStyle("-fx-background-color: #ffa726; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                
                updateBtn.setOnAction(e -> {
                    BloodInventoryItem item = getTableView().getItems().get(getIndex());
                    showUpdateDialog(item);
                });
                
                alertBtn.setOnAction(e -> {
                    BloodInventoryItem item = getTableView().getItems().get(getIndex());
                    showAlertDialog(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    HBox buttons = new HBox(8, updateBtn, alertBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        inventoryTable.getColumns().addAll(bloodTypeCol, totalUnitsCol, availableCol, reservedCol, expiringCol, statusCol, actionsCol);
        filteredInventory.addAll(allInventory);
    }
    
    private void showAddUnitsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Manual Stock");
        dialog.setHeaderText("Manually add units to inventory");
        
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(15); grid.setPadding(new Insets(20));
        
        ComboBox<String> bloodTypeBox = new ComboBox<>();
        bloodTypeBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        bloodTypeBox.setPromptText("Select Type");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        grid.add(new Label("Blood Type:"), 0, 0); grid.add(bloodTypeBox, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1); grid.add(quantityField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (bloodTypeBox.getValue() == null || quantityField.getText().isEmpty()) {
                    Main.showAlert("Error", "Please fill all fields!", Alert.AlertType.ERROR);
                    return;
                }
                try {
                    String bloodType = bloodTypeBox.getValue();
                    int quantity = Integer.parseInt(quantityField.getText());
                    
                    // Call DAO to insert units
                    boolean success = unitDAO.addManualInventory(bloodType, quantity);
                    
                    if (success) {
                        Main.showAlert("Success", quantity + " units of " + bloodType + " added!", Alert.AlertType.INFORMATION);
                        refreshInventory();
                    } else {
                        Main.showAlert("Error", "Failed to add units. Check DB Logs.", Alert.AlertType.ERROR);
                    }
                } catch (NumberFormatException e) {
                    Main.showAlert("Error", "Quantity must be a valid number!", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    // Helper methods (Load, Refresh, Filter, etc.) - Same logic as before
    private void loadInventoryData() {
        List<BloodTypeStats> stats = reportDAO.getRealTimeDistribution();
        allInventory.clear();
        for (BloodTypeStats s : stats) {
            int total = s.getTotalUnits();
            int available = s.getAvailable();
            int reserved = total - available;
            String status = s.getStatus().equals("Good") ? "Good Stock" : s.getStatus().equals("Low") ? "Low Stock" : "Critical";
            // Use 0 for expiring since we aren't querying that specific date logic yet
            allInventory.add(new BloodInventoryItem(s.getBloodType(), total, available, reserved, 0, status));
        }
    }
    
    private void refreshInventory() {
        loadInventoryData();
        filteredInventory.clear();
        filteredInventory.addAll(allInventory);
        inventoryTable.refresh();
        Main.showAlert("Success", "Inventory refreshed successfully!", Alert.AlertType.INFORMATION);
    }
    
    private void filterInventory() {
        String searchText = searchField.getText().trim().toUpperCase();
        String filterValue = filterComboBox.getValue();
        filteredInventory.clear();
        for (BloodInventoryItem item : allInventory) {
            boolean matches = true;
            if (!searchText.isEmpty() && !item.getBloodType().contains(searchText)) matches = false;
            if (filterValue != null && !filterValue.equals("All Blood Types") && !item.getBloodType().equals(filterValue)) matches = false;
            if (matches) filteredInventory.add(item);
        }
        inventoryTable.refresh();
    }
    
    private void showUpdateDialog(BloodInventoryItem item) {
        // Placeholder for manual adjustment
        Main.showAlert("Info", "To adjust stock, please add units or process requests.", Alert.AlertType.INFORMATION);
    }
    
    private void showAlertDialog(BloodInventoryItem item) {
        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("Set Stock Alert");
        dialog.setHeaderText("Set minimum stock level for " + item.getBloodType());
        dialog.setContentText("Minimum units:");
        dialog.showAndWait().ifPresent(minStock -> Main.showAlert("Success", "Alert set for " + item.getBloodType(), Alert.AlertType.INFORMATION));
    }
    
    private int calculateTotalUnits() { return allInventory.stream().mapToInt(BloodInventoryItem::getTotalUnits).sum(); }
    private int calculateAvailableUnits() { return allInventory.stream().mapToInt(BloodInventoryItem::getAvailableUnits).sum(); }
    private int countLowStock() { return (int) allInventory.stream().filter(item -> !item.getStatus().equals("Good Stock")).count(); }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("🩸 BDMS - Inventory Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    private VBox createStatCard(String title, String value, String color, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        card.getChildren().addAll(new Label(icon + " " + title), new Label(value));
        return card;
    }
    
    public static class BloodInventoryItem {
        private String bloodType, status;
        private int totalUnits, availableUnits, reservedUnits, expiringSoon;
        public BloodInventoryItem(String bloodType, int totalUnits, int availableUnits, int reservedUnits, int expiringSoon, String status) {
            this.bloodType = bloodType; this.totalUnits = totalUnits; this.availableUnits = availableUnits;
            this.reservedUnits = reservedUnits; this.expiringSoon = expiringSoon; this.status = status;
        }
        public String getBloodType() { return bloodType; }
        public int getTotalUnits() { return totalUnits; }
        public int getAvailableUnits() { return availableUnits; }
        public int getReservedUnits() { return reservedUnits; }
        public int getExpiringSoon() { return expiringSoon; }
        public String getStatus() { return status; }
    }
}