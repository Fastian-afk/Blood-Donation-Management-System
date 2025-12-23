package app.ui;

import app.database.DonationDAO;
import app.database.DonorDAO; // Added
import app.models.Donation;
import app.models.Donor;    // Added
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javafx.util.StringConverter;

/**
 * MyDonationsView - FINAL VERSION
 */

public class MyDonationsView {
    
    private Stage stage;
    private TableView<DonationRecord> donationsTable;
    private ObservableList<DonationRecord> allDonations;
    private ObservableList<DonationRecord> filteredDonations;
    private DonationDAO donationDAO;
    private DonorDAO donorDAO; 
    private String currentDonorId;
    
    private DatePicker dateFilter;
    private TextField centerFilter;
    private ComboBox<String> statusFilter;

    public MyDonationsView(Stage stage) {
        this.stage = stage;
        this.donationDAO = new DonationDAO();
        this.donorDAO = new DonorDAO(); // Initialize
        this.allDonations = FXCollections.observableArrayList();
        this.filteredDonations = FXCollections.observableArrayList();
        
        String currentUserId = LoginHelper.getCurrentUserId();
        if (currentUserId != null) {
            this.currentDonorId = donationDAO.findDonorIdByUserId(currentUserId);
            if (this.currentDonorId != null) {
                loadDonationData(); 
            }
        }
    }
    
