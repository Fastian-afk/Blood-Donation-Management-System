package app.ui;

import app.controllers.ExportController;
import app.Main;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ExportDataView - FINAL VERSION
 * Implements SSD12 advanced parameter selection and multi-category export.
 */

public class ExportDataView {
    private Stage stage;
    private ExportController exportController;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<String> formatCombo;
    private CheckBox donorsCheck, donationsCheck, inventoryCheck, requestsCheck;

    public ExportDataView(Stage stage) {
        this.stage = stage;
        this.exportController = new ExportController();
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        root.setTop(createTopBar());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f5f7fa;");

        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("📊 Advanced Data Export Module");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        VBox formCard = new VBox(30);
        formCard.setPadding(new Insets(30));
        formCard.setMaxWidth(700);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        // --- SECTION 1: Data Categories ---
        Label categoryTitle = new Label("1. Select Data Categories (SSD12 Step 234):");
        categoryTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        donorsCheck = new CheckBox("Donor Records");
        donationsCheck = new CheckBox("Donation Events");
        inventoryCheck = new CheckBox("Inventory/Units");
        requestsCheck = new CheckBox("Blood Requests");

        HBox checksBox = new HBox(25, donorsCheck, donationsCheck, inventoryCheck, requestsCheck);
        checksBox.setPadding(new Insets(10, 0, 10, 0));

        // --- SECTION 2: Date Range and Format ---
        Label parameterTitle = new Label("2. Specify Filters & Format (SSD12 Step 236, 235):");
        parameterTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        startDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        endDatePicker = new DatePicker(LocalDate.now());

        formatCombo = new ComboBox<>();
        formatCombo.getItems().addAll("CSV", "PDF", "EXCEL");
        formatCombo.setValue("CSV");
        formatCombo.setPrefWidth(150);

        grid.add(new Label("Start Date:"), 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(new Label("End Date:"), 2, 0);
        grid.add(endDatePicker, 3, 0);
        grid.add(new Label("Format:"), 0, 1);
        grid.add(formatCombo, 1, 1);

        // --- EXPORT BUTTON ---
        Button exportBtn = new Button("📥 Generate & Download");
        exportBtn.setStyle("-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7); -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 30px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        exportBtn.setOnAction(e -> handleExport());

        formCard.getChildren().addAll(
            categoryTitle, checksBox,
            new Separator(),
            parameterTitle, grid,
            new Separator(),
            exportBtn
        );

        mainContent.getChildren().addAll(titleLabel, formCard);
        scroll.setContent(mainContent);
        root.setCenter(scroll);
        
        stage.setScene(new Scene(root, 800, 750));
    }
    
    private void handleExport() {
        List<String> categories = new ArrayList<>();
        if (donorsCheck.isSelected()) categories.add("Donors");
        if (donationsCheck.isSelected()) categories.add("Donations");
        if (inventoryCheck.isSelected()) categories.add("Inventory");
        if (requestsCheck.isSelected()) categories.add("Requests");

        if (categories.isEmpty()) {
            Main.showAlert("Error", "Please select at least one data category to export!", Alert.AlertType.ERROR);
            return;
        }

        boolean success = exportController.prepareExport(
            categories,
            formatCombo.getValue(),
            startDatePicker.getValue(),
            endDatePicker.getValue()
        );

        if (success) {
            Main.showAlert("Export Successful", 
                "Export process initiated for:\n" + String.join(", ", categories) + 
                "\nFormat: " + formatCombo.getValue(), 
                Alert.AlertType.INFORMATION);
        } else {
            Main.showAlert("Export Failed", "Could not prepare export file!", Alert.AlertType.ERROR);
        }
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> stage.close());
        
        Label titleLabel = new Label("📊 Data Export");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }
}