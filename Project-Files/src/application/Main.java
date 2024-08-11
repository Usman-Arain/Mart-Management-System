package application;

import components.CataloguePane;
import components.CheckoutBox;
import components.LoginForm;
import components.NavigationPanel;
import components.SalesReport;
import components.EmployeeDetailsPage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane mainPane;
    private NavigationPanel navigationPanel;
    private CataloguePane productCategoriesPane;
    private CheckoutBox checkoutBox;
    private SalesReport salesReport;
    private LoginForm loginForm;

    private boolean isLoggedIn = false;

    @Override
    public void start(Stage primaryStage) {
        mainPane = new BorderPane();

        // Create the sales report
        salesReport = new SalesReport();

        // Create the checkout box and pass the sales report
        checkoutBox = new CheckoutBox(salesReport);

        // Create the product categories pane
        productCategoriesPane = new CataloguePane(checkoutBox);

        navigationPanel = new NavigationPanel(this, checkoutBox, productCategoriesPane);

        // Create the login form
        loginForm = new LoginForm(this);

        // Show the login form initially
        mainPane.setCenter(loginForm);

        mainPane.setTop(createShopNamePane("Mart Management System"));

        Scene scene = new Scene(mainPane, 1000, 600);

        primaryStage.setTitle("Mart Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createShopNamePane(String shopName) {
        Label shopNameLabel = new Label(shopName);
        shopNameLabel.setStyle("-fx-font-weight: bold;");

        VBox shopNamePane = new VBox(shopNameLabel);
        shopNamePane.setAlignment(Pos.CENTER);
        shopNamePane.setPadding(new Insets(10));
        shopNamePane.setStyle("-fx-border-color: blue; -fx-border-width: 1px; -fx-background-color: lightgray;");

        return shopNamePane;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        if (isLoggedIn) {
            mainPane.setCenter(productCategoriesPane);
            mainPane.setLeft(navigationPanel);
        } else {
            mainPane.setCenter(loginForm);
            mainPane.setLeft(null);
        }
    }

    public void setCenter(CataloguePane cataloguePane) {
        if (isLoggedIn) {
            mainPane.setCenter(cataloguePane);
        }
    }
    public void setCenter(SalesReport salesReportPage) {
    	if (isLoggedIn) {
            mainPane.setCenter(salesReportPage);
        }
    }

    public void setCenter(EmployeeDetailsPage employeeDetailsPage) {
    	if (isLoggedIn) {
            mainPane.setCenter(employeeDetailsPage);
        }
    }

    public void showPage() {
        if (isLoggedIn) {
            Stage primaryStage = (Stage) mainPane.getScene().getWindow();
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    public NavigationPanel getNavigationPanel() {
        return navigationPanel;
    }
}
