package components;
import application.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginForm extends StackPane {

    private Main mainPage;

    public LoginForm(Main mainPage) {
        this.mainPage = mainPage;
        setupUI();
    }

    private void setupUI() {
    	// Create UI elements
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");



        // Create layout
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(20));
        loginBox.setMaxWidth(200);
        loginBox.getChildren().addAll(titleLabel, usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        getChildren().add(loginBox);

        // Event handling
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (validateCredentials(username, password)) {
                mainPage.setLoggedIn(true);
                mainPage.showPage();
            } else {
                // Show error message
                Label errorLabel = new Label("Invalid username or password.");
                errorLabel.setStyle("-fx-text-fill: red;");
                loginBox.getChildren().add(errorLabel);
            }
        });

        // Add hover effect to the login button
        loginButton.setOnMouseEntered(event -> loginButton.setStyle("-fx-font-weight: bold; -fx-background-color: lightblue;"));
        loginButton.setOnMouseExited(event -> loginButton.setStyle(null));
    }

    

    private boolean validateCredentials(String username, String password) {
        return username.equals("admin") && password.equals("admin123");
    }
}
