package components;

import application.Main;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class NavigationPanel extends VBox {

    public NavigationPanel(Main mainPage, CheckoutBox checkoutBox, CataloguePane cataloguePane) {
        setPadding(new Insets(10));
        setSpacing(10);

        // Create the navigation buttons
        Button catalogueButton = new Button("Catalogue");
        Button salesReportButton = new Button("Sales Report");
        Button employeeDetailsButton = new Button("Employee Details");

        // Set the preferred width for all buttons
        catalogueButton.setPrefWidth(150);
        salesReportButton.setPrefWidth(150);
        employeeDetailsButton.setPrefWidth(150);

        // Add event handlers to the buttons
        catalogueButton.setOnAction(event -> mainPage.setCenter(cataloguePane));
        salesReportButton.setOnAction(event -> mainPage.setCenter(new SalesReport()));
        employeeDetailsButton.setOnAction(event -> mainPage.setCenter(new EmployeeDetailsPage()));

        // Add the buttons to the navigation panel
        getChildren().addAll(
                catalogueButton,
                salesReportButton,
                employeeDetailsButton
        );

        // Apply CSS style to add a border line on the right side
        setStyle("-fx-border-color: black; -fx-border-width: 0 1px 0 0;");
    }
}
