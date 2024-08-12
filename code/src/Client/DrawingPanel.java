//Zhiyuan Liu 1071288
package Client;

import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;


public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener {
    
    private DrawingMode drawingMode = DrawingMode.DRAWING;
    private Point startPoint;
    private Point endPoint;
    //private ArrayList<Line> undoList;
    //private ArrayList<Line> redoList;
    private ArrayList<Line> serverList;
    private Line currentLine;
    private int brushSize = Whiteboard.DEFAULT_SIZE;
    private Color brushColor = Whiteboard.DEFAULT_COLOR;
    private String userId;

    public DrawingPanel(String userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        //undoList = new ArrayList<>();
        //redoList = new ArrayList<>();
        serverList = new ArrayList<>();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        List<Line> serverListCopy = new ArrayList<>(serverList);
        for (Line line : serverListCopy) {
            if (line.getDrawingMode() == DrawingMode.TEXT) {
                drawText(g, line);
            } else {
                drawLine(g, g2d, line);
            }
        }
        
        g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (currentLine != null) {
            if (currentLine.getDrawingMode() == DrawingMode.TEXT) {
                drawText(g, currentLine);
            } else {
                drawLine(g, g2d, currentLine);
            }
        }
    }

    private void drawLine(Graphics g, Graphics2D g2d, Line line) {
        g2d.setStroke(new BasicStroke(line.getBrushSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (line.getDrawingMode() == DrawingMode.ERASE) {
            g.setColor(getBackground());
        } else {
            g.setColor(line.getBrushColor());
        }

        List<Point> points = line.getPoints();
        if (points.size() >= 2) {
            Point startPoint = points.get(0);
            Point endPoint = points.get(points.size() - 1);

            if (line.getDrawingMode() == DrawingMode.LINE) {
                g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
            } else if (line.getDrawingMode() == DrawingMode.CIRCLE) {
                int radius = (int) Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));
                g.drawOval(startPoint.x - radius, startPoint.y - radius, 2 * radius, 2 * radius);
            } else if (line.getDrawingMode() == DrawingMode.OVAL) {
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(endPoint.x - startPoint.x);
                int height = Math.abs(endPoint.y - startPoint.y);
                g.drawOval(x, y, width, height);
            } else if (line.getDrawingMode() == DrawingMode.RECTANGLE) {
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(endPoint.x - startPoint.x);
                int height = Math.abs(endPoint.y - startPoint.y);
                g.drawRect(x, y, width, height);
            } else {
                Point previousPoint = points.get(0);
                for (int i = 1; i < points.size(); i++) {
                    Point currentPoint = points.get(i);
                    if (line.getDrawingMode() == DrawingMode.ERASE) {
                        g.setColor(getBackground());
                    } else {
                        g.setColor(line.getBrushColor());
                    }
                    g.drawLine(previousPoint.x, previousPoint.y, currentPoint.x, currentPoint.y);
                    previousPoint = currentPoint;
                }
            }
        }
    }
    
    private void drawText(Graphics g, Line line) {
        g.setColor(line.getBrushColor());
        g.setFont(new Font("Arial", Font.PLAIN, line.getBrushSize()));
        Point textPosition = line.getPoints().get(0);
        g.drawString(line.getText(), textPosition.x, textPosition.y);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        //redoList.clear();
        startPoint = e.getPoint();
        currentLine = new Line(drawingMode, brushSize, brushColor, userId);  
        currentLine.addPoint(startPoint);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        endPoint = e.getPoint();
        currentLine.addPoint(endPoint);
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        endPoint = e.getPoint();
        currentLine.addPoint(endPoint);
        //undoList.add(currentLine);
        serverList.add(currentLine);
        Whiteboard.sendLineToServer(currentLine);
        currentLine = null;
        startPoint = null;
        endPoint = null;
        repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (drawingMode == DrawingMode.TEXT) {
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "cancel");
            String texts = JOptionPane.showInputDialog(this, "Enter text:", "Text Input", JOptionPane.PLAIN_MESSAGE);
            if (texts != null && !texts.isEmpty()) {
                Point textPosition = e.getPoint();
                currentLine = new Line(drawingMode, brushSize, brushColor, userId, texts);
                currentLine.addPoint(textPosition);
                //undoList.add(currentLine);
                serverList.add(currentLine);
                Whiteboard.sendLineToServer(currentLine);
                repaint();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

//    public void undo() {
//        if (!undoList.isEmpty()) {
//            Line lastLine = null;
//            for (int i = undoList.size() - 1; i >= 0; i--) {
//                Line line = undoList.get(i);
//                if (line.getUserId().equals(userId)) {
//                    lastLine = undoList.remove(i);
//                    break;
//                }
//            }
//            if (lastLine != null) {
//                redoList.add(lastLine);
//                serverList.remove(lastLine);
//                Whiteboard.sendLineToServer(new Line(lastLine.getDrawingMode(), lastLine.getBrushSize(), lastLine.getBrushColor(), userId));
//                repaint();
//            }
//        }
//    }
//    
//    public void redo() {
//        if (!redoList.isEmpty()) {
//            Line lastUndo = null;
//            for (int i = redoList.size() - 1; i >= 0; i--) {
//                Line line = redoList.get(i);
//                if (line.getUserId().equals(userId)) {
//                    lastUndo = redoList.remove(i);
//                    break;
//                }
//            }
//            if (lastUndo != null) {
//                undoList.add(lastUndo);
//                serverList.add(lastUndo);
//                Whiteboard.sendLineToServer(lastUndo);
//                repaint();
//            }
//        }
//    }
//    
//    public void clearUndoList() {
//        undoList.clear();
//    }
    
    public void setMode(DrawingMode mode) {
        drawingMode = mode;
    }
    
    public void setBrushSize(int size) {
        brushSize = size;
    }

    public int getBrushSize() {
        return brushSize;
    }
    
    public void setBrushColor(Color color) {  
        brushColor = color;
    }

    public Color getBrushColor() {
        return brushColor;
    }
    
    public void addLineFromServer(Line line) {
        serverList.add(line);
        repaint();
    }
    
    public void clearServerList() {
        serverList.clear();
        repaint();
    }
    

    
    public List<JsonObject> transferServerListASFile() {
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (Line line : serverList) {
            JsonObject lineObject = new JsonObject();
            lineObject.addProperty("drawingMode", line.getDrawingMode().toString());
            lineObject.addProperty("brushSize", line.getBrushSize());
            lineObject.addProperty("brushColor", line.getBrushColor().getRGB());
            lineObject.addProperty("userId", line.getUserId());

            if (line.getDrawingMode() == DrawingMode.TEXT) {
                lineObject.addProperty("text", line.getText());
            }

            JsonArray pointsArray = new JsonArray();
            for (Point point : line.getPoints()) {
                JsonObject pointObject = new JsonObject();
                pointObject.addProperty("x", point.x);
                pointObject.addProperty("y", point.y);
                pointsArray.add(pointObject);
            }
            lineObject.add("points", pointsArray);

            jsonObjects.add(lineObject);
        }
        return jsonObjects;
    }
    

}