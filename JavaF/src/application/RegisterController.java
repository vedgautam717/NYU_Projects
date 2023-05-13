package application;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private Button registerInButton;
    
    @FXML
    private Button returnToLogin;

    public void handleRegisterButton() throws IOException {
        String firstname = firstName.getText();
        String lastname = lastName.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        // Send the registration request to the external API
        URL url = new URL("http://localhost:8080/api/v1/auth/register");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        String jsonInputString = "{\"firstname\": \"" + firstname + "\", \"lastname\": \"" + lastname + "\", \"password\": \"" + password + "\", \"email\": \"" + email + "\", \"role\": \"ADMIN\"}";
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int statusCode = con.getResponseCode();
        System.out.println(statusCode);
        if (statusCode == 200) {
            // Registration successful, move forward
            Main.switchToLoginScene();
        } else {
            // Registration failed, show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText("Could not register user");
            alert.setContentText("Please try again later.");
            alert.showAndWait();
        }
    }
    
    public void handleLoginButton() {
    	Main.switchToLoginScene();
    }
}