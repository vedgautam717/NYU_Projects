package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.github.javafx.charts.zooming.ZoomManager;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;

// Import the Tooltip class
import javafx.scene.control.Tooltip;
// Import the Alert class
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;

public class PlotController {

  private DataModel dataModel;
  private String[][] data;
  private List<String> columnNames;

  @FXML
  private ComboBox<String> plotTypeComboBox;

  @FXML
  private TextField plotTypeTextField;

  @FXML
  private ComboBox<String> xAxisComboBox;

  @FXML
  private ComboBox<String> yAxisComboBox;

  @FXML
  private Button plotButton;

  @FXML
  private LineChart<Number, Number> lineChart;

  @FXML
  private ScatterChart<Number, Number> scatterChart;

  @FXML
  private StackPane chartPane;

  // A constant for the line plot option
  private static final String LINE_PLOT = "Line Plot";

  // A constant for the scatter plot option
  private static final String SCATTER_PLOT = "Scatter Plot";

  @FXML
  void initialize() {
    // Disable the plot button initially
    plotButton.setDisable(true);
    lineChart.setVisible(false);
    scatterChart.setVisible(false);
    // Initialize the menu button with options for line and scatter plots

    dataModel = UploadController.getDataModel();
    data = dataModel.getData();
    columnNames = dataModel.getColumns();

    // Set Column options
    xAxisComboBox.setItems(FXCollections.observableList(columnNames));
    yAxisComboBox.setItems(FXCollections.observableList(columnNames));

    // Set the prompt text for the ComboBoxes
    xAxisComboBox.setPromptText("Select X Axis");
    yAxisComboBox.setPromptText("Select Y Axis");

    // Enable the plot button
    plotButton.setDisable(false);
  }

  public void setDataModel(DataModel dataModel) {
    this.dataModel = dataModel;
  }

  @FXML
  void handlePlotButton() {
    lineChart.getData().clear();
    scatterChart.getData().clear();
    String xLabel = xAxisComboBox.getValue();
    String yLabel = yAxisComboBox.getValue();
    String plotType = plotTypeComboBox.getValue();

    if (xLabel != null && yLabel != null && plotType != null) {
      // Get the data for the x and y axis
      String[] xData = dataModel.getColumnData(xLabel);
      String[] yData = dataModel.getColumnData(yLabel);
      // Check if the data is categorical
      if (isCategorical(xData) || isCategorical(yData)) {
        // Show a popup message that categorical plot is not supported
        showPopupMessage("Sorry, we cannot do a categorical plot. Please wait for future version to come.");
      } else {
        // Proceed with the plot as usual
        if (plotType.equals(LINE_PLOT)) {
          createLineChart(xLabel, yLabel);
        } else if (plotType.equals(SCATTER_PLOT)) {
          createScatterChart(xLabel, yLabel);
        }
      }
    }
  }

  // A method to check if a string is numeric
  private boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  // A method to check if the data in a column is categorical
  private boolean isCategorical(String[] column) {
    for (String value : column) {
      if (!isNumeric(value)) {
        return true;
      }
    }
    return false;
  }

  // A method to show a popup message
  private void showPopupMessage(String message) {
    // Create an alert with information type
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    // Set the title and content of the alert
    alert.setTitle("Categorical Plot");
    alert.setContentText(message);
    // Show the alert and wait for the user to close it
    alert.showAndWait();
    }

    private void createLineChart(String xLabel, String yLabel) {
        // Create the line chart
    	scatterChart.getData().clear();
        lineChart.getXAxis().setLabel(xLabel);
        lineChart.getYAxis().setLabel(yLabel);
        lineChart.setTitle(LINE_PLOT + " of " + xLabel + " vs. " + yLabel);
        // Add this line to refresh the chart
        lineChart.layout();
        XYChart.Series<Number, Number> series = createSeries(xLabel, yLabel);
        lineChart.getData().add(series);
        lineChart.setAnimated(false);

        // Add this line to create a zoom manager for the line chart
        new ZoomManager(chartPane, lineChart, series);


        lineChart.setVisible(true);
        scatterChart.setVisible(false);
    }

