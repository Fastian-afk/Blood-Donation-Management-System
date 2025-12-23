package app;

import app.controllers.StatsController;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import app.ui.*;
import app.controllers.AuditLogController;
import app.controllers.LoginController;
import app.database.DatabaseConnection;
import app.models.AuditLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import java.util.Optional;

/**
 * BDMS Main Application - FINAL VERSION
 * Team: Imaad Fazal, Rayyan Imran, Saleh Mobashar
 */

public class Main extends Application {
    
    private Stage primaryStage;
    private static Main instance;
    private VBox dashboardContentArea;
    
    // Controller for authentication
    private LoginController loginController;
    
    public Main() {
        instance = this;
    }
    
    public static Main getInstance() {
        return instance;
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.loginController = new LoginController(); // Initialize controller
        
        System.out.println("🔧 Initializing BDMS...");
        DatabaseConnection.getInstance(); // Connect to DB
        
        showLoginScreen(); // Show new login screen
        
        primaryStage.setTitle("BDMS - Blood Donation Management System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(750);
        primaryStage.setResizable(true);
        primaryStage.show();
        
        System.out.println("✅ BDMS Application Started Successfully!");
    }
    
    /**
     * Login now fetches user_id on success.
     */
    
    private void showLoginScreen() {
        BorderPane root = new BorderPane();
        
        // Left Side (Class: login-left-pane)
        VBox leftPane = new VBox(20);
        leftPane.setPadding(new Insets(50));
        leftPane.setAlignment(Pos.CENTER_LEFT);
        leftPane.getStyleClass().add("login-left-pane"); // <--- CSS CLASS
        
        Text logo = new Text("🩸");
        logo.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 80));
        logo.setFill(Color.web("#800000")); 
        
        Label title = new Label("BDMS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        title.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Blood Donation Management System");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
        subtitle.setTextFill(Color.WHITE);
        
        Label motto = new Label("Give Blood, Save Life.");
        motto.setFont(Font.font("Arial", FontWeight.LIGHT, 16));
        motto.setTextFill(Color.WHITE);
        
        leftPane.getChildren().addAll(logo, title, subtitle, new Separator(), motto);
        leftPane.setPrefWidth(500);

