package app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import app.Main;
import java.time.LocalDate;

public class RewardsView {
    private Stage stage;
    private Label pointsLabel;
    private ListView<String> rewardsListView;
    private TableView<RewardTransaction> historyTable;
    
    public RewardsView(Stage stage) {
        this.stage = stage;
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Top Bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Main Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        
        // Title
        Label titleLabel = new Label("🎁 Donor Rewards Program");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        // Points Card
        VBox pointsCard = createPointsCard();
        
        // Rewards Catalog
        VBox catalogCard = createRewardsCatalog();
        
        // Transaction History
        VBox historyCard = createTransactionHistory();
        
        mainContent.getChildren().addAll(titleLabel, pointsCard, catalogCard, historyCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
    }
    
    private VBox createPointsCard() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label titleLabel = new Label("⭐ Your Loyalty Points");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        HBox pointsBox = new HBox(40);
        pointsBox.setAlignment(Pos.CENTER_LEFT);
        
        pointsLabel = new Label("2,450");
        pointsLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        VBox infoBox = new VBox(15);
        Label infoLabel1 = new Label("Current Points: 2,450");
        infoLabel1.setStyle("-fx-font-size: 14px;");
        
        Label infoLabel2 = new Label("Points to Next Reward: 500 pts");
        infoLabel2.setStyle("-fx-font-size: 14px;");
        
        Label infoLabel3 = new Label("Lifetime Points: 12,890");
        infoLabel3.setStyle("-fx-font-size: 14px;");
        
        infoBox.getChildren().addAll(infoLabel1, infoLabel2, infoLabel3);
        pointsBox.getChildren().addAll(pointsLabel, infoBox);
        
        card.getChildren().addAll(titleLabel, pointsBox);
        return card;
    }
    
    private VBox createRewardsCatalog() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label titleLabel = new Label("🏆 Available Rewards");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        ObservableList<String> rewards = FXCollections.observableArrayList(
            "Free Health Checkup (500 pts)",
            "Movie Voucher (300 pts)",
            "Restaurant Coupon (400 pts)",
            "Shopping Voucher (600 pts)",
            "Blood Bank Priority (200 pts)",
            "Free Medical Report (450 pts)"
        );
        
        rewardsListView = new ListView<>(rewards);
        rewardsListView.setPrefHeight(200);
        rewardsListView.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0;");
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button redeemBtn = new Button("✅ Redeem Selected");
        redeemBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-weight: bold;"
        );
        
        redeemBtn.setOnAction(e -> {
            String selected = rewardsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int points = Integer.parseInt(selected.replaceAll("[^0-9]", ""));
                if (2450 >= points) {
                    Main.showAlert("Success", 
                        "Reward redeemed successfully!\n\n" +
                        "Reward: " + selected + "\n" +
                        "Points Remaining: " + (2450 - points),
                        Alert.AlertType.INFORMATION);
                    pointsLabel.setText(String.valueOf(2450 - points));
                    historyTable.getItems().add(0, new RewardTransaction(
                        selected.split("\\(")[0].trim(),
                        points,
                        LocalDate.now().toString()
                    ));
                } else {
                    Main.showAlert("Error", "Insufficient points for this reward!", Alert.AlertType.ERROR);
                }
            } else {
                Main.showAlert("Error", "Please select a reward first!", Alert.AlertType.ERROR);
            }
        });
        
        buttonBox.getChildren().add(redeemBtn);
        
        card.getChildren().addAll(titleLabel, rewardsListView, buttonBox);
        return card;
    }
    
    private VBox createTransactionHistory() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label titleLabel = new Label("📊 Redemption History");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        historyTable = new TableView<>();
        historyTable.setPrefHeight(250);
        
        TableColumn<RewardTransaction, String> rewardCol = new TableColumn<>("Reward");
        rewardCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().reward));
        
        TableColumn<RewardTransaction, Integer> pointsCol = new TableColumn<>("Points Used");
        pointsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().points));
        
        TableColumn<RewardTransaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().date));
        
        historyTable.getColumns().addAll(rewardCol, pointsCol, dateCol);
        
        ObservableList<RewardTransaction> data = FXCollections.observableArrayList(
            new RewardTransaction("Free Health Checkup", 500, "2024-11-10"),
            new RewardTransaction("Movie Voucher", 300, "2024-11-05"),
            new RewardTransaction("Restaurant Coupon", 400, "2024-10-28")
        );
        historyTable.setItems(data);
        
        card.getChildren().addAll(titleLabel, historyTable);
        return card;
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);"
        );
        
        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("🩸 BDMS - Rewards Program");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    public static class RewardTransaction {
        public String reward;
        public int points;
        public String date;
        
        public RewardTransaction(String reward, int points, String date) {
            this.reward = reward;
            this.points = points;
            this.date = date;
        }
    }
}