package application;

class Point {
    private String x;
    private Number y;

    public Point(String x, Number y) {
        this.x = x;
        this.y = y;
    }

    // Add getters and setters for the fields
    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public Number getY() {
        return y;
    }

    public void setY(Number y) {
        this.y = y;
    }
}