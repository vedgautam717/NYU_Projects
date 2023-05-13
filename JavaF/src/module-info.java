module JavaF {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.swing;
	requires org.json;
	
	opens application to javafx.graphics, javafx.fxml;
}
