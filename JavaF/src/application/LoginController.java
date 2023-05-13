package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

	@FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button signInButton;

    public void handleLoginButton() throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        URL url = new URL("http://localhost:8080/api/v1/auth/authenticate");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        String jsonInputString = "{\"password\": \"" + password + "\", \"email\": \"" + email + "\"}";
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int statusCode = con.getResponseCode();
     
        System.out.println(statusCode);
        if (statusCode == 200) {
        	// Open the connection and get the input stream.
        	BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        	StringBuilder sb = new StringBuilder();
        	String line;
        	while ((line = br.readLine()) != null) {
        	  sb.append(line + "\n");
        	}
        	br.close();
        	JSONObject jsonObject = new JSONObject(sb.toString());

        	// Get the access token.
        	String accessToken = jsonObject.getString("access_token");

        	// Do something with the access token.
        	System.out.println(accessToken);
        	Main.switchToUploadScene(accessToken);
        } else {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("Please enter correct username and password.");
            alert.showAndWait();
        }
    }
    
    public void handleRegisterButton() {
    	Main.switchToRegisterScene();
    }
}