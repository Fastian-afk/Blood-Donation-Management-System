package app.ui;

import app.models.BloodTypeStats;
import app.database.ReportDAO;
import app.controllers.StatsController;
import app.database.HospitalActivityDAO;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import app.Main;
import javafx.scene.paint.Color;
import java.util.Map; 
import java.util.Optional;

/**
 * AdminAnalyticsView - FINAL VERSION
 * - Shows REAL Donor Statistics
 * - Hospital Activity table persists to DB.
 */

public class AdminAnalyticsView {
    private Stage stage;
    private TableView<HospitalStats> hospitalTable;
    private ObservableList<HospitalStats> hospitalData;
    private HospitalActivityDAO hospitalActivityDAO; 

    public AdminAnalyticsView(Stage stage) {
        this.stage = stage;
        this.hospitalActivityDAO = new HospitalActivityDAO();
        this.hospitalData = hospitalActivityDAO.getAllHospitalStats(); 
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setTop(createTopBar());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        
        Label titleLabel = new Label("📊 System Analytics");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        HBox metricsBox = createMetricsBox();
        VBox bloodTypeCard = createBloodTypeAnalytics();
        VBox hospitalCard = createHospitalActivity();
        
        VBox donorCard = createDonorStatistics();
        
        mainContent.getChildren().addAll(titleLabel, metricsBox, bloodTypeCard, hospitalCard, donorCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
    }

    private VBox createHospitalActivity() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label title = new Label("🏥 Hospital Activity");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        hospitalTable = new TableView<>();
        hospitalTable.setPrefHeight(200);
        
        TableColumn<HospitalStats, String> hospitalCol = new TableColumn<>("Hospital");
        hospitalCol.setCellValueFactory(cellData -> cellData.getValue().hospitalProperty());
        
        TableColumn<HospitalStats, Integer> requestsCol = new TableColumn<>("Requests");
        requestsCol.setCellValueFactory(cellData -> cellData.getValue().requestsProperty().asObject());
        
        TableColumn<HospitalStats, Integer> fulfilledCol = new TableColumn<>("Fulfilled");
        fulfilledCol.setCellValueFactory(cellData -> cellData.getValue().fulfilledProperty().asObject());
        
        TableColumn<HospitalStats, String> rateCol = new TableColumn<>("Fulfillment Rate");
        rateCol.setCellValueFactory(cellData -> cellData.getValue().rateProperty());
        
        hospitalTable.getColumns().addAll(hospitalCol, requestsCol, fulfilledCol, rateCol);
        hospitalTable.setItems(hospitalData);
        
        hospitalTable.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                HospitalStats selected = hospitalTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Activity");
                    confirm.setHeaderText("Delete record for " + selected.getHospital() + "?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            if (hospitalActivityDAO.deleteHospitalStats(selected.getActivityId())) {
                                hospitalData.remove(selected);
                            }
                        }
                    });
                }
            }
        });

        Button addBtn = new Button("➕ Add Activity");
        addBtn.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showAddHospitalActivityDialog());
        
        card.getChildren().addAll(title, hospitalTable, addBtn);
        return card;
    }

    private void showAddHospitalActivityDialog() {
        Dialog<HospitalStats> dialog = new Dialog<>();
        dialog.setTitle("Add Hospital Activity");
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField hospitalField = new TextField(); hospitalField.setPromptText("Hospital Name");
        TextField requestsField = new TextField(); requestsField.setPromptText("e.g., 50");
        TextField fulfilledField = new TextField(); fulfilledField.setPromptText("e.g., 48");
        grid.add(new Label("Hospital:"), 0, 0); grid.add(hospitalField, 1, 0);
        grid.add(new Label("Requests:"), 0, 1); grid.add(requestsField, 1, 1);
        grid.add(new Label("Fulfilled:"), 0, 2); grid.add(fulfilledField, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String name = hospitalField.getText();
                    int requests = Integer.parseInt(requestsField.getText());
                    int fulfilled = Integer.parseInt(fulfilledField.getText());
                    double rate = (requests == 0) ? 0.0 : ((double) fulfilled / requests) * 100;
                    String rateStr = String.format("%.1f%%", rate);
                    return new HospitalStats(0, name, requests, fulfilled, rateStr);
                } catch (NumberFormatException e) { return null; }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(newStats -> {
            HospitalStats savedStat = hospitalActivityDAO.addHospitalStats(newStats);
            if (savedStat != null) hospitalData.add(savedStat);
        });
    }

    private VBox createDonorStatistics() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label title = new Label("👥 Donor Statistics (Live Data)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        HBox statsBox = new HBox(40);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setPadding(new Insets(20));
        
        // Fetch real data using StatsController
        StatsController stats = new StatsController();
        Map<String, String> data = stats.getAdminStats();
        
        statsBox.getChildren().addAll(
            createDetailedStat("Total Registered", data.get("donors"), "📊"),
            createDetailedStat("Total Donations", data.get("units"), "💉") 
        );
        
        card.getChildren().addAll(title, statsBox);
        return card;
    }
    
    private VBox createDetailedStat(String title, String value, String icon) {
        VBox box = new VBox(8);
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 24px;");
        
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        box.getChildren().addAll(iconLbl, titleLbl, valueLbl);
        return box;
    }

    public static class HospitalStats {
        private final javafx.beans.property.IntegerProperty activityId;
        private final javafx.beans.property.StringProperty hospital;
        private final javafx.beans.property.StringProperty rate;
        private final javafx.beans.property.IntegerProperty requests;
        private final javafx.beans.property.IntegerProperty fulfilled;
        
        public HospitalStats(int activityId, String hospital, int requests, int fulfilled, String rate) {
            this.activityId = new javafx.beans.property.SimpleIntegerProperty(activityId);
            this.hospital = new javafx.beans.property.SimpleStringProperty(hospital);
            this.requests = new javafx.beans.property.SimpleIntegerProperty(requests);
            this.fulfilled = new javafx.beans.property.SimpleIntegerProperty(fulfilled);
            this.rate = new javafx.beans.property.SimpleStringProperty(rate);
        }
        public int getActivityId() { return activityId.get(); }
        public void setActivityId(int id) { this.activityId.set(id); }
        public String getHospital() { return hospital.get(); }
        public String getRate() { return rate.get(); }
        public int getRequests() { return requests.get(); }
        public int getFulfilled() { return fulfilled.get(); }
        
        public javafx.beans.property.StringProperty hospitalProperty() { return hospital; }
        public javafx.beans.property.StringProperty rateProperty() { return rate; }
        public javafx.beans.property.IntegerProperty requestsProperty() { return requests; }
        public javafx.beans.property.IntegerProperty fulfilledProperty() { return fulfilled; }
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        Button backButton = new Button("⬅️ Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");
        backButton.setOnAction(e -> stage.close());
        Label titleLabel = new Label("📊 Analytics");
        titleLabel.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    private HBox createMetricsBox() {
        HBox box = new HBox(20);
        // Use real data here too!
        StatsController stats = new StatsController();
        Map<String, String> data = stats.getAdminStats();
        
        box.getChildren().addAll(
            createMetricCard("Total Donors", data.get("donors"), "#667eea"),
            createMetricCard("Blood Units", data.get("units"), "#f093fb"),
            createMetricCard("Pending Requests", data.get("pending"), "#4facfe"),
            createMetricCard("Today's Appts", data.get("appointments"), "#43e97b")
        );
        return box;
    }
    
    private VBox createBloodTypeAnalytics() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label title = new Label("🩸 Blood Type Distribution (Live)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(20);
        
        // --- DYNAMIC FETCH ---
        app.database.ReportDAO dao = new app.database.ReportDAO();
        java.util.List<app.models.BloodTypeStats> stats = dao.getRealTimeDistribution();
        
        int index = 0;
        for (app.models.BloodTypeStats s : stats) {
            VBox typeCard = new VBox(8);
            typeCard.setPadding(new Insets(15));
            typeCard.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8;");
            
            Label typeLbl = new Label(s.getBloodType());
            typeLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
            
            Label qtyLbl = new Label(s.getAvailable() + " units");
            qtyLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
            
            // Simple progress bar based on an arbitrary 'healthy' max of 50 units
            ProgressBar progress = new ProgressBar(Math.min(1.0, s.getAvailable() / 50.0));
            progress.setPrefWidth(120);
            
            typeCard.getChildren().addAll(typeLbl, progress, qtyLbl);
            grid.add(typeCard, index % 4, index / 4);
            index++;
        }
        
        card.getChildren().addAll(title, grid);
        return card;
    }
    
    private VBox createMetricCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        Label t = new Label(title); t.setStyle("-fx-text-fill: #666666;");
        Label v = new Label(value); v.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        card.getChildren().addAll(t, v);
        return card;
    }
}