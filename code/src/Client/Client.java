//Zhiyuan Liu 1071288
package Client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;


import javax.swing.UIManager;


public class Client extends Thread {
    private static final int DEFAULT_PORT = 8899;
    private static final String DEFAULT_ADDRESS = "localhost";
    private static final String DEFAULT_LOCAL_ADDRESS = "./drawingPanel";
    private static final String DEFAULT_FILE_NAME = "DrawingPanel.json";
    private static final String DEFAULT_CHECK_CODE = "THIS==DRAWING==PANEL==FILE\n";
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket client;
    private String address;
    private int port;
    private Whiteboard whiteboard;
    private StartPage startPage;
    private String userId;
    private String roomId;
    private ErrorPage waitingWindow;
    
    public LinkedBlockingQueue<String> requestQueue = new LinkedBlockingQueue<>(1);

    public Client(String addressInput, int portInput) {
        address = addressInput;
        port = portInput;
    }
    
    
    public static void main(String[] args) {
        String address = DEFAULT_ADDRESS;
        int port = DEFAULT_PORT;

        if (args.length == 2) {
            try {
                address = args[0];
                port = Integer.parseInt(args[1]);
                if (port > 65535 || port < 1024) {
                    ErrorPage errorPage = new ErrorPage();
                    errorPage.showWindow("Invalid port number: " + port);
                    System.out.print("Invalid port number: " + port);
                    System.exit(0);
                }
            } catch (NumberFormatException e) {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow("Invalid port number: " + args[1]);
                System.out.print("Invalid port number");
                System.exit(1);
            }
        }

        try {
            Client client = new Client(address, port);
            client.start();

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        client.startPage = new StartPage(client);
                        client.startPage.showWindow();
                    } catch (Exception e) {
                        ErrorPage errorPage = new ErrorPage();
                        errorPage.showWindow("Failed to initialize GUI: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            ErrorPage errorPage = new ErrorPage();
            errorPage.showWindow("Failed to start client: " + e.getMessage());
            System.exit(1);
        }
    }
    
    
    @Override
    public void run() {
        try {
            client = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
            System.out.println("Connected!");

            String response;
            while ((response = reader.readLine()) != null) {
                handleServerResponse(response);
            }
        } catch (UnknownHostException e) {
            ErrorPage errorPage = new ErrorPage();
            errorPage.showWindow("Unknown host: " + address + ", please check the address again");
        } catch (IOException e) {
            ErrorPage errorPage = new ErrorPage();
            errorPage.showWindow("Connection failed! Please check the address and try again later. \nCurrent address:" + address + " , port: " + port);
         
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow("Error closing socket: " + e.getMessage());
            }
        }
    }
    
