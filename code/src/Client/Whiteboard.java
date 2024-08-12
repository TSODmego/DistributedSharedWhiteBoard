//Zhiyuan Liu 1071288

package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import javax.swing.SwingConstants;
import java.util.List;
public class Whiteboard extends JFrame {
    
    public static final int DEFAULT_SIZE = 8;
    public static final Color DEFAULT_COLOR = Color.BLACK;
    
    public static final int DEFAULT_MIN_BRUSH = 2;
    public static final int DEFAULT_MAX_BRUSH = 25;
    
    private static JLabel brushSizeLabel;
    private static Client client;
    
    private static DrawingPanel drawingPanel;
    private Colors colorsWindow;
    private static String userID;
    private static String roomID;
    private RoomInfo roomInfo;
    
    private Chat chat;
    private File file;
    public Whiteboard(Client client) {
        this.client = client;
        this.userID = client.getUserId();
        this.roomID = client.getRoomId();
        roomInfo = new RoomInfo(client);
        this.chat = new Chat(client);
        this.file = new File(client);
        initialize();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JsonObject disconnectMessage = new JsonObject();
                disconnectMessage.addProperty("action", "disconnect");
                disconnectMessage.addProperty("userId", userID);
                disconnectMessage.addProperty("roomId", roomID);
                client.sendRequest(disconnectMessage.toString());
                dispose();
            }
        });
    }
    
    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);
        setLocationRelativeTo(null);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow("this feature is still developing");
        	}
        });
        undoButton.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        //undoButton.addActionListener(e -> drawingPanel.undo());

        JButton drawFunction = new JButton("Draw");
        drawFunction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setMode(DrawingMode.DRAWING);
            }
        });

        JButton eraseFunction = new JButton("Erase");
        eraseFunction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setMode(DrawingMode.ERASE);
            }
        });

        JButton lineFuction = new JButton("Line");
        lineFuction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setMode(DrawingMode.LINE);
            }
        });

        JButton oval = new JButton("Oval");
        oval.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                drawingPanel.setMode(DrawingMode.OVAL);
        	}
        });

        JButton circleFunction = new JButton("Circle");
        circleFunction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setMode(DrawingMode.CIRCLE); 
            }
        });

        JButton rectangleFunction = new JButton("Rectangle");
        rectangleFunction.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		drawingPanel.setMode(DrawingMode.RECTANGLE);
        	}
        });

        JPanel drawingArea = new JPanel();
        drawingPanel = new DrawingPanel(userID);
        drawingArea.setLayout(new BorderLayout());
        drawingArea.add(drawingPanel, BorderLayout.CENTER);
        
        JButton redoButton = new JButton("Redo");
        redoButton.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        redoButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow("this feature is still developing");
                //drawingPanel.redo();
        	}
        });
        
        JButton chatButton = new JButton("Chat");
        chatButton.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        chatButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		chat.showWindow();
        	}
        });
        
        JSlider SizeSlider = new JSlider();
        SizeSlider.setMinimum(DEFAULT_MIN_BRUSH);
        SizeSlider.setMaximum(DEFAULT_MAX_BRUSH);
        SizeSlider.setValue(Whiteboard.DEFAULT_SIZE);

        brushSizeLabel = new JLabel("Brush Size: " + Whiteboard.DEFAULT_SIZE);
        brushSizeLabel.setIcon(createBrushSizeIcon(Whiteboard.DEFAULT_SIZE, Whiteboard.DEFAULT_COLOR));
        brushSizeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        brushSizeLabel.setVerticalTextPosition(SwingConstants.CENTER);
        
        SizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int brushSize = SizeSlider.getValue();
                drawingPanel.setBrushSize(brushSize);
                brushSizeLabel.setText("Brush Size: " + brushSize);
                brushSizeLabel.setIcon(createBrushSizeIcon(brushSize, drawingPanel.getBrushColor()));
                
            }
        });
        
        JButton btnNewButton = new JButton("Brush Color");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		colorsWindow = new Colors(drawingPanel.getBrushColor());
        		colorsWindow.showWindow();
        	}
        });
        btnNewButton.setFont(new Font("微軟正黑體", Font.PLAIN, 18));
        
        JButton fileButton = new JButton("File");
        fileButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    file.showWindow();
        	}
        });
        fileButton.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        
        JButton infoButton = new JButton("RoomInfo");
        infoButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openRoomInfo();
        	}
        });
        infoButton.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        
        JButton textFunction = new JButton("Text");
        textFunction.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                drawingPanel.setMode(DrawingMode.TEXT);
        	}
        });
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(textFunction, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
        				.addComponent(rectangleFunction, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
        				.addComponent(oval, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
        				.addComponent(circleFunction, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
        				.addComponent(lineFuction, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
        				.addComponent(eraseFunction, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
        				.addComponent(drawFunction, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(drawingArea, GroupLayout.DEFAULT_SIZE, 1236, Short.MAX_VALUE)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addComponent(undoButton, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(redoButton, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED, 275, Short.MAX_VALUE)
        					.addComponent(infoButton)
        					.addGap(18)
        					.addComponent(fileButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(chatButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
        					.addGap(32)
        					.addComponent(btnNewButton)
        					.addGap(32)
        					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        						.addComponent(SizeSlider, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
        						.addComponent(brushSizeLabel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE))))
        			.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
        				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
        					.addComponent(undoButton, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
        					.addComponent(redoButton, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
        				.addGroup(groupLayout.createSequentialGroup()
        					.addComponent(brushSizeLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(SizeSlider, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
        					.addGap(9))
        				.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
        				.addComponent(chatButton, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
        				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
        					.addComponent(infoButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        					.addComponent(fileButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addGap(192)
        					.addComponent(drawFunction, GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(eraseFunction, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(lineFuction, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(circleFunction, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(oval, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(rectangleFunction, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(textFunction, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
        					.addGap(74))
        				.addGroup(groupLayout.createSequentialGroup()
        					.addGap(18)
        					.addComponent(drawingArea, GroupLayout.PREFERRED_SIZE, 658, GroupLayout.PREFERRED_SIZE)
        					.addContainerGap())))
        );
        getContentPane().setLayout(groupLayout);
    }

    private static Icon createBrushSizeIcon(int size, Color color) {
        int iconSize = DEFAULT_MAX_BRUSH;
        BufferedImage image = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillOval((iconSize - size) / 2, (iconSize - size) / 2, size, size);
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    public static void setBrushColor(Color color) {
        drawingPanel.setBrushColor(color);
        brushSizeLabel.setIcon(createBrushSizeIcon(drawingPanel.getBrushSize(), color));
    }
    
    private void showErrorMessage(String errorMessage) {
        ErrorPage errorPage = new ErrorPage();
        errorPage.showWindow(errorMessage);
    }
    
    public void showWindow() {
        setVisible(true);
        setLocationRelativeTo(null);
    }
    
    public void closeWindow() {
    	dispose();
    }

    public void returnStart() {
        dispose();
        client.getStartPage().showWindow();
    }
    
    public void updateRoomInfo(String roomId, String managerUserId, List<String> userIds) {
        if (roomInfo != null) {
            roomInfo.updateRoomInfo(roomId, managerUserId, userIds);
        }
    }
    
    private void openRoomInfo() {
        roomInfo.showWindow();
        roomInfo.requestRoomInfo();
    }
    
    //send request to server:
    public static void sendLineToServer(Line line) {
        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "draw");
        jsonObject.addProperty("roomId", roomID);
        jsonObject.addProperty("userId", userID);

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

        jsonObject.add("line", lineObject);

        String jsonString = gson.toJson(jsonObject);
        client.sendRequest(jsonString);
    }
    
    
    public void handleDrawResponse(JsonObject jsonResponse) {
        JsonObject lineObject = jsonResponse.getAsJsonObject("line");

        String drawingModeString = lineObject.get("drawingMode").getAsString();
        DrawingMode drawingMode = DrawingMode.valueOf(drawingModeString);
        int brushSize = lineObject.get("brushSize").getAsInt();
        int colorInt = lineObject.get("brushColor").getAsInt();
        Color brushColor = new Color(colorInt);
        String lineUserId = lineObject.get("userId").getAsString();

        List<Point> points = new ArrayList<>();
        JsonArray pointsArray = lineObject.getAsJsonArray("points");
        for (JsonElement pointElement : pointsArray) {
            JsonObject pointObject = pointElement.getAsJsonObject();
            int x = pointObject.get("x").getAsInt();
            int y = pointObject.get("y").getAsInt();
            points.add(new Point(x, y));
        }

        Line line;
        if (drawingMode == DrawingMode.TEXT) {
            String text = lineObject.get("text").getAsString();
            line = new Line(drawingMode, brushSize, brushColor, lineUserId, text);
        } else {
            line = new Line(drawingMode, brushSize, brushColor, lineUserId);
        }
        line.getPoints().addAll(points);

        drawingPanel.addLineFromServer(line);
    }
    
    
    
//    public void clearUndolist() {
//    	drawingPanel.clearUndoList();
//    }
    

    
    public Chat getChat() {
    	return this.chat;
    }
    
    public DrawingPanel getDrawingPanel() {
    	return this.drawingPanel;
    }
}

enum DrawingMode {
    DRAWING,
    LINE,
    ERASE,
    CIRCLE,
    OVAL,
    RECTANGLE,
    TEXT
}



