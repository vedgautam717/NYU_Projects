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
}
