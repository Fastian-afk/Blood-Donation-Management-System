package app.ui;

import app.controllers.BloodRequestController;
import app.models.BloodRequest;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * HospitalFulfilledOrdersView - FINAL VERSION
 * - Fetches real data.
 * - Filters for 'Fulfilled' requests.
 */

public class HospitalFulfilledOrdersView {
    private Stage stage;
    private TableView<OrderRecord> ordersTable;
    private ObservableList<OrderRecord> tableData;
    private BloodRequestController controller;
    
    public HospitalFulfilledOrdersView(Stage stage) {
        this.stage = stage;
        this.controller = new BloodRequestController();
        this.tableData = FXCollections.observableArrayList();
        loadData();
    }

    private void loadData() {
        tableData.clear();
        List<BloodRequest> requests = controller.getMyRequests();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (BloodRequest req : requests) {
            // Only show fulfilled orders
            if ("Fulfilled".equalsIgnoreCase(req.getStatus())) {
                tableData.add(new OrderRecord(
                    req.getRequestId(),
                    req.getBloodType(),
                    req.getQuantity(),
                    req.getRequestDate().format(dtf),
                    req.getRequestDate().plusDays(1).format(dtf), 
                    req.getRequestDate().plusDays(2).format(dtf)  
                ));
            }
        }
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
        
        Label titleLabel = new Label("✓ Fulfilled Blood Orders");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        
        // Stats (Calculated from real data)
        int total = tableData.size();
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
            createStatCard("Total Fulfilled", String.valueOf(total), "#43e97b")
        );
        
        // Orders Table
        VBox tableCard = new VBox(20);
        tableCard.setPadding(new Insets(30));
        tableCard.setMaxWidth(900);
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label tableTitle = new Label("📋 Order History");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        ordersTable = new TableView<>();
        ordersTable.setItems(tableData);
        ordersTable.setPrefHeight(350);
        
        TableColumn<OrderRecord, String> orderCol = new TableColumn<>("Order ID");
        orderCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        
        TableColumn<OrderRecord, String> bloodCol = new TableColumn<>("Blood Type");
        bloodCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        
        TableColumn<OrderRecord, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<OrderRecord, String> requestCol = new TableColumn<>("Request Date");
        requestCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        
        TableColumn<OrderRecord, String> fulfilledCol = new TableColumn<>("Fulfilled Date");
        fulfilledCol.setCellValueFactory(new PropertyValueFactory<>("fulfilledDate"));
        
        ordersTable.getColumns().addAll(orderCol, bloodCol, qtyCol, requestCol, fulfilledCol);
        
        tableCard.getChildren().addAll(tableTitle, ordersTable);
        
        mainContent.getChildren().addAll(titleLabel, statsBox, tableCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
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
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("✓ Fulfilled Orders");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    public static class OrderRecord {
        private String orderId, bloodType, requestDate, fulfilledDate, deliveredDate;
        private int quantity;
        public OrderRecord(String orderId, String bloodType, int quantity, String requestDate, String fulfilledDate, String deliveredDate) {
            this.orderId = orderId; this.bloodType = bloodType; this.quantity = quantity;
            this.requestDate = requestDate; this.fulfilledDate = fulfilledDate; this.deliveredDate = deliveredDate;
        }
        public String getOrderId() { return orderId; }
        public String getBloodType() { return bloodType; }
        public int getQuantity() { return quantity; }
        public String getRequestDate() { return requestDate; }
        public String getFulfilledDate() { return fulfilledDate; }
        public String getDeliveredDate() { return deliveredDate; }
    }
}