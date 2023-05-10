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

    public void switchToLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchToUploadScene() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("upload.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchToPlotScene(DataModel dataModel) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Plot.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            PlotController plotController = loader.getController();
            plotController.setDataModel(dataModel);
            plotController.initialize();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
