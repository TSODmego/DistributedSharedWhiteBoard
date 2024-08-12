//Zhiyuan Liu 1071288
package Server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

public class Room {
    private String theRoomId;
    private Map<Socket, String> socketUserIdMap;
    private String managerId;
    private List<JsonObject> whiteboardInfo;
    private List<String> chatHistoryInfo;
    
    
    
    public Room(String theRoomId, String managerId) {
        this.theRoomId = theRoomId;
        this.managerId = managerId;
        this.socketUserIdMap = new HashMap<>();
        this.whiteboardInfo = new ArrayList<>();
        this.chatHistoryInfo = new ArrayList<>();
    }

    public void addUser(String userId, Socket socket) {
        if (this.socketUserIdMap.containsValue(userId)) {
            System.out.println("User ID already exists in the room.");
            return;
        }
        socketUserIdMap.put(socket, userId);
    }

    public void removeUser(Socket socket) {
        String userId = socketUserIdMap.remove(socket);
        if (userId != null) {
            System.out.println("User " + userId + " has been removed from the room.");
        }
    }
    
    public void removeUserByManager(String managerUserId, Socket socketToRemove) {
        if (managerUserId.equals(this.managerId) && !socketToRemove.equals(getManagerSocket())) {
            removeUser(socketToRemove);
        }
    }

    public String getManagerId() {
        return managerId;
    }

    public boolean hasUser(String userId) {
        return socketUserIdMap.containsValue(userId);
    }

    public boolean isEmpty() {
        return socketUserIdMap.isEmpty();
    }

    public String getUserId(Socket socket) {
        return socketUserIdMap.get(socket);
    }

    public Socket getSocketByUserId(String userId) {
        for (Map.Entry<Socket, String> entry : socketUserIdMap.entrySet()) {
            if (entry.getValue().equals(userId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<Socket> getClientSockets() {
        return new ArrayList<>(socketUserIdMap.keySet());
    }

    public String getRoomId() {
        return theRoomId;
    }

    public String getManagerUserId() {
        return managerId;
    }

    public void addLineToWhiteboard(JsonObject lineObject) {
        whiteboardInfo.add(lineObject);
    }

    public List<JsonObject> getWhiteboardInfo() {
        return whiteboardInfo;
    }



    public Socket getManagerSocket() {
        for (Map.Entry<Socket, String> entry : socketUserIdMap.entrySet()) {
            if (entry.getValue().equals(managerId)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public void addChatMessage(String message) {
    	chatHistoryInfo.add(message);
    }

    public List<String> getChatHistory() {
        return chatHistoryInfo;
    }
    
    public void clearWhiteboard() {
        whiteboardInfo.clear();
    }
    
    public void resetWhiteboard(List<JsonObject> whiteboardInfo) {
        whiteboardInfo.clear();
        whiteboardInfo.addAll(whiteboardInfo);
    }

}
