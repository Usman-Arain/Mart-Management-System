package components;

import javafx.beans.property.*;

public class Employee {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty number;
    private StringProperty email;
    private StringProperty address;

    public Employee(int id, String name, String number, String email, String address) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.number = new SimpleStringProperty(number);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getNumber() {
        return number.get();
    }

    public StringProperty numberProperty() {
        return number;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }
    @Override
    public String toString() {
        return getName();
    }
}
