package application;

import java.util.List;

public class DataModel {
    private String[][] data;
    private List<String> columnNames;

    public DataModel(String[][] data, List<String> columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    public String[][] getData() {
        return data;
    }

    public List<String> getColumns() {
        return columnNames;
    }
    
 // A method to get the data for a given column name
    public String[] getColumnData(String columnName) {
      // Check if the column name is valid
      if (columnNames.contains(columnName)) {
        // Get the index of the column name in the list
        int index = columnNames.indexOf(columnName);
        // Create an array to store the column data
        String[] columnData = new String[data.length];
        // Loop through the data and copy the values from the column index
        for (int i = 0; i < data.length; i++) {
          columnData[i] = data[i][index];
        }
        // Return the column data
        return columnData;
      } else {
        // Return null if the column name is invalid
        return null;
      }
    }

}
