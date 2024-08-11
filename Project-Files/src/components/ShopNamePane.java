package components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ShopNamePane extends StackPane {

    public ShopNamePane(String shopName) {
        // Create the label for the shop name
        Label shopNameLabel = new Label(shopName);
        shopNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Set alignment and padding
        setAlignment(Pos.CENTER);
        setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: lightgray;");
        setPadding(new javafx.geometry.Insets(10));

        // Add the shop name label to the pane
        getChildren().add(shopNameLabel);
    }
}
