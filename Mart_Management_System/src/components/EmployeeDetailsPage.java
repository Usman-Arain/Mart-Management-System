package components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class EmployeeDetailsPage extends BorderPane {
    private TableView<Employee> employeeTable;
    private ObservableList<Employee> employeeList;
    private Button addButton;
    private Button removeButton;
    private Button editButton;

    @SuppressWarnings("unchecked")
    public EmployeeDetailsPage() {
        employeeList = FXCollections.observableArrayList();
        loadEmployeeDataFromCSV();

        employeeTable = new TableView<>();
        employeeTable.setItems(employeeList);

        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> numberColumn = new TableColumn<>("Number");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        employeeTable.getColumns().addAll(idColumn, nameColumn, numberColumn, emailColumn, addressColumn);

        addButton = new Button("Add");
        addButton.setOnAction(event -> showAddEmployeeDialog());

        removeButton = new Button("Remove");
        removeButton.setOnAction(event -> showRemoveEmployeeDialog());

        editButton = new Button("Edit");
        editButton.setOnAction(event -> showEditEmployeeDialog());

        HBox buttonBox = new HBox(addButton, removeButton, editButton);
        buttonBox.setSpacing(10);

        VBox contentBox = new VBox(employeeTable, buttonBox);
        contentBox.setSpacing(10);
        contentBox.setPadding(new Insets(10));

        setCenter(contentBox);
    }

    private void showAddEmployeeDialog() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Add Employee");
        dialog.setHeaderText("Enter employee details");

        // Create the text fields for employee details
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField numberField = new TextField();
        numberField.setPromptText("Number");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        // Add the text fields to the dialog
        dialog.getDialogPane().setContent(new VBox(
                new Label("Name:"), nameField,
                new Label("Number:"), numberField,
                new Label("Email:"), emailField,
                new Label("Address:"), addressField
        ));

        // Add buttons to the dialog
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Set the result converter for the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String name = nameField.getText();
                String number = numberField.getText();
                String email = emailField.getText();
                String address = addressField.getText();

                // Generate a unique ID for the new employee
                int newId = employeeList.size() + 1;

                return new Employee(newId, name, number, email, address);
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();
        result.ifPresent(employee -> {
            employeeList.add(employee);
            saveEmployeeDataToCSV();
        });
    }

    private void showRemoveEmployeeDialog() {
        if (employeeList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Employees");
            alert.setHeaderText(null);
            alert.setContentText("There are no employees to remove.");
            alert.showAndWait();
            return;
        }

        ChoiceDialog<Employee> dialog = new ChoiceDialog<>(employeeList.get(0), employeeList);
        dialog.setTitle("Remove Employee");
        dialog.setHeaderText("Select an employee to remove");
        dialog.setContentText("Employee:");

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return dialog.getSelectedItem();
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();
        result.ifPresent(employee -> {
            employeeList.remove(employee);
            saveEmployeeDataToCSV();
        });
    }

    private void showEditEmployeeDialog() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Employee Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an employee to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.setHeaderText("Edit employee details");

        // Create the text fields for employee details
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setText(selectedEmployee.getName());

        TextField numberField = new TextField();
        numberField.setPromptText("Number");
        numberField.setText(selectedEmployee.getNumber());

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setText(selectedEmployee.getEmail());

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.setText(selectedEmployee.getAddress());

        // Add the text fields to the dialog
        dialog.getDialogPane().setContent(new VBox(
                new Label("Name:"), nameField,
                new Label("Number:"), numberField,
                new Label("Email:"), emailField,
                new Label("Address:"), addressField
        ));

        // Add buttons to the dialog
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Set the result converter for the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                String name = nameField.getText();
                String number = numberField.getText();
                String email = emailField.getText();
                String address = addressField.getText();

                selectedEmployee.nameProperty().set(name);
                selectedEmployee.numberProperty().set(number);
                selectedEmployee.emailProperty().set(email);
                selectedEmployee.addressProperty().set(address);

                return selectedEmployee;
            }
            return null;
        });

        dialog.showAndWait();
        saveEmployeeDataToCSV();
    }

    private void loadEmployeeDataFromCSV() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("employees.csv"))) {
            String line;
            int idSequence = 1;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {
                    String name = values[0];
                    String number = values[1];
                    String email = values[2];
                    String address = values[3];
                    employeeList.add(new Employee(idSequence, name, number, email, address));
                    idSequence++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEmployeeDataToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("employees.csv"))) {
            for (Employee employee : employeeList) {
                writer.printf("%s,%s,%s,%s%n", employee.getName(), employee.getNumber(),
                        employee.getEmail(), employee.getAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
