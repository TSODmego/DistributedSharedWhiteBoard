//Zhiyuan Liu 1071288
package Client;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Line {
    private DrawingMode drawingMode;
    private int brushSize;
    private Color brushColor;
    private String userId;
    private List<Point> points;
    private String text;

    public Line(DrawingMode drawingMode, int brushSize, Color brushColor, String userId) {
        this.drawingMode = drawingMode;
        this.brushSize = brushSize;
        this.brushColor = brushColor;
        this.userId = userId;
        this.points = new ArrayList<>();
        this.text = "";
    }

    public Line(DrawingMode drawingMode, int brushSize, Color brushColor, String userId, String text) {
        this.drawingMode = drawingMode;
        this.brushSize = brushSize;
        this.brushColor = brushColor;
        this.userId = userId;
        this.points = new ArrayList<>();
        this.text = text;
    }

    public DrawingMode getDrawingMode() {
        return drawingMode;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public Color getBrushColor() {
        return brushColor;
    }

    public String getUserId() {
        return userId;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}