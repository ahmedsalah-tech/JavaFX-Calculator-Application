module Calculator {
	requires javafx.controls;
	requires javafx.graphics;
	
	opens calculator to javafx.graphics, javafx.fxml;
}
