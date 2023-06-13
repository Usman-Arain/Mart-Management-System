module module {
	requires javafx.controls;
	requires javafx.graphics;
	opens components to javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
}