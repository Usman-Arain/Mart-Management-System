package components;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class CheckoutBox extends VBox {

    private Map<String, Product> selectedProducts;
    private Label titleLabel;
    private VBox productsPane;
    private Label totalLabel;

    public CheckoutBox(SalesReport salesReport) {
        selectedProducts = new HashMap<>();

        setPrefSize(250, 400);
        setPadding(new Insets(0, 2, 0, 2));
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: lightgray;");

        titleLabel = new Label("Checkout Box");
        titleLabel.setStyle("-fx-font-weight: bold;");

        productsPane = new VBox();
        productsPane.setFillWidth(true);
        productsPane.setSpacing(10);
        
        ScrollPane scrollPane = new ScrollPane(productsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        totalLabel = new Label("Total: $0.00");

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> clearSelectedProducts());
        clearButton.setStyle("-fx-pref-width: 50px;");

        Button sellButton = new Button("Sell");
        sellButton.setOnAction(event -> generateSalesFile());
        sellButton.setStyle("-fx-pref-width: 50px;");
        
        HBox buttonBox = new HBox(clearButton, sellButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        getChildren().addAll(titleLabel, scrollPane, totalLabel, buttonBox);
        
    }

    public void addProduct(String productName, double price) {
        if (selectedProducts.containsKey(productName)) {
            Product product = selectedProducts.get(productName);
            product.setQuantity(product.getQuantity() + 1);
        } else {
            Product product = new Product(productName, price, 1);
            selectedProducts.put(productName, product);
        }

        updateProductsPane();
        updateTotalLabel();
    }
    
    public void clearSelectedProducts() {
        selectedProducts.clear();
        updateProductsPane();
        updateTotalLabel();
    }

    private void updateProductsPane() {
        productsPane.getChildren().clear();

        Label nameLabel = new Label("Name");
        Label quantityLabel = new Label("Quantity");
        Label subtotalLabel = new Label("Subtotal");

        nameLabel.setStyle("-fx-font-weight: bold;");
        quantityLabel.setStyle("-fx-font-weight: bold;");
        subtotalLabel.setStyle("-fx-font-weight: bold;");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();
        column1.setHalignment(HPos.LEFT);
        column2.setHalignment(HPos.CENTER);
        column3.setHalignment(HPos.RIGHT);
        column1.setPrefWidth(60);
        column2.setPrefWidth(90);
        column3.setPrefWidth(60);
        Insets column1Margin = new Insets(0, 10, 0, 0);
        Insets column3Margin = new Insets(0, 0, 0, 10);
        GridPane.setMargin(nameLabel, column1Margin);
        GridPane.setMargin(quantityLabel, null);
        GridPane.setMargin(subtotalLabel, column3Margin);

        gridPane.getColumnConstraints().addAll(column1, column2, column3);

        gridPane.add(nameLabel, 0, 0);
        gridPane.add(quantityLabel, 1, 0);
        gridPane.add(subtotalLabel, 2, 0);

        int rowIndex = 1;
        for (Map.Entry<String, Product> entry : selectedProducts.entrySet()) {
            String productName = entry.getKey();
            Product product = entry.getValue();

            int quantity = product.getQuantity();
            double price = product.getPrice();
            double subtotal = calculateSubtotalPrice(quantity, price);
            
            Label nameLabel1 = new Label(productName);
            Label subtotalLabel1 = new Label(String.format("%.2f",subtotal));

            TextField quantityTextField = new TextField(String.valueOf(quantity));
            quantityTextField.setPrefWidth(30);
            quantityTextField.setEditable(false);

            Button increaseButton = new Button("+");
            Button decreaseButton = new Button("-");

            increaseButton.setStyle("-fx-font-weight: bold;");
            decreaseButton.setStyle("-fx-font-weight: bold;");

            increaseButton.setOnAction(e -> increaseQuantity(productName, quantityTextField));
            decreaseButton.setOnAction(e -> decreaseQuantity(productName, quantityTextField));

            HBox quantityContainer = new HBox(decreaseButton, quantityTextField, increaseButton);
            quantityContainer.setSpacing(2);
            quantityContainer.setAlignment(Pos.CENTER);

            gridPane.add(nameLabel1, 0, rowIndex);
            gridPane.add(quantityContainer, 1, rowIndex);
            gridPane.add(subtotalLabel1, 2, rowIndex);

            rowIndex++;
        }

        productsPane.getChildren().add(gridPane);
    }

    private void updateTotalLabel() {
        double total = calculateTotalPrice();
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private double calculateSubtotalPrice(int quantity, double price) {
        return price * quantity;
    }

    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (Map.Entry<String, Product> entry : selectedProducts.entrySet()) {
            Product product = entry.getValue();
            int quantity = product.getQuantity();
            double price = product.getPrice();
            double subtotal = calculateSubtotalPrice(quantity, price);
            totalPrice += subtotal;
        }
        return totalPrice;
    }

    private void increaseQuantity(String productName, TextField quantityTextField) {
        int quantity = Integer.parseInt(quantityTextField.getText());
        quantity++;
        quantityTextField.setText(String.valueOf(quantity));
        Product product = selectedProducts.get(productName);
        product.setQuantity(quantity);
        updateProductsPane();
        updateTotalLabel();
    }

    private void decreaseQuantity(String productName, TextField quantityTextField) {
        int quantity = Integer.parseInt(quantityTextField.getText());
        if (quantity > 0) {
            quantity--;
            quantityTextField.setText(String.valueOf(quantity));
            Product product = selectedProducts.get(productName);
            product.setQuantity(quantity);
            updateProductsPane();
            updateTotalLabel();
        }
        if (quantity == 0) {
            selectedProducts.remove(productName);
            updateProductsPane();
            updateTotalLabel();
        }
    }

    private void generateSalesFile() {
        try {
            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Format the current date as a string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateString = currentDate.format(formatter);

            // Create the file name using the current date
            String fileName = "record/sales_" + dateString + ".csv";
            
         // Check if the subfolder exists, create it if it doesn't
            File subfolder = new File("record");
            if (!subfolder.exists()) {
                subfolder.mkdir();
            }

            // Check if the file exists
            boolean fileExists = Files.exists(Paths.get(fileName));

            // Open the file for writing
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            // If the file doesn't exist, write the table header
            if (!fileExists) {
                writer.write("Product Name,Quantity,Subtotal");
                writer.newLine();
            }

            // Iterate over selected products
            for (Map.Entry<String, Product> entry : selectedProducts.entrySet()) {
                String productName = entry.getKey();
                Product product = entry.getValue();
                int quantity = product.getQuantity();
                double price = product.getPrice();
                double subtotal = calculateSubtotalPrice(quantity, price);

                // Check if the product already exists in the file
                boolean productExists = false;
                if (fileExists) {
                    // Read the file and check if the product name exists
                    List<String> lines = Files.readAllLines(Paths.get(fileName));
                    for (int i = 1; i < lines.size(); i++) { // Start from index 1 to skip the header line
                        String line = lines.get(i);
                        String[] fields = line.split(",");
                        if (fields.length >= 3 && fields[0].equals(productName)) {
                            // Update quantity and subtotal
                            int existingQuantity = Integer.parseInt(fields[1]);
                            double existingSubtotal = Double.parseDouble(fields[2]);
                            quantity += existingQuantity;
                            subtotal += existingSubtotal;

                            // Modify the line with updated values
                            lines.set(i, productName + "," + quantity + "," + subtotal);
                            productExists = true;
                            break;
                        }
                    }
                    // Write all the lines back to the file
                    Files.write(Paths.get(fileName), lines);
                }

                // If the product doesn't exist in the file, append a new line
                if (!productExists) {
                    writer.write(productName + "," + quantity + "," + subtotal);
                    writer.newLine();
                }
            }

            writer.close();
            System.out.println("Sales file generated successfully.");

            // Clear selected products after generating the sales file
            clearSelectedProducts();

        } catch (IOException e) {
            System.out.println("An error occurred while generating the sales file.");
            e.printStackTrace();
        }
    }
}