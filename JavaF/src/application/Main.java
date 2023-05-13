package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) {
        Main.stage = primaryStage;
        switchToLoginScene();
        primaryStage.setTitle("Data Visualization Project");
        primaryStage.show();
    }

    public static void switchToLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void switchToRegisterScene() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void switchToChangePasswordScene(String accessToken) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ChangePassword.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            ChangePasswordController changePasswordController = loader.getController();
            changePasswordController.setAccessToken(accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchToUploadScene(String accessToken) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Upload.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            UploadController uploadController = loader.getController();
            uploadController.setAccessToken(accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public static void switchToPlotScene(DataModel dataModel, String accessToken) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Plot.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            PlotController plotController = loader.getController();
            plotController.setDataModel(dataModel);
            PlotController.setAccessToken(accessToken);
            plotController.initialize();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
