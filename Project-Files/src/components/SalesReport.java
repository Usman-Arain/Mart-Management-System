package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SalesReport extends VBox {

    private Map<String, Integer> productQuantityMap;
    private double totalSales;

    public SalesReport() {
        productQuantityMap = new HashMap<>();
        totalSales = 0.0;

        setSpacing(10);

        // Create the calendar box
        VBox calendarBox = new VBox();
        calendarBox.setAlignment(Pos.CENTER);
        calendarBox.setPadding(new Insets(10));

        Label calendarLabel = new Label("Select Date:");
        calendarLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(event -> updateSalesReport(datePicker.getValue()));
        
     // Set the default selected date to the current local date
        datePicker.setValue(LocalDate.now());

        calendarBox.getChildren().addAll(calendarLabel, datePicker);

        // Create the sales report section
        VBox salesReportBox = new VBox();
        salesReportBox.setSpacing(10);

        Label titleLabel = new Label("Sales Report");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.GREEN);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(10));

        GridPane tablePane = new GridPane();
        tablePane.setHgap(50);
        tablePane.setVgap(5);
        tablePane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        tablePane.setPadding(new Insets(10));
        tablePane.setMaxWidth(330);

        Label productNameLabel = createLabel("Product Name", true);
        Label quantityLabel = createLabel("Quantity", true);
        Label subtotalLabel = createLabel("Subtotal", true);

        tablePane.add(productNameLabel, 0, 0);
        tablePane.add(quantityLabel, 1, 0);
        tablePane.add(subtotalLabel, 2, 0);

        salesReportBox.getChildren().addAll(titleLabel, tablePane);

     // Create the scroll pane and set the sales report box as its content
        ScrollPane scrollPane = new ScrollPane(salesReportBox);
        scrollPane.setFitToHeight(true);

        // Add the calendar box and scroll pane to the main VBox
        getChildren().addAll(calendarBox, scrollPane);
        
        // Fetch the sales data for the default selected date
        updateSalesReport(datePicker.getValue());
    }

    private Label createLabel(String text, boolean bold) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, 14));
        return label;
    }

    private void updateSalesReport(LocalDate selectedDate) {
        if (selectedDate == null) {
            return; // No date selected
        }

        productQuantityMap.clear();
        totalSales = 0.0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateStr = selectedDate.format(formatter);

        try (Stream<String> lines = Files.lines(Paths.get("record/sales_" + currentDateStr + ".csv"))) {
            lines.skip(1) // Skip the header row
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        String productName = parts[0];
                        int quantity = Integer.parseInt(parts[1]);
                        double subtotal = Double.parseDouble(parts[2]);

                        productQuantityMap.put(productName, productQuantityMap.getOrDefault(productName, 0) + quantity);
                        totalSales += subtotal;
                    });
        } catch (IOException e) {
            System.err.println("Error reading sales file: " + e.getMessage());
        }

        // Clear the existing rows in the table
        GridPane tablePane = (GridPane) ((VBox) ((ScrollPane) getChildren().get(1)).getContent()).getChildren().get(1);
        List<Node> nodesToRemove = new ArrayList<>();

        for (Node node : tablePane.getChildren()) {
            if (GridPane.getRowIndex(node) > 0) {
                nodesToRemove.add(node);
            }
        }

        tablePane.getChildren().removeAll(nodesToRemove);

        // Add the updated rows to the table
        int rowIndex = 1;
        for (Map.Entry<String, Integer> entry : productQuantityMap.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            double subtotal = calculateSubtotalPrice(quantity, productName, currentDateStr);

            Label productNameLabel = createLabel(productName, false);
            Label quantityLabel = createLabel(String.valueOf(quantity), false);
            Label subtotalLabel = createLabel(String.format("%.2f", subtotal), false);

            tablePane.add(productNameLabel, 0, rowIndex);
            tablePane.add(quantityLabel, 1, rowIndex);
            tablePane.add(subtotalLabel, 2, rowIndex);

            rowIndex++;
        }

        // Add the total row to the table
        Label totalLabel = createLabel("Total", true);
        Label totalQuantityLabel = createLabel(String.valueOf(calculateTotalQuantity()), true);
        Label totalSalesLabel = createLabel(String.format("%.2f", totalSales), true);

        tablePane.add(totalLabel, 0, rowIndex);
        tablePane.add(totalQuantityLabel, 1, rowIndex);
        tablePane.add(totalSalesLabel, 2, rowIndex);
    }

    private double calculateSubtotalPrice(int quantity, String productName, String currentDateStr) {
        try (Stream<String> lines = Files.lines(Paths.get("record/sales_" + currentDateStr + ".csv"))) {
            return lines.skip(1) // Skip the header row
                    .filter(line -> {
                        String[] parts = line.split(",");
                        return parts[0].equals(productName);
                    })
                    .mapToDouble(line -> {
                        String[] parts = line.split(",");
                        return Double.parseDouble(parts[2]);
                    })
                    .sum() * quantity;
        } catch (IOException e) {
            System.err.println("Error reading sales file: " + e.getMessage());
            return 0.0;
        }
    }

    private int calculateTotalQuantity() {
        int totalQuantity = 0;
        for (int quantity : productQuantityMap.values()) {
            totalQuantity += quantity;
        }
        return totalQuantity;
    }
}
