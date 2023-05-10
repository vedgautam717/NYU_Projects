package application;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

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
    private LineChart<String, Number> lineChart;

    @FXML
    private ScatterChart<String, Number> scatterChart;
    
    
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
		// TODO Auto-generated method stub
		this.dataModel = dataModel;
		
	}
	
	@FXML
	void handlePlotButton() {
	    String xLabel = xAxisComboBox.getValue();
	    String yLabel = yAxisComboBox.getValue();
	    String plotType = plotTypeComboBox.getValue();

	    if (xLabel != null && yLabel != null && plotType != null) {
	        if (plotType.equals("Line Plot")) {
	            // Create the line chart
	            lineChart.getData().clear();
	            lineChart.getXAxis().setLabel(xLabel);
	            lineChart.getYAxis().setLabel(yLabel);
	            lineChart.setTitle("Line Plot of " + xLabel + " vs. " + yLabel);
	            // Add this line to refresh the chart
	            lineChart.layout();
	            XYChart.Series<String, Number> series = new XYChart.Series<>();
	            int xIndex = columnNames.indexOf(xLabel);
	            int yIndex = columnNames.indexOf(yLabel);
	            for (int i = 0; i < data.length; i++) {
	                String xValue = data[i][xIndex];
	                Number yValue = parseNumber(data[i][yIndex]);
	                series.getData().add(new XYChart.Data<>(xValue, yValue));
	            }
	            lineChart.getData().add(series);
	            lineChart.setVisible(true);
	            scatterChart.setVisible(false);
	            
	            // Set the X axis tick label rotation to avoid overlapping
	            lineChart.getXAxis().setTickLabelRotation(90);
	            lineChart.toFront();
	        } else if (plotType.equals("Scatter Plot")) {
	            // Create the scatter chart
	            scatterChart.getData().clear();
	            scatterChart.getXAxis().setLabel(xLabel);
	            scatterChart.getYAxis().setLabel(yLabel);
	            scatterChart.setTitle("Scatter Plot of " + xLabel + " vs. " + yLabel);
	            // Add this line to refresh the chart
	            scatterChart.layout();
	            XYChart.Series<String, Number> series = new XYChart.Series<>();
	            int xIndex = columnNames.indexOf(xLabel);
	            int yIndex = columnNames.indexOf(yLabel);
	            for (int i = 0; i < data.length; i++) {
	                String xValue = data[i][xIndex];
	                Number yValue = parseNumber(data[i][yIndex]);
	                series.getData().add(new XYChart.Data<>(xValue, yValue));
	            }
	            scatterChart.getData().add(series);
	            scatterChart.setVisible(true);
	            lineChart.setVisible(false);
	            
	            // Set the X axis tick label rotation to avoid overlapping
	            scatterChart.getXAxis().setTickLabelRotation(90);
	            scatterChart.toFront();
	        }
	    }
	}



	
	private Number parseNumber(String str) {
	    try {
	        return Double.parseDouble(str);
	    } catch (NumberFormatException e) {
	        return null;
	    }
	}

}
