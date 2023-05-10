package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button signInButton;

    public void handleLoginButton() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.equals("admin") && password.equals("admin")) {
        	// Testing
//        	System.out.println(username);
            Main.switchToUploadScene();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("Please enter correct username and password.");
            alert.showAndWait();
        }
    }
}
