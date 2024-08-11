package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class CataloguePane extends BorderPane {

    private GridPane productGrid;
    private CheckoutBox checkoutBox;
    
    public CataloguePane(CheckoutBox checkoutBox) {
        this.checkoutBox = checkoutBox;

        productGrid = new GridPane();
        productGrid.setAlignment(Pos.CENTER);
        productGrid.setHgap(10);
        productGrid.setVgap(10);
        productGrid.setPadding(new Insets(10));

        Label titleLabel = new Label("Catalogue");
        titleLabel.setStyle("-fx-font-weight: bold; " +
                "-fx-font-size: 15px; " +
                "-fx-text-fill: blue; " +
                "-fx-underline: true;");
        
        VBox catalogueBox = new VBox(10);
        catalogueBox.setAlignment(Pos.TOP_CENTER);
        catalogueBox.getChildren().addAll(titleLabel, productGrid);

        setCenter(catalogueBox);
        setRight(checkoutBox);
        
        addProductsToGrid();
    }

    private void addProductsToGrid() {
        int productsPerRow = 6;

        // Manual grid with product details
        String[][] productDetails = {
            {"Product 1", "Price: $9.99"},
            {"Product 2", "Price: $8.99"},
            {"Product 3", "Price: $6.99"},
            {"Product 4", "Price: $3.99"},
            {"Product 5", "Price: $2.99"},
            {"Product 6", "Price: $8.99"},
            {"Product 7", "Price: $1.99"},
            {"Product 8", "Price: $9.99"},
            {"Product 9", "Price: $5.99"},
            {"Product 10", "Price: $9.99"},
            {"Product 11", "Price: $3.99"},
            {"Product 12", "Price: $2.99"},
            {"Product 13", "Price: $9.99"},
            {"Product 14", "Price: $7.99"},
            {"Product 15", "Price: $5.99"},
            {"Product 16", "Price: $9.99"},
            {"Product 17", "Price: $1.99"},
            {"Product 18", "Price: $8.99"},
            {"Product 19", "Price: $2.99"},
            {"Product 20", "Price: $4.99"},
            {"Product 21", "Price: $6.99"},
            {"Product 22", "Price: $1.99"},
            {"Product 23", "Price: $4.99"},
            {"Product 24", "Price: $9.99"},
            // Add more product details here...
        };

        int row = 0;
        int col = 0;

        for (String[] details : productDetails) {
            String productName = details[0];
            String price = details[1];

            ProductBox productBox = createProductBox(productName, price);
            productGrid.add(productBox, col, row);

            col++;
            if (col == productsPerRow) {
                col = 0;
                row++;
            }
        }
    }

    private ProductBox createProductBox(String productName, String price) {
        Label nameLabel = new Label(productName);
        Label priceLabel = new Label(price);

        VBox productBox = new VBox(5);
        productBox.setAlignment(Pos.CENTER);
        productBox.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-padding: 10px;");
        productBox.getChildren().addAll(nameLabel, priceLabel);

        ProductBox wrapperBox = new ProductBox(productBox, productName, priceLabel);
        wrapperBox.setOnMouseClicked(this::handleProductClick);

        return wrapperBox;
    }

    private void handleProductClick(MouseEvent event) {
        if (event.getSource() instanceof ProductBox) {
            ProductBox productBox = (ProductBox) event.getSource();
            String productName = productBox.getProductName();
            double price = extractPriceFromLabel(productBox.getPriceLabel());
            checkoutBox.addProduct(productName, price);
        }
    }
    private double extractPriceFromLabel(Label priceLabel) {
        String priceText = priceLabel.getText();
        String priceString = priceText.substring(priceText.indexOf("$") + 1);
        return Double.parseDouble(priceString);
    }

    private static class ProductBox extends VBox {
        private String productName;
        private Label priceLabel;

        public ProductBox(VBox productBox, String productName, Label priceLabel) {
            super(productBox);
            this.productName = productName;
            this.priceLabel = priceLabel;
        }

        public String getProductName() {
            return productName;
        }
        
        public Label getPriceLabel() {
        	return priceLabel;
        }
    }
}
