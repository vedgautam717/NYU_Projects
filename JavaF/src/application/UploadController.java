package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

public class UploadController {

    @FXML
    private Label fileNameLabel;

    @FXML
    private Button uploadButton;
    
    private String[][] data;
    
    private static DataModel dataModel;
    
    private static String accessToken;

    public static String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		UploadController.accessToken = accessToken;
	}

	@FXML
    private void handleUploadButton() {
        // Open file chooser dialog
		System.out.println("UploadCOntroller "+accessToken);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                List<String[]> rows = new ArrayList<>();
                List<String> columnNames = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                	String[] row = line.split(",");
                    if (columnNames.isEmpty()) {  // Add column names to the list
                        columnNames.addAll(Arrays.asList(row));
                    } else {
                        rows.add(row);
                    }
                }

                // Convert List<String[]> to String[][]
                data = new String[rows.size()][];
                for (int i = 0; i < rows.size(); i++) {
                    data[i] = rows.get(i);
                }
                
                
             // Testing: Iterate over rows and columns and print each element
//                for (int i = 0; i < data.length; i++) {
//                    for (int j = 0; j < data[i].length; j++) {
//                        System.out.print(data[i][j] + " ");
//                    }
//                    System.out.println(); // Print a newline after each row
//                }

                // Update UI
                fileNameLabel.setText(selectedFile.getName());
                
                // Testing
//                System.out.println(columnNames);
                dataModel = new DataModel(data, columnNames);
                
                Main.switchToPlotScene(dataModel, accessToken);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to read file");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    public static DataModel getDataModel() {
        return dataModel;
    }
    
    public void handleLogoutButton() {
    	Main.switchToLoginScene();
    }
    
    public void handleChangePasswordButton() {
    	Main.switchToChangePasswordScene(accessToken);
    }

}