    private void handleServerResponse(String response) {
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        String action = jsonResponse.get("action").getAsString();
        
        //join:
        if (action.equals("joinGranted")) {
            this.whiteboard = new Whiteboard(this);
            whiteboard.showWindow();
            waitingWindow.closeWindow();
            if (startPage.getJoinInPage() != null) {
                startPage.getJoinInPage().closeWindow();
            }
            
            

            sendJoinSuccess();
            
        } else if (action.equals("joinDenied")) {
            String message = "Join request denied";
            if (jsonResponse.has("message")) {
                message = jsonResponse.get("message").getAsString();
            }
            
            if (message.contains("UserID is repeated")||message.contains("Room does not exist")) {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow(message);
                waitingWindow.closeWindow();
                
              
                
            } else {
                waitingWindow.closeWindow();
                startPage.showWindow();
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow(message);
                if (startPage.getJoinInPage() != null) {
                    startPage.getJoinInPage().closeWindow();
                }
            }
        } else if (action.equals("joinRequest")) {
            String userId = jsonResponse.get("userId").getAsString();
            String roomId = jsonResponse.get("roomId").getAsString();
            Agreement agreement = new Agreement(this, userId, roomId);
            agreement.showWindow("User " + userId + " wants to join the room. Do you agree?");
            

        } else if (action.equals("joinSuccess")) {
        	
            sendGetRoomInfoRequest();
            
        //create:  
        }else if (action.equals("roomCreated")) {
            if (jsonResponse.has("roomId")) {
                roomId = jsonResponse.get("roomId").getAsString();
                this.whiteboard = new Whiteboard(this);
                whiteboard.showWindow();
            } else {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow("Invalid response from server: lost room Id.");
            }
        
        //roominfo:
        } else if (action.equals("roomInfo")) {
            if (jsonResponse.has("roomId") && jsonResponse.has("managerUserId") && jsonResponse.has("userIds")) {
                String roomId = jsonResponse.get("roomId").getAsString();
                String managerUserId = jsonResponse.get("managerUserId").getAsString();
                List<String> userIds = new Gson().fromJson(jsonResponse.get("userIds"), new TypeToken<List<String>>(){}.getType());

                if (whiteboard != null) {
                    whiteboard.updateRoomInfo(roomId, managerUserId, userIds);
                }
            } else {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow("Invalid response from server: lost room information.");
            }
            
        //drawing:
        } else if (action.equals("draw")) {
            if (whiteboard != null) {
                whiteboard.handleDrawResponse(jsonResponse);
            }
            
        //kick out:
        } else if (action.equals("userKicked")) {
            String kickedUserId = jsonResponse.get("userId").getAsString();
            if (kickedUserId.equals(userId)) {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow("You have been kicked out of the room.");
                whiteboard.closeWindow();
            } else {
                whiteboard.updateRoomInfo(roomId, null, null);
            }
        } else if (action.equals("kickManagerError")) {
            ErrorPage errorPage = new ErrorPage();
            errorPage.showWindow("The manager cannot be kicked out of the room.");
         
        
        
        //quit:
        }else if (action.equals("userQuit")) {
            if (jsonResponse.has("userId") && jsonResponse.has("isManager")) {
                String leftUserId = jsonResponse.get("userId").getAsString();
                boolean isManager = jsonResponse.get("isManager").getAsBoolean();
                sendGetRoomInfoRequest();
                if (isManager) {
                    whiteboard.closeWindow();
                    ErrorPage errorPage = new ErrorPage();
                    errorPage.showWindow("The manager has left the room. The room is closed.");
                }
            }
        
        //chat:
        } else if (action.equals("chat")) {
            String message = jsonResponse.get("message").getAsString();
            boolean isHistory = jsonResponse.has("isHistory") && jsonResponse.get("isHistory").getAsBoolean();
            if (isHistory) {
                whiteboard.getChat().appendHistory(message);
            } else {
                String userId = jsonResponse.get("userId").getAsString();
                whiteboard.getChat().appendMessage(userId, message);
            }
            
            
            
        //file:
            
            	
            
        }else if (action.equals("saveFile")) {
            if (whiteboard != null) {
                List<JsonObject> lines = whiteboard.getDrawingPanel().transferServerListASFile();
                String defaultFileName = DEFAULT_FILE_NAME;
                File defaultDirectory = new File(DEFAULT_LOCAL_ADDRESS);
                if (!defaultDirectory.exists()) {
                    defaultDirectory.mkdirs();
                }
                File defaultFile = new File(defaultDirectory, defaultFileName);
                try (FileWriter writer = new FileWriter(defaultFile)) {
                	writer.write(DEFAULT_CHECK_CODE);
                    Gson saved = new Gson();
                    saved.toJson(lines, writer);
                    ErrorPage errorPage = new ErrorPage();
                    errorPage.showWindow("Save successfully");
                } catch (IOException e) {
                    ErrorPage errorPage = new ErrorPage();
                    errorPage.showWindow("Save failed");
                }
            }
        } else if (action.equals("saveasFile")) {
            if (whiteboard != null) {
                List<JsonObject> lines = whiteboard.getDrawingPanel().transferServerListASFile();
                JFileChooser fileChooser = new JFileChooser();
                UIManager.put("FileChooser.openDialogTitleText","Open");                  
                UIManager.put("FileChooser.lookInLabelText","Look in:");              
                UIManager.put("FileChooser.fileNameLabelText","File name:");              
                UIManager.put("FileChooser.filesOfTypeLabelText","Files of type:");      
                UIManager.put("FileChooser.openButtonText","Open");                     
                UIManager.put("FileChooser.cancelButtonText", "Cancel");                   
                UIManager.put("FileChooser.acceptAllFileFilterText","All Files");     
                UIManager.put("FileChooser.saveDialogTitleText","Save as");     
                UIManager.put("FileChooser.saveInLabelText","Save in:");
                UIManager.put("FileChooser.saveButtonText","Save");   
                fileChooser.updateUI();
                fileChooser.setDialogTitle("Save Whiteboard File");
                int userSelection = fileChooser.showSaveDialog(whiteboard);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!fileName.toLowerCase().endsWith(".json")) {
                        fileName += ".json";
                    }
                    try (FileWriter writer = new FileWriter(fileName)) {
                    	writer.write(DEFAULT_CHECK_CODE);
                        Gson saved = new Gson();
                        saved.toJson(lines, writer);
                        ErrorPage errorPage = new ErrorPage();
                        errorPage.showWindow("Save successfully");
                    } catch (IOException e) {
                        ErrorPage errorPage = new ErrorPage();
                        errorPage.showWindow("Save failed");
                    }
                }
            }
        } else if (action.equals("newFile")) {
            if (whiteboard != null) {
                whiteboard.getDrawingPanel().clearServerList();
                //whiteboard.getDrawingPanel().clearUndoList();
                whiteboard.getDrawingPanel().repaint();
            }
            
            
        } else if (action.equals("notManagerError")) {
            String message = jsonResponse.get("message").getAsString();
            ErrorPage errorPage = new ErrorPage();
            errorPage.showWindow(message);
        }
        
        
        
        
        
        
        
        
        else {
            System.out.println("Received response without 'action' field: " + response);
        }
        

    }
    

    public void sendJoinRoomRequest(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
        JsonObject request = new JsonObject();
        request.addProperty("action", "join");
        request.addProperty("userId", userId);
        request.addProperty("roomId", roomId);
        sendRequest(request.toString());
        this.waitingWindow = new ErrorPage();
        waitingWindow.showWindow("Waiting for the agreement of joining by manager...");
    }

    public void sendCreateRoomRequest(String userId) {
        this.userId = userId;
        JsonObject request = new JsonObject();
        request.addProperty("action", "create");
        request.addProperty("userId", userId);
        sendRequest(request.toString());
    }

    public void sendRequest(String request) {
        try {
            writer.write(request + "\n");
            writer.flush();
        } catch (IOException e) {
            ErrorPage errorPage = new ErrorPage();
            errorPage.showWindow("Error sending request: " + e.getMessage());
        }
    }
    
    public void sendJoinSuccess() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "joinSuccess");
        request.addProperty("roomId", roomId);
        sendRequest(request.toString());
    }
    
    public void sendGetRoomInfoRequest() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "getRoomInfo");
        request.addProperty("roomId", roomId);
        sendRequest(request.toString());
    }
    
    public void sendKickRequest(String userIdToRemove) {
        JsonObject request = new JsonObject();
        request.addProperty("action", "removeUser");
        request.addProperty("roomId", roomId);
        request.addProperty("managerUserId", userId);
        request.addProperty("userIdToRemove", userIdToRemove);
        sendRequest(request.toString());
    }
    
    public void sendJoinResponse(String roomId, String userId, boolean agreed) {
        JsonObject response = new JsonObject();
        response.addProperty("action", "joinResponse");
        response.addProperty("roomId", roomId);
        response.addProperty("userId", userId);
        response.addProperty("agreed", agreed);
        sendRequest(response.toString());
    }
    

    


    public String getUserId() {
        return userId;
    }
    

    public String getRoomId() {
        return roomId;
    }
    
    public StartPage getStartPage() {
        return startPage;
    }
    

    //file:
    public void sendNewRequest() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "new");
        request.addProperty("roomId", roomId);
        request.addProperty("userId", userId);
        sendRequest(request.toString());
    }


    public void sendSaveRequest() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "save");
        request.addProperty("roomId", roomId);
        request.addProperty("userId", userId);
        sendRequest(request.toString());
    }

    public void sendSaveAsRequest() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "saveas");
        request.addProperty("roomId", roomId);
        request.addProperty("userId", userId);
        sendRequest(request.toString());
    }
    
    public void sendOpenRequest() {
        JFileChooser fileChooser = new JFileChooser();
        UIManager.put("FileChooser.openDialogTitleText","Open");                  
        UIManager.put("FileChooser.lookInLabelText","Look in:");              
        UIManager.put("FileChooser.fileNameLabelText","File name:");              
        UIManager.put("FileChooser.filesOfTypeLabelText","Files of type:");      
        UIManager.put("FileChooser.openButtonText","Open");                     
        UIManager.put("FileChooser.cancelButtonText", "Cancel");                   
        UIManager.put("FileChooser.acceptAllFileFilterText","All Files");     
        UIManager.put("FileChooser.saveDialogTitleText","Save as");     
        UIManager.put("FileChooser.saveInLabelText","Save in:");
        UIManager.put("FileChooser.saveButtonText","Save");   
        fileChooser.updateUI();
        fileChooser.setDialogTitle("Open Whiteboard File");
        int userSelection = fileChooser.showOpenDialog(whiteboard);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                List<JsonObject> lines = loadWhiteboardFromFile(fileName);
                JsonObject request = new JsonObject();
                request.addProperty("action", "open");
                request.addProperty("roomId", roomId);
                request.addProperty("userId", userId);
                JsonArray jsonArray = new JsonArray();
                for (JsonObject line : lines) {
                    jsonArray.add(line);
                }
                request.add("lines", jsonArray);
                sendRequest(request.toString());
            } catch (IOException e) {
                ErrorPage errorPage = new ErrorPage();
                errorPage.showWindow(e.getMessage());
            }
        }
    }
    
    
    public List<JsonObject> loadWhiteboardFromFile(String fileName) throws IOException {
        List<JsonObject> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String firstLine = reader.readLine()+"\n";
            //System.out.println(firstLine);
            //System.out.println(DEFAULT_CHECK_CODE);
            if (!firstLine.equals(DEFAULT_CHECK_CODE)) {
            	//System.out.println("not");
                throw new IOException("Invalid DrawingPanel file format.");
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<JsonObject>>() {}.getType();
            lines = gson.fromJson(reader, type);
        }
        return lines;
    }
    
   


} 