        // Right Side
        VBox rightPane = new VBox(25);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(40));
        
        Label loginTitle = new Label("Account Login");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        loginTitle.setTextFill(Color.web("#333333"));
        
        Label roleLabel = new Label("Select Your Role:");
        roleLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Donor", "Staff", "Hospital", "Admin");
        roleComboBox.setValue("Donor");
        roleComboBox.setPrefWidth(350);
        roleComboBox.setPrefHeight(45);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (or Email)");
        usernameField.setPrefWidth(350);
        usernameField.setPrefHeight(45);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(350);
        passwordField.setPrefHeight(45);
        
        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(350);
        loginButton.setPrefHeight(50);
        loginButton.getStyleClass().add("login-button"); // <--- CSS CLASS
        
        loginButton.setOnAction(e -> {
             // ... (Keep existing login logic) ...
             String username = usernameField.getText().trim();
             String password = passwordField.getText().trim();
             String role = roleComboBox.getValue();
             // ... Call controller ...
             String userId = loginController.authenticate(username, password, role);
             if (userId != null) {
                 LoginHelper.login(userId, username, role);
                 showDashboard();
             } else {
                 showAlert("Login Failed", "Invalid credentials.", Alert.AlertType.ERROR);
             }
        });
        
        Hyperlink registerLink = new Hyperlink("New Donor? Register Here");
        registerLink.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        registerLink.setTextFill(Color.web("#395094"));
        registerLink.setOnAction(e -> {
            Stage regStage = new Stage();
            regStage.setTitle("Donor Registration");
            DonorRegistrationView regView = new DonorRegistrationView(regStage);
            regView.show();
            regStage.show();
        });
        
        rightPane.getChildren().addAll(loginTitle, new Separator(), roleLabel, roleComboBox, usernameField, passwordField, loginButton, registerLink);
        
        root.setLeft(leftPane);
        root.setCenter(rightPane);
        
        Scene scene = new Scene(root, 1200, 750);
        // Ensure CSS is added here in start() if not already done
        scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }
    
    /**
     * Updated to use LoginHelper
     */
    private void showDashboard() {
        BorderPane dashboard = new BorderPane();
        // Soft Off-White/Rose tint background
        dashboard.setStyle("-fx-background-color: #fafafa;");
        
        String role = LoginHelper.getCurrentRole();
        String username = LoginHelper.getCurrentUsername();
        
        HBox topBar = createTopBar(username, role);
        dashboard.setTop(topBar);
        
        HBox mainContent = new HBox();
        VBox sidebar = createSidebar(role);
        
        dashboardContentArea = new VBox(20);
        dashboardContentArea.setPadding(new Insets(30));
        dashboardContentArea.setStyle("-fx-background-color: transparent;"); // Transparent so root shows through
        dashboardContentArea.setFillWidth(true);
        
        loadDashboardContent(role, username);
        
        ScrollPane scrollPane = new ScrollPane(dashboardContentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #fafafa; -fx-background-color: transparent;"); // Fix scrollpane gray box
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        
        mainContent.getChildren().addAll(sidebar, scrollPane);
        dashboard.setCenter(mainContent);
        
        Scene scene = new Scene(dashboard, 1200, 750);
        // Ensure CSS is linked just in case
        scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }
    
    /**
     * Updated to use LoginHelper and new schema roles
     */
    private void loadDashboardContent(String role, String username) {
        dashboardContentArea.getChildren().clear();
        
        Label welcomeLabel = new Label("Welcome, " + username + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.web("#333333"));
        
        HBox statsCards = createStatsCards(role);
        
        dashboardContentArea.getChildren().addAll(welcomeLabel, statsCards);
        
       
        if (role.equals("Admin")) {
            dashboardContentArea.getChildren().add(createAdminDashboard());
        } else if (role.equals("Staff")) { 
            dashboardContentArea.getChildren().add(createBloodBankDashboard());
        } else if (role.equals("Hospital")) { 
            dashboardContentArea.getChildren().add(createHospitalDashboard());
        } else if (role.equals("Donor")) {
            dashboardContentArea.getChildren().add(createDonorDashboard(username));
        }
    }
    
 // --- METHOD TO OPEN THE LIST/INBOX ---
    private void openNotificationWindow() {
        Stage stage = new Stage();
        stage.setTitle("User Notifications");
        NotificationView view = new NotificationView(stage);
        view.show();
        stage.show();
    }
    
    private void openBloodUnitTrackerWindow() {
        Stage stage = new Stage();
        stage.setTitle("Blood Unit Lifecycle Tracker");
        BloodUnitTrackerView view = new BloodUnitTrackerView(stage);
        view.show();
        stage.show();
    }

    /**
     * Creates the top bar with correct theme and logout function.
     */
    private HBox createTopBar(String username, String role) {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        // FIX: Navy Blue Gradient Background (High Contrast for White Text)
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, #395094, #2c3e50); " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );
        
        Label logoLabel = new Label("🩸 BDMS");
        logoLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 24));
        logoLabel.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Welcome, " + username + "!");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        userLabel.setTextFill(Color.WHITE); // White text on Navy background
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        // Hover effect for logout
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;"));

        logoutBtn.setOnAction(e -> {
            LoginHelper.logout();
            showLoginScreen();
        });
        
        topBar.getChildren().addAll(logoLabel, spacer, userLabel, logoutBtn);
        
        return topBar;
    }
    
   
    private VBox createSidebar(String role) {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        
        // FIX: Changed background to a subtle White-to-Pink gradient
        sidebar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #ffffff, #ffebee); " + 
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 2, 0);"
        );
        
        Label menuTitle = new Label("MENU");
        menuTitle.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 16));
        menuTitle.setTextFill(Color.web("#666666"));
        
        sidebar.getChildren().add(menuTitle);
        sidebar.getChildren().add(new Separator());
        
        String username = LoginHelper.getCurrentUsername();
        
        if (role.equals("Donor")) {
            sidebar.getChildren().addAll(
                createMenuButton("📋 Dashboard", e -> loadDashboardContent(role, username)),
                createMenuButton("📅 Schedule Appointment", e -> openAppointmentWindow()),
                createMenuButton("📊 My Donations", e -> openMyDonationsWindow()),
                createMenuButton("📄 My Records", e -> openMyRecordsWindow())
            );
        } else if (role.equals("Staff")) {
            sidebar.getChildren().addAll(
                createMenuButton("📋 Dashboard", e -> loadDashboardContent(role, username)),
                createMenuButton("👥 Manage Donors", e -> openMyRecordsWindow()),
                createMenuButton("✅ Eligibility Check", e -> openEligibilityWindow()),
                createMenuButton("📦 Manage Inventory", e -> openInventoryWindow()),
                createMenuButton("🩸 Process Donation", e -> openDonationProcessWindow()),
                createMenuButton("🔍 Unit Tracker", e -> openBloodUnitTrackerWindow()), 
                createMenuButton("🔔 View Notifications", e -> openNotificationWindow()),
                createMenuButton("📊 Reports", e -> openReportsWindow())
            );
        } else if (role.equals("Hospital")) {
            sidebar.getChildren().addAll(
                createMenuButton("📋 Dashboard", e -> loadDashboardContent(role, username)),
                createMenuButton("🩸 Request Blood", e -> openBloodRequestWindow()),
                createMenuButton("📊 Reports", e -> openReportsWindow()),
                createMenuButton("🧬 Blood Compatibility", e -> openBloodCompatibilityWindow())
            );
        } else if (role.equals("Admin")) {
            sidebar.getChildren().addAll(
                createMenuButton("📋 Dashboard", e -> loadDashboardContent(role, username)),
                createMenuButton("👥 Manage Users", e -> openAdminManageUsersWindow()),
                createMenuButton("✅ Approve Donations", e -> openDonationProcessWindow()),
                createMenuButton("📦 Manage Inventory", e -> openInventoryWindow()),
                createMenuButton("🔔 View Notifications", e -> openNotificationWindow()),
                createMenuButton("📣 Send Notification", e -> openBroadcastNotificationWindow()),
                createMenuButton("🔐 System Settings", e -> openAdminSystemSettingsWindow()),
                createMenuButton("🔍 Unit Tracker", e -> openBloodUnitTrackerWindow()), 
                createMenuButton("📊 Generate Reports", e -> openReportsWindow())
            );
        }
        
        return sidebar;
    }
    
    /**
     * Creates the dashboard action buttons with the new theme.
     */
    private Button createDashboardButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setPrefHeight(60);
        
        // STYLE 1: Default (White Card with Navy Border & Text)
        String defaultStyle = 
            "-fx-background-color: white; " +
            "-fx-text-fill: #395094; " + // Navy Text
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #d0d0d0; " + // Visible Grey Border
            "-fx-border-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);";

        // STYLE 2: Hover (Navy Gradient Fill with White Text)
        String hoverStyle = 
            "-fx-background-color: linear-gradient(to right, #395094, #2c3e50); " +
            "-fx-text-fill: white; " + // White Text
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: transparent; " +
            "-fx-border-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 3);";

        btn.setStyle(defaultStyle);

        // Animation Logic
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            btn.setScaleX(1.05); // Slight grow
            btn.setScaleY(1.05);
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(defaultStyle);
            btn.setScaleX(1.0); // Return to normal
            btn.setScaleY(1.0);
        });
        
        btn.setOnAction(e -> action.run());
        return btn;
    }
        
    
    /**
     * Creates the sidebar menu buttons with the new theme.
     */
    private Button createMenuButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setPrefWidth(210);
        btn.setPrefHeight(40);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #333333; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #f0f0f0; " +
                "-fx-text-fill: #D92525; " + // Updated color
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 5;"
            );
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #333333; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 5;"
            );
        });
        
        btn.setOnAction(handler);
        
        return btn;
    }
    
    /**
     * Updated to use new Role names from schema
     */
    /**
     * UPDATED: Generates dynamic stats cards using live database data.
     */
    private HBox createStatsCards(String role) {
        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER_LEFT);
        
        // 1. Instantiate Controller to fetch live data
        app.controllers.StatsController stats = new app.controllers.StatsController();
        
        if (role.equals("Admin") || role.equals("Staff")) {
            // Admin/Staff see system-wide totals
            java.util.Map<String, String> data = stats.getAdminStats();
            
            cards.getChildren().addAll(
                createStatCard("Total Donors", data.get("donors"), "#667eea"),
                createStatCard("Blood Units", data.get("units"), "#f093fb"),
                createStatCard("Pending Requests", data.get("pending"), "#4facfe"),
                createStatCard("Today's Appointments", data.get("appointments"), "#43e97b")
            );
            
        } else if (role.equals("Donor")) {
            // Donors see their personal history
            java.util.Map<String, String> data = stats.getDonorStats();
            
            cards.getChildren().addAll(
                createStatCard("Total Donations", data.get("donations"), "#667eea"),
                createStatCard("Next Appointment", data.get("appointment"), "#f093fb"),
                createStatCard("Blood Type", data.get("bloodType"), "#4facfe"),
                createStatCard("Eligibility", data.get("eligibility"), "#43e97b")
            );
            
        } else if (role.equals("Hospital")) {
            // Hospitals see their specific request status + global stock
            java.util.Map<String, String> data = stats.getHospitalStats();
            
            cards.getChildren().addAll(
                createStatCard("Pending Requests", data.get("pending"), "#667eea"),
                createStatCard("Fulfilled Orders", data.get("fulfilled"), "#43e97b"),
                createStatCard("Blood Units Received", data.get("received"), "#f093fb"),
                createStatCard("Current Stock", data.get("stock"), "#4facfe")
            );
        }
        
        return cards;
    }  
    
    /**
     * Creates a single stat card for the dashboard.
     */
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setPrefWidth(220);
        card.setPrefHeight(110);
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Base Style from CSS
        card.getStyleClass().add("stat-card"); // <--- CSS CLASS
        
        // Dynamic Border (Must stay in Java because 'color' variable changes)
        card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 0 0 0 5;");
        
        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        titleLabel.setTextFill(Color.web("#888888"));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.web(color));
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
      

    // --- Dashboard Panel Creation Methods ---
    
    private VBox createAdminDashboard() {
        VBox adminDash = new VBox(20);
        
        Label sectionLabel = new Label("📊 Admin Overview");
        sectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionLabel.setTextFill(Color.web("#333333"));
        
        HBox manageBox = new HBox(20);
        manageBox.getChildren().addAll(
            createDashboardButton("👥 Manage Users", () -> openAdminManageUsersWindow()),
            createDashboardButton("📋 Pending Approvals", () -> openAdminPendingApprovalsWindow()),
            createDashboardButton("🔐 System Settings", () -> openAdminSystemSettingsWindow()),
            createDashboardButton("📈 Analytics", () -> openAdminAnalyticsWindow())
        );
        
        
        Label auditLabel = new Label("🕐 Recent Activities");
        auditLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        auditLabel.setTextFill(Color.web("#555555"));

        // Controller for audit logs
        AuditLogController auditController = new AuditLogController();

        // TableView for Audit Logs
        TableView<AuditLog> auditTable = new TableView<>();
        auditTable.setPrefHeight(300);
        auditTable.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
        
        TableColumn<AuditLog, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        
        TableColumn<AuditLog, String> userCol = new TableColumn<>("User ID");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userId")); // Shows ID
        
        TableColumn<AuditLog, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        dateCol.setPrefWidth(150);
        
        TableColumn<AuditLog, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300); // Make description wider
        
        auditTable.getColumns().addAll(dateCol, userCol, actionCol, descCol);
        
        // Load data from database
        ObservableList<AuditLog> logData = FXCollections.observableArrayList(auditController.getAllLogs());
        auditTable.setItems(logData);
        
        // Add delete functionality
        auditTable.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                AuditLog selected = auditTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Log");
                    confirm.setHeaderText("Delete log entry " + selected.getLogId() + "?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            if (auditController.deleteLog(selected.getLogId())) {
                                logData.remove(selected);
                            }
                        }
                    });
                }
            }
        });

        Button addLogBtn = new Button("➕ Add Log (Manual)");
        addLogBtn.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        addLogBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("Manual Entry");
            dialog.setTitle("Add Manual Log");
            dialog.setHeaderText("Enter log details");
            dialog.setContentText("Description:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(desc -> {
                // This now uses the correct user_id
                AuditLog newLog = auditController.addLog(LoginHelper.getCurrentUserId(), "Manual Entry", desc);
                if (newLog != null) {
                    logData.add(0, newLog); // Add to top of list
                } else {
                    Main.showAlert("Error", "Failed to add log. Check console.", Alert.AlertType.ERROR);
                }
            });
        });
        
        adminDash.getChildren().addAll(sectionLabel, manageBox, auditLabel, auditTable, addLogBtn);
        return adminDash;
    }
    
    private VBox createBloodBankDashboard() {
        VBox bbDash = new VBox(20);
        
        Label sectionLabel = new Label("🩸 Blood Bank Operations");
        sectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionLabel.setTextFill(Color.web("#333333"));
        
        HBox operationsBox = new HBox(20);
        operationsBox.getChildren().addAll(
            createDashboardButton("✅ Process Donations", () -> openDonationProcessWindow()),
            createDashboardButton("📦 Stock Status", () -> openInventoryWindow()),
            createDashboardButton("🔬 Quality Checks", () -> openQualityCheckWindow()),
            createDashboardButton("🚚 Distribution", () -> openDistributionWindow())
        );
        
        Label inventoryLabel = new Label("📊 Blood Inventory (Live)");
        inventoryLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        inventoryLabel.setTextFill(Color.web("#555555"));
        
        GridPane bloodTypeGrid = new GridPane();
        bloodTypeGrid.setHgap(20);
        bloodTypeGrid.setVgap(20);
        bloodTypeGrid.setPadding(new Insets(10));
        
        // --- DYNAMIC DATA FETCH ---
        app.database.BloodUnitDAO unitDAO = new app.database.BloodUnitDAO();
        java.util.Map<String, Integer> counts = unitDAO.getInventoryCounts();
        
        String[] bloodTypes = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        int col = 0; int row = 0;
        
        for (String type : bloodTypes) {
            // Get count or default to 0
            String count = String.valueOf(counts.getOrDefault(type, 0));
            bloodTypeGrid.add(createBloodTypeCard(type, count), col, row);
            
            col++; 
            if (col >= 4) { col = 0; row++; }
        }
        // --------------------------
        
        bbDash.getChildren().addAll(sectionLabel, operationsBox, inventoryLabel, bloodTypeGrid);
        return bbDash;
    }
    
    private VBox createHospitalDashboard() {
        VBox hospDash = new VBox(20);
        
        Label sectionLabel = new Label("🏥 Hospital Operations");
        sectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionLabel.setTextFill(Color.web("#333333"));
        
        HBox requestBox = new HBox(20);
        requestBox.getChildren().addAll(
            createDashboardButton("🔴 Emergency Request", () -> openBloodRequestWindow()),
            createDashboardButton("📋 My Requests", () -> openHospitalMyRequestsWindow()),
            createDashboardButton("✓ Fulfilled Orders", () -> openHospitalFulfilledOrdersWindow()),
            createDashboardButton("📞 Contact Bank", () -> openHospitalContactBankView())
        );
        
        Label requestsLabel = new Label("📥 Active Requests (Top 3 Urgent)");
        requestsLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        requestsLabel.setTextFill(Color.web("#555555"));
        
        VBox requestsList = new VBox(10);
        
        // --- DYNAMIC DATA FETCH ---
        app.database.BloodRequestDAO reqDAO = new app.database.BloodRequestDAO();
        java.util.List<app.models.BloodRequest> urgentReqs = reqDAO.getUrgentRequests(3);
        
        if (urgentReqs.isEmpty()) {
            Label noReqLabel = new Label("No pending requests.");
            noReqLabel.setTextFill(Color.GRAY);
            requestsList.getChildren().add(noReqLabel);
        } else {
            for (app.models.BloodRequest req : urgentReqs) {
                requestsList.getChildren().add(
                    createRequestCard(req.getBloodType(), req.getQuantity() + " units", req.getUrgency())
                );
            }
        }
        
        
        hospDash.getChildren().addAll(sectionLabel, requestBox, requestsLabel, requestsList);
        return hospDash;
    }
    
    private VBox createDonorDashboard(String username) {
        VBox donorDash = new VBox(20);
        
        Label sectionLabel = new Label("❤️ Your Donation Profile");
        sectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionLabel.setTextFill(Color.web("#333333"));
        
        HBox actionsBox = new HBox(20);
        actionsBox.getChildren().addAll(
            createDashboardButton("📅 Schedule Donation", () -> openAppointmentWindow()),
            createDashboardButton("🩸 Donation History", () -> openMyDonationsWindow()),
            createDashboardButton("📄 Health Records", () -> openMyRecordsWindow()),
            createDashboardButton("🎁 Rewards", () -> openRewardsWindow())
        );
        
        Label historyLabel = new Label("📊 Recent Donation History");
        historyLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        historyLabel.setTextFill(Color.web("#555555"));
        
        VBox donationHistory = new VBox(10);
        
        // --- DYNAMIC DATA FETCH (FIXED LOOKUP) ---
        app.database.DonationDAO donationDAO = new app.database.DonationDAO();
        app.database.DonorDAO donorDAO = new app.database.DonorDAO();
        
        // 1. Get User ID (Reliable)
        String userId = LoginHelper.getCurrentUserId();
        
        // 2. Find Donor ID using User ID
        String donorId = donationDAO.findDonorIdByUserId(userId);
        
        // 3. Fetch Donations if Donor ID exists
        if (donorId != null) {
            // Fetch Donor profile to get blood type for the card
            app.models.Donor donor = donorDAO.findDonorById(donorId);
            String bloodGroup = (donor != null) ? donor.getBloodGroup() : "-";

            java.util.List<app.models.Donation> donations = donationDAO.findDonationsByDonorId(donorId);
            
            // Show top 3 recent
            int count = 0;
            for (app.models.Donation d : donations) {
                if (count >= 3) break;
                
                donationHistory.getChildren().add(
                    createDonationHistoryCard(bloodGroup, d.getDonationDate().toString(), d.getStatus())
                );
                count++;
            }
            
            if (donations.isEmpty()) {
                 Label noDonLabel = new Label("No donation history found.");
                 noDonLabel.setTextFill(Color.GRAY);
                 donationHistory.getChildren().add(noDonLabel);
            }
        } else {
             Label errorLabel = new Label("Could not load donor profile.");
             errorLabel.setTextFill(Color.RED);
             donationHistory.getChildren().add(errorLabel);
        }
        // --------------------------
        
        donorDash.getChildren().addAll(sectionLabel, actionsBox, historyLabel, donationHistory);
        return donorDash;
    }
    
    // --- Helper methods for creating cards ---
    
    private VBox createBloodTypeCard(String type, String quantity) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(180);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label typeLabel = new Label(type);
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        typeLabel.setTextFill(Color.web("#667eea"));
        
        Label qtyLabel = new Label(quantity + " units");
        qtyLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        qtyLabel.setTextFill(Color.web("#999999"));
        
        card.getChildren().addAll(typeLabel, qtyLabel);
        return card;
    }
    
    private VBox createRequestCard(String bloodType, String units, String priority) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 1);");
        
        HBox header = new HBox(20);
        Label typeLabel = new Label("Blood Type: " + bloodType);
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label priorityLabel = new Label(priority);
        priorityLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        header.getChildren().addAll(typeLabel, priorityLabel);
        
        Label unitsLabel = new Label("Required: " + units);
        unitsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        unitsLabel.setTextFill(Color.web("#666666"));
        
        card.getChildren().addAll(header, unitsLabel);
        return card;
    }
    
    private VBox createDonationHistoryCard(String bloodType, String date, String status) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 1);");
        
        HBox header = new HBox(20);
        Label dateLabel = new Label(date);
        dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-text-fill: #43e97b; -fx-font-weight: bold;");
        header.getChildren().addAll(dateLabel, statusLabel);
        
        Label typeLabel = new Label("Blood Type: " + bloodType);
        typeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        typeLabel.setTextFill(Color.web("#666666"));
        
        card.getChildren().addAll(header, typeLabel);
        return card;
    }
    
    // --- Methods for opening new windows ---
    
    private void openRewardsWindow() {
        Stage stage = new Stage();
        stage.setTitle("Rewards");
        RewardsView view = new RewardsView(stage);
        view.show();
        stage.show();
    }

    private void openHospitalMyRequestsWindow() {
        Stage stage = new Stage();
        stage.setTitle("My Requests");
        HospitalMyRequestsView view = new HospitalMyRequestsView(stage);
        view.show();
        stage.show();
    }

    private void openHospitalFulfilledOrdersWindow() {
        Stage stage = new Stage();
        stage.setTitle("Fulfilled Orders");
        HospitalFulfilledOrdersView view = new HospitalFulfilledOrdersView(stage);
        view.show();
        stage.show();
    }

    private void openHospitalContactBankView() {
        Stage stage = new Stage();
        stage.setTitle("Contact Bank");
        HospitalContactBankView view = new HospitalContactBankView(stage);
        view.show();
        stage.show();
    }

    private void openAdminManageUsersWindow() {
        Stage stage = new Stage();
        stage.setTitle("Manage Users");
        AdminManageUsersView view = new AdminManageUsersView(stage);
        view.show();
        stage.show();
    }

    private void openAdminPendingApprovalsWindow() {
        Stage stage = new Stage();
        stage.setTitle("Pending Approvals");
        AdminPendingApprovalsView view = new AdminPendingApprovalsView(stage);
        view.show();
        stage.show();
    }

    private void openAdminSystemSettingsWindow() {
        Stage stage = new Stage();
        stage.setTitle("System Settings");
        AdminSystemSettingsView view = new AdminSystemSettingsView(stage);
        view.show();
        stage.show();
    }

    private void openAdminAnalyticsWindow() {
        Stage stage = new Stage();
        stage.setTitle("Analytics");
        AdminAnalyticsView view = new AdminAnalyticsView(stage);
        view.show();
        stage.show();
    }
    
    private void openQualityCheckWindow() {
        Stage stage = new Stage();
        stage.setTitle("Quality Check");
        QualityCheckView view = new QualityCheckView(stage);
        view.show();
        stage.show();
    }
    
    private void openDistributionWindow() {
        Stage stage = new Stage();
        stage.setTitle("Blood Distribution");
        DistributionView view = new DistributionView(stage); 
        view.show();
        stage.show();
    }
    
    // Other window-opening methods
    
    private void openAppointmentWindow() {
        Stage stage = new Stage();
        stage.setTitle("Schedule Appointment");
        AppointmentView view = new AppointmentView(stage);
        view.show();
        stage.show();
    }
    
    private void openEligibilityWindow() {
        Stage stage = new Stage();
        stage.setTitle("Eligibility Check");
        EligibilityCheckView view = new EligibilityCheckView(stage);
        view.show();
        stage.show();
    }
    
    private void openInventoryWindow() {
        Stage stage = new Stage();
        stage.setTitle("Manage Inventory");
        InventoryManagementView view = new InventoryManagementView(stage);
        view.show();
        stage.show();
    }
    
    private void openDonationProcessWindow() {
        Stage stage = new Stage();
        stage.setTitle("Process Donation");
        DonationProcessView view = new DonationProcessView(stage);
        view.show();
        stage.show();
    }
    
    private void openReportsWindow() {
        Stage stage = new Stage();
        stage.setTitle("Reports");
        ReportView view = new ReportView(stage);
        view.show();
        stage.show();
    }
    
    private void openBloodRequestWindow() {
        Stage stage = new Stage();
        stage.setTitle("Request Blood");
        BloodRequestView view = new BloodRequestView(stage);
        view.show();
        stage.show();
    }
    
    private void openMyDonationsWindow() {
        Stage stage = new Stage();
        stage.setTitle("My Donations");
        MyDonationsView view = new MyDonationsView(stage);
        view.show();
        stage.show();
    }
    
    private void openMyRecordsWindow() {
        Stage stage = new Stage();
        stage.setTitle("My Records");
        MyRecordsView view = new MyRecordsView(stage);
        view.show();
        stage.show();
    }

    // (UC5)
    private void openBloodCompatibilityWindow() {
        Stage stage = new Stage();
        stage.setTitle("Blood Compatibility Check");
        BloodCompatibilityView view = new BloodCompatibilityView(stage);
        view.show();
        stage.show();
    }

    // (UC9)
    private void openBroadcastNotificationWindow() {
        Stage stage = new Stage();
        stage.setTitle("Send Broadcast Notification");
        BroadcastNotificationView view = new BroadcastNotificationView(stage);
        view.show();
        stage.show();
    }
    
    // --- Main and Alert Methods ---
    
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}