    private void loadDonationData() {
        if (this.currentDonorId == null) return;

        // 1. Fetch Donor Details to get Blood Type
        Donor donor = donorDAO.findDonorById(this.currentDonorId);
        String bloodType = (donor != null) ? donor.getBloodGroup() : "Unknown";

        // 2. Fetch Donations
        List<Donation> donations = donationDAO.findDonationsByDonorId(this.currentDonorId);
        
        ObservableList<DonationRecord> records = FXCollections.observableArrayList();
        for (Donation d : donations) {
            records.add(new DonationRecord(
                d.getDonationId(), 
                d.getDonationDate().toString(), 
                "Central Bank", 
                d.getStatus(), 
                bloodType, 
                d.getQuantity() + " ml"
            ));
        }
        allDonations = records;
        filteredDonations.addAll(allDonations);
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        
        Label titleLabel = new Label("My Donation History");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333333"));
        
        VBox tableCard = new VBox(15);
        tableCard.setPadding(new Insets(25));
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        
        dateFilter = new DatePicker(); 
        dateFilter.setConverter(new StringConverter<LocalDate>() {
            private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override public String toString(LocalDate date) { return (date != null) ? dateFormatter.format(date) : ""; }
            @Override public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try { return LocalDate.parse(string, dateFormatter); } catch (Exception e) { return null; }
                } return null;
            }
        });
        dateFilter.setPromptText("Filter by date");
        dateFilter.setPrefHeight(40);
        dateFilter.setPrefWidth(180);
        
        centerFilter = new TextField(); 
        centerFilter.setPromptText("🔍 Search by center");
        centerFilter.setPrefWidth(200);
        centerFilter.setPrefHeight(40);
        centerFilter.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #dddddd; -fx-border-radius: 8;");
        
        statusFilter = new ComboBox<>(); 
        statusFilter.getItems().addAll("All Status", "Approved", "Pending", "Rejected");
        statusFilter.setValue("All Status");
        statusFilter.setPrefHeight(40);
        statusFilter.setPrefWidth(150);
        
        Button addButton = new Button("+ Add Donation");
        addButton.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        filterBar.getChildren().addAll(dateFilter, centerFilter, statusFilter, spacer, addButton);
        
        createDonationsTable();
        
        dateFilter.setOnAction(e -> filterDonations()); 
        centerFilter.textProperty().addListener((obs, oldVal, newVal) -> filterDonations());
        statusFilter.setOnAction(e -> filterDonations());
        
        addButton.setOnAction(e -> showAddDonationDialog());
        
        donationsTable.setOnKeyPressed(e -> {
            if ((e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) && 
                donationsTable.getSelectionModel().getSelectedItem() != null) {
                deleteDonation();
            }
        });
        
        tableCard.getChildren().addAll(filterBar, donationsTable);
        mainContent.getChildren().addAll(titleLabel, tableCard);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }
    
    private void filterDonations() {
        LocalDate date = dateFilter.getValue();
        String centerText = centerFilter.getText().toLowerCase();
        String status = statusFilter.getValue();
        
        filteredDonations.clear();
        
        for (DonationRecord record : allDonations) {
            LocalDate recordDate = LocalDate.parse(record.getDonationDate()); 
            
            boolean matchesDate = (date == null || recordDate.equals(date));
            boolean matchesCenter = (centerText.isEmpty() || record.getCenter().toLowerCase().contains(centerText));
            boolean matchesStatus = (status.equals("All Status") || record.getStatus().equals(status));

            if (matchesDate && matchesCenter && matchesStatus) {
                filteredDonations.add(record);
            }
        }
        donationsTable.refresh();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("🩸 BDMS - My Donations");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    @SuppressWarnings("unchecked")
    private void createDonationsTable() {
        donationsTable = new TableView<>();
        donationsTable.setItems(filteredDonations);
        donationsTable.setPrefHeight(450);
        
        TableColumn<DonationRecord, String> idCol = new TableColumn<>("Donation ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("donationId"));
        idCol.setPrefWidth(130);
        
        TableColumn<DonationRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("donationDate"));
        dateCol.setPrefWidth(120);
        
        TableColumn<DonationRecord, String> centerCol = new TableColumn<>("Center");
        centerCol.setCellValueFactory(new PropertyValueFactory<>("center"));
        centerCol.setPrefWidth(200);
        
        TableColumn<DonationRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(column -> new TableCell<DonationRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(item);
                    statusLabel.setPadding(new Insets(5, 10, 5, 10));
                    statusLabel.setStyle(
                        "-fx-background-radius: 5; " +
                        "-fx-font-weight: bold; " +
                        (item.equals("Approved") ? "-fx-background-color: #d4edda; -fx-text-fill: #155724;" :
                         item.equals("Pending") ? "-fx-background-color: #fff3cd; -fx-text-fill: #856404;" :
                         "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;")
                    );
                    setGraphic(statusLabel);
                }
            }
        });
        
        TableColumn<DonationRecord, String> bloodTypeCol = new TableColumn<>("Blood Type");
        bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        bloodTypeCol.setPrefWidth(100);
        
        TableColumn<DonationRecord, String> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        
        TableColumn<DonationRecord, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(180);
        actionsCol.setCellFactory(column -> new TableCell<DonationRecord, Void>() {
            private final Button viewBtn = new Button("View");
            private final Button certBtn = new Button("Certificate");
            
            {
                viewBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                certBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                
                viewBtn.setOnAction(e -> {
                    DonationRecord record = getTableView().getItems().get(getIndex());
                    Main.showAlert("Donation Details", 
                        "ID: " + record.getDonationId() + "\n" +
                        "Date: " + record.getDonationDate() + "\n" +
                        "Center: " + record.getCenter() + "\n" +
                        "Blood Type: " + record.getBloodType() + "\n" +
                        "Quantity: " + record.getQuantity() + "\n" +
                        "Status: " + record.getStatus(),
                        AlertType.INFORMATION);
                });
                
                certBtn.setOnAction(e -> {
                    DonationRecord record = getTableView().getItems().get(getIndex());
                    if (record.getStatus().equals("Approved")) {
                        Main.showAlert("Certificate", "Donation certificate downloaded for " + record.getDonationId(), AlertType.INFORMATION);
                    } else {
                        Main.showAlert("Error", "Certificate only available for approved donations!", AlertType.WARNING);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(8, viewBtn, certBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        donationsTable.getColumns().addAll(idCol, dateCol, centerCol, statusCol, bloodTypeCol, quantityCol, actionsCol);
    }
    
    private void showAddDonationDialog() {
        if (this.currentDonorId == null) {
            Main.showAlert("Error", "Cannot add donation. Donor ID is missing!", AlertType.ERROR);
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Donation Record");
        dialog.setHeaderText("Enter donation details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(15); grid.setPadding(new Insets(20));
        
        TextField idField = new TextField();
        String newUniqueId = "DON-" + System.currentTimeMillis() % 10000;
        idField.setPromptText("Enter new unique ID (e.g., " + newUniqueId + ")");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        
     // Use existing centers for reference
        ComboBox<String> centerBox = new ComboBox<>();
        centerBox.getItems().addAll(
            "CTR-1: Main Blood Bank - Islamabad",
            "CTR-2: PIMS Hospital Blood Bank",
            "CTR-3: Shifa International Hospital",
            "CTR-4: Polyclinic Hospital Blood Bank",
            "CTR-5: CMH Blood Bank",
            "CTR-6: Red Crescent Blood Center"
        );
        centerBox.setValue("CTR-1: Main Blood Bank - Islamabad");
        
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Approved", "Pending", "Rejected");
        statusBox.setValue("Pending");
        
        
        TextField quantityField = new TextField();
        quantityField.setPromptText("450");
        
        grid.add(new Label("Donation ID:"), 0, 0); grid.add(idField, 1, 0);
        grid.add(new Label("Date:"), 0, 1); grid.add(datePicker, 1, 1);
        grid.add(new Label("Center:"), 0, 2); grid.add(centerBox, 1, 2);
        grid.add(new Label("Status:"), 0, 3); grid.add(statusBox, 1, 3);
        grid.add(new Label("Quantity (ml):"), 0, 4); grid.add(quantityField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (idField.getText().isEmpty() || centerBox.getValue().isEmpty() || quantityField.getText().isEmpty()) {
                    Main.showAlert("Error", "Please fill all fields!", AlertType.ERROR);
                    return;
                }
                
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    Donation newDonation = new Donation(this.currentDonorId, quantity);
                    newDonation.setDonationId(idField.getText());
                    newDonation.setDonationDate(datePicker.getValue());
                    newDonation.setStatus(statusBox.getValue());
                    
                    boolean saved = donationDAO.saveDonation(newDonation);
                    
                    if (saved) {
                        // Fetch blood type again to ensure consistency
                        Donor donor = donorDAO.findDonorById(this.currentDonorId);
                        String bType = (donor != null) ? donor.getBloodGroup() : "N/A";

                        DonationRecord newRecord = new DonationRecord(
                            newDonation.getDonationId(),
                            newDonation.getDonationDate().toString(),
                            centerBox.getValue(),
                            newDonation.getStatus(),
                            bType, // Correct blood type
                            newDonation.getQuantity() + " ml"
                        );
                        allDonations.add(0, newRecord);
                        filteredDonations.clear();
                        filteredDonations.addAll(allDonations);
                        donationsTable.refresh();
                        Main.showAlert("Success", "Donation record added successfully!", AlertType.INFORMATION);
                    } else {
                        Main.showAlert("Error", "Failed to save donation record! ID might be duplicated.", AlertType.ERROR);
                    }
                } catch (NumberFormatException e) {
                    Main.showAlert("Error", "Quantity must be a number!", AlertType.ERROR);
                }
            }
        });
    }
    
    private void deleteDonation() {
        DonationRecord selected = donationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Delete Donation");
        confirm.setHeaderText("Are you sure you want to delete this donation record?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (donationDAO.deleteDonation(selected.getDonationId())) {
                    allDonations.remove(selected);
                    filteredDonations.remove(selected);
                    donationsTable.refresh();
                    Main.showAlert("Success", "Donation record deleted!", AlertType.INFORMATION);
                } else {
                    Main.showAlert("Error", "Failed to delete record!", AlertType.ERROR);
                }
            }
        });
    }
    
    public static class DonationRecord {
        private String donationId, donationDate, center, status, bloodType, quantity;
        public DonationRecord(String id, String date, String center, String status, String bg, String qty) {
            this.donationId = id; this.donationDate = date; this.center = center;
            this.status = status; this.bloodType = bg; this.quantity = qty;
        }
        public String getDonationId() { return donationId; }
        public String getDonationDate() { return donationDate; }
        public String getCenter() { return center; }
        public String getStatus() { return status; }
        public String getBloodType() { return bloodType; }
        public String getQuantity() { return quantity; }
    }
}