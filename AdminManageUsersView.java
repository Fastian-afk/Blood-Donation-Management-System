package app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import app.Main;

public class AdminManageUsersView {
    private Stage stage;
    private TableView<UserRecord> usersTable;
    
    public AdminManageUsersView(Stage stage) {
        this.stage = stage;
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
        
        Label titleLabel = new Label("👥 Manage Users");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        // Add User Form
        VBox formCard = createAddUserForm();
        
        // Users Table
        VBox tableCard = createUsersTable();
        
        mainContent.getChildren().addAll(titleLabel, formCard, tableCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        
        stage.setScene(new javafx.scene.Scene(root, 500, 650));
    }
    
    private VBox createAddUserForm() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label formTitle = new Label("➕ Add New User");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(250);
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(250);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(250);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Admin", "Staff", "Hospital", "Donor");
        roleCombo.setValue("Donor");
        roleCombo.setPrefWidth(250);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button addBtn = new Button("➕ Add User");
        addBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-weight: bold;"
        );
        
        addBtn.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                Main.showAlert("Error", "Please fill all fields!", Alert.AlertType.ERROR);
                return;
            }
            
            usersTable.getItems().add(0, new UserRecord(
                "USR-" + System.currentTimeMillis(),
                usernameField.getText(),
                emailField.getText(),
                roleCombo.getValue(),
                "Active"
            ));
            
            Main.showAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
        });
        
        Button resetBtn = new Button("🔄 Reset");
        resetBtn.setStyle(
            "-fx-background-color: #ffa726; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-weight: bold;"
        );
        
        resetBtn.setOnAction(e -> {
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
            roleCombo.setValue("Donor");
        });
        
        buttonBox.getChildren().addAll(addBtn, resetBtn);
        
        card.getChildren().addAll(formTitle, grid, buttonBox);
        return card;
    }
    
    private VBox createUsersTable() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(900);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        Label tableTitle = new Label("📋 System Users");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        usersTable = new TableView<>();
        usersTable.setPrefHeight(350);
        
        TableColumn<UserRecord, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().userId));
        
        TableColumn<UserRecord, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().username));
        
        TableColumn<UserRecord, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().email));
        
        TableColumn<UserRecord, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().role));
        
        TableColumn<UserRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().status));
        
        TableColumn<UserRecord, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<UserRecord, Void>() {
            private final HBox hbox = new HBox(5);
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            
            {
                editBtn.setStyle("-fx-padding: 5px 10px; -fx-background-color: #667eea; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px;");
                deleteBtn.setStyle("-fx-padding: 5px 10px; -fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px;");
                
                editBtn.setOnAction(e -> {
                    UserRecord record = getTableView().getItems().get(getIndex());
                    Main.showAlert("Info", "Edit User: " + record.username, Alert.AlertType.INFORMATION);
                });
                
                deleteBtn.setOnAction(e -> {
                    UserRecord record = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete User");
                    confirm.setContentText("Delete user: " + record.username + "?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            usersTable.getItems().remove(getIndex());
                            Main.showAlert("Success", "User deleted!", Alert.AlertType.INFORMATION);
                        }
                    });
                });
                
                hbox.getChildren().addAll(editBtn, deleteBtn);
                hbox.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        usersTable.getColumns().addAll(userIdCol, usernameCol, emailCol, roleCol, statusCol, actionCol);
        
        usersTable.setItems(FXCollections.observableArrayList(
            new UserRecord("USR-001", "admin", "admin@bdms.com", "Admin", "Active"),
            new UserRecord("USR-002", "bloodbank_staff", "staff@bloodbank.com", "Staff", "Active"),
            new UserRecord("USR-003", "hospital_staff", "hospital@hosp.com", "Hospital", "Active"),
            new UserRecord("USR-004", "donor1", "donor1@mail.com", "Donor", "Active")
        ));
        
        card.getChildren().addAll(tableTitle, usersTable);
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
        
        Label titleLabel = new Label("👥 Manage Users");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
    
    public static class UserRecord {
        public String userId, username, email, role, status;
        
        public UserRecord(String userId, String username, String email, String role, String status) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.role = role;
            this.status = status;
        }
    }
}