    private void createScatterChart(String xLabel, String yLabel) {
        // Create the scatter chart
    	lineChart.getData().clear();
        scatterChart.getXAxis().setLabel(xLabel);
        scatterChart.getYAxis().setLabel(yLabel);
        scatterChart.setTitle(SCATTER_PLOT + " of " + xLabel + " vs. " + yLabel);
        // Add this line to refresh the chart
        scatterChart.layout();
        XYChart.Series<Number, Number> series = createSeries(xLabel, yLabel);
        scatterChart.getData().add(series);
        scatterChart.setAnimated(false);

        // Add this line to create a zoom manager for the scatter chart
        new ZoomManager(chartPane, scatterChart, Collections.singleton(series));

        scatterChart.setVisible(true);
        lineChart.setVisible(false);
    }

    private XYChart.Series<Number, Number> createSeries(String xLabel, String yLabel) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        int xIndex = columnNames.indexOf(xLabel);
        int yIndex = columnNames.indexOf(yLabel);
        // Create a list of pairs of x and y values
        List<Pair<String, Number>> pairs = new ArrayList<>();
        // Create a list of pairs of x and y values as doubles
        List<Pair<Double, Double>> doublePairs = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            String xValue = data[i][xIndex];
            Number yValue = parseNumber(data[i][yIndex]);
            pairs.add(new Pair<>(xValue, yValue));
            // Try to parse the x value as a double
            try {
                double xDouble = Double.parseDouble(xValue);
                doublePairs.add(new Pair<>(xDouble, yValue.doubleValue()));
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }
        // Check if the x values are numeric or float
        boolean isNumeric = doublePairs.size() == pairs.size();
        // If they are numeric or float, sort them in increasing order
        if (isNumeric) {
            Collections.sort(doublePairs, (p1, p2) -> Double.compare(p1.getKey(), p2.getKey()));
//            // Clear the original pairs list and add the sorted double pairs as strings
//            pairs.clear();
//            for (Pair<Double, Double> pair : doublePairs) {
//                pairs.add(new Pair<>(pair.getKey(), pair.getValue()));
//            }
        }
        // Add the sorted pairs to the series
        for (Pair<Double, Double> pair : doublePairs) {
            XYChart.Data<Number, Number> temp = new XYChart.Data<>(pair.getKey(), pair.getValue());
            // Set the node to a new instance of ShowCoordinatesNode
            temp.setNode(new ShowCoordinatesNode(pair.getValue().doubleValue(), pair.getKey()));
            series.getData().add(temp);
        }
        return series;
    }




    private Number parseNumber(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public void handleBackButton() {
    	Main.switchToUploadScene();
    }
    
    public void handleLogoutButton() {
    	Main.switchToLoginScene();
    }
    
    public void handleSaveButton() {
        // Get the current chart from the chart pane
    	String plotType = plotTypeComboBox.getValue();
    	Node chart;
    	if (plotType.equals(LINE_PLOT)) {
    		chart = chartPane.getChildren().get(0);
          } 
    	else {
    		chart = chartPane.getChildren().get(1);
          }
        
        // Create a snapshot of the chart
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        // Create a file chooser to save the image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Plot");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        // Show the save dialog and get the selected file
        File file = fileChooser.showSaveDialog(chart.getScene().getWindow());
        // If the file is not null, write the image to the file
        if (file != null) {
            try {
                // Get an ImageWriter for PNG files
                ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
                // Create an ImageWriteParam to set the compression level
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(1.0f);
                // Create a file output stream for the selected file
                FileOutputStream fos = new FileOutputStream(file);
                // Get an ImageOutputStream from the file output stream
                ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
                // Set the output destination of the writer to the ImageOutputStream
                writer.setOutput(ios);
                // Write the image using the ImageWriter
                writer.write(null, new IIOImage(SwingFXUtils.fromFXImage(image, null), null, null), param);
                // Close the streams and writer
                writer.dispose();
                ios.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

// A custom node class that shows the coordinates on hover
class ShowCoordinatesNode extends StackPane {
    // A constructor that takes the x and y values as parameters
    public ShowCoordinatesNode(double x, double y) {
        // Create a tooltip with the coordinates as text
        Tooltip tooltip = new Tooltip("(" + x + ", " + y + ")");
        // Set the tooltip to this node
        Tooltip.install(this, tooltip);
    }
}
