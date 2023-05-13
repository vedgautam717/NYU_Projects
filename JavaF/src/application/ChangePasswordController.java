package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ChangePasswordController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField lastName;

    @FXML
    private TextField firstName;

    @FXML
    private TextField email;

    public static String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        ChangePasswordController.accessToken = accessToken;
     // call the setter method when the access token is set
        setAllFields(accessToken);
    }

    private static String accessToken;

    public void handleSubmitButton() throws ProtocolException {

        String password1 = passwordField.getText();
        String password2 = confirmPasswordField.getText();
        if (password1.equals(password2)) {
            // create a JSON object with the user input
            JSONObject body = new JSONObject();
            body.put("firstname", firstName.getText());
            body.put("lastname", lastName.getText());
            body.put("email", email.getText());
            body.put("password", password1);
            body.put("role", "ADMIN");
            System.out.println(body);
            
            try {
            	// create a URL object for the API endpoint
                URL url = new URL("http://localhost:8080/api/v1/profile-update");

                // create a HttpURLConnection object and set the request method to POST
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                // set the authorization header with the access token
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                // set the content type header to JSON
                connection.setRequestProperty("Content-Type", "application/json");

                // enable output and write the JSON body to the output stream
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.toString().getBytes());

                // get the response code and check if it is successful
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    // switch to the login scene
                    Main.switchToLoginScene();
                } else {
                    // show an alert with the error message
                	System.out.println("API Error");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("API Error");
                    alert.setContentText(connection.getResponseMessage());
                    alert.showAndWait();
                }

                // the rest of the code goes here

            } catch (MalformedURLException e) {
                // handle the exception here
                e.printStackTrace();
                System.out.println("error");
            } catch (IOException e) {
                // handle the exception here
            	System.out.println("error");
                e.printStackTrace();
            }

            

        } else {
        	
        	// create an alert object with the error type
        	  Alert alert = new Alert(Alert.AlertType.ERROR);
        	  // set the title of the alert
        	  alert.setTitle("Password Not Same");
        	  // set the text of the alert
        	  alert.setContentText("The passwords you entered do not match. Please try again.");
        	  // show the alert to the user
        	  alert.showAndWait();
        }

    }
    
    public void setAllFields(String accessToken) {
        try {
            // create a URL object for the API endpoint
            URL url = new URL("http://localhost:8080/api/v1/profile");

            // create a HttpURLConnection object and set the request method to GET
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // set the authorization header with the access token
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // get the response code and check if it is successful
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // read the response body as a JSON object
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject response = new JSONObject(builder.toString());

                // set the text fields with the values from the JSON object
                firstName.setText(response.getString("firstname"));
                lastName.setText(response.getString("lastname"));
                email.setText(response.getString("email"));
                passwordField.setText("");
                confirmPasswordField.setText("");

                // make the email field not editable
                email.setEditable(false);

            } else {
                // show an alert with the error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("API Error");
                alert.setContentText(connection.getResponseMessage());
                alert.showAndWait();
            }
        } catch (MalformedURLException e) {
            // handle the exception here
            e.printStackTrace();
        } catch (IOException e) {
            // handle the exception here
            e.printStackTrace();
        }
    }

    public void handleBackButton() {
        Main.switchToUploadScene(accessToken);
    }
    
    public void deleteAccount() {
        try {
            // create a URL object for the API endpoint
            URL url = new URL("http://localhost:8080/api/v1/delete");

            // create a HttpURLConnection object and set the request method to GET
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // set the authorization header with the access token
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // get the response code and check if it is successful
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // switch to the login scene
                Main.switchToLoginScene();
            } else {
                // show an alert with the error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("API Error");
                alert.setContentText(connection.getResponseMessage());
                alert.showAndWait();
            }
        } catch (MalformedURLException e) {
            // handle the exception here
            e.printStackTrace();
        } catch (IOException e) {
            // handle the exception here
            e.printStackTrace();
        }
    }
}
