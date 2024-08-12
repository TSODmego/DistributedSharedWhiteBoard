//Zhiyuan Liu 1071288
package Server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import Client.Line;

import java.net.SocketException;

public class Worker extends Thread {
    private BlockingQueue<Socket> taskQueue;
    private Server server;

    public Worker(BlockingQueue<Socket> taskQueue, Server server) {
        this.taskQueue = taskQueue;
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = taskQueue.take();
                handleClientRequest(clientSocket);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

            Gson gson = new Gson();

            String request;
            while ((request = in.readLine()) != null) {
                try {
                    JsonObject jsonRequest = gson.fromJson(request, JsonObject.class);
                    String action = jsonRequest.get("action").getAsString();

                    if (action.equals("join")) {
                        handleJoinRequest(jsonRequest, out, clientSocket);
                    } else if (action.equals("joinResponse")) {
                        handleJoinResponse(jsonRequest);
                    } else if (action.equals("create")) {
                        handleCreateRequest(jsonRequest, out, clientSocket);
                    } else if (action.equals("getRoomInfo")) {
                        handleGetRoominfo(jsonRequest, out);
                    } else if (action.equals("draw")) {
                        handleDrawRequest(jsonRequest, clientSocket);
                    } else if (action.equals("removeUser")) {
                        handleKickRequest(jsonRequest);
                    } else if (action.equals("disconnect")) {
                    	handleDisconnectRequest(jsonRequest);
                    } else if (action.equals("joinSuccess")) {
                    	handleJoinSuccess(jsonRequest);
                    }else if (action.equals("chat")) {
                        handleChatRequest(jsonRequest);
                    } else if (action.equals("new")) {
                        handleNewRequest(jsonRequest);
                    } else if (action.equals("open")) {
                        handleOpenRequest(jsonRequest);
                    } else if (action.equals("save")) {
                        handleSaveRequest(jsonRequest);
                    } else if (action.equals("saveas")) {
                        handleSaveasRequest(jsonRequest);
                    }
                } catch (com.google.gson.JsonSyntaxException e) {
                    System.out.println("Invalid JSON request: " + request);
                } catch (NullPointerException e) {
                    System.out.println("Null JSON request");
                }
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("Connection reset")) {
                server.getServerUI().setMessage("Client disconnected: " + clientSocket);
            } else {
                server.getServerUI().setMessage("Error handling client request: " + e.getMessage());
            }
        } catch (IOException e) {
            server.getServerUI().setMessage("Error handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                server.getServerUI().setMessage("Error closing client socket: " + e.getMessage()); 
            }
        }
    }

    private void handleJoinRequest(JsonObject jsonRequest, BufferedWriter out, Socket clientSocket) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String userId = jsonRequest.get("userId").getAsString();

        if (!server.roomExists(roomId)) {
            try {
                out.write(generateResponse("joinDenied", "Room does not exist") + "\n");
                out.flush();
            } catch (IOException e) {
                server.getServerUI().setMessage("Error sending join denied response: " + e.getMessage());
            }
            return;
        }

        Room room = server.getRoom(roomId);
        if (room.hasUser(userId)) {
            try {
                out.write(generateResponse("joinDenied", "UserID is repeated, please choose another one.") + "\n");
                out.flush();
            } catch (IOException e) {
                server.getServerUI().setMessage("Error sending join denied response: " + e.getMessage());
            }
            return;
        }

        Socket managerSocket = room.getManagerSocket();
        try {
            BufferedWriter managerOut = new BufferedWriter(new OutputStreamWriter(managerSocket.getOutputStream(), "UTF-8"));
            JsonObject request = new JsonObject();
            request.addProperty("action", "joinRequest");
            request.addProperty("userId", userId);
            request.addProperty("roomId", roomId);
            managerOut.write(request.toString() + "\n");
            managerOut.flush();

            server.addClientWaitingMap(userId, clientSocket);
        } catch (IOException e) {
            server.getServerUI().setMessage("Error sending join request to manager: " + e.getMessage());
            try {
                out.write(generateResponse("joinDenied", "Manager is not available") + "\n");
                out.flush();
            } catch (IOException ex) {
                server.getServerUI().setMessage("Error sending join denied response: " + ex.getMessage());
            }
        }
    }

    private void handleCreateRequest(JsonObject jsonRequest, BufferedWriter out, Socket clientSocket) {
        String userId = jsonRequest.get("userId").getAsString();
        String roomId = userId + ": " + UUID.randomUUID().toString();

        server.createRoom(roomId, userId, clientSocket);
        server.addClientOutputStream(clientSocket, out);

        JsonObject response = new JsonObject();
        response.addProperty("action", "roomCreated");
        response.addProperty("roomId", roomId);
        try {
            out.write(response.toString() + "\n");
            out.flush();
        } catch (IOException e) {
            server.getServerUI().setMessage("Error sending room created response: " + e.getMessage());
        }
    }

    private void handleJoinResponse(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String userId = jsonRequest.get("userId").getAsString();
        boolean agreed = jsonRequest.get("agreed").getAsBoolean();

        Socket clientSocket = server.getClientWaitingSocket(userId);
        if (clientSocket != null) {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
                if (agreed) {
                    server.addUserToRoom(roomId, userId, clientSocket);
                    server.addClientOutputStream(clientSocket, out);

                    JsonObject response = new JsonObject();
                    response.addProperty("action", "joinGranted");
                    out.write(response.toString() + "\n");
                    out.flush();

                    Room room = server.getRoom(roomId);
                    for (JsonObject lineObject : room.getWhiteboardInfo()) {
                        JsonObject drawResponse = new JsonObject();
                        drawResponse.addProperty("action", "draw");
                        drawResponse.add("line", lineObject);
                        out.write(drawResponse.toString() + "\n");
                        out.flush();
                    }
                    

                    for (String message : room.getChatHistory()) {
                        JsonObject chatMessage = new JsonObject();
                        chatMessage.addProperty("action", "chat");
                        chatMessage.addProperty("message", message + "\n");
                        chatMessage.addProperty("isHistory", true);
                        out.write(chatMessage.toString() + "\n");
                        out.flush();
                    }
                    
                } else {
                    JsonObject response = new JsonObject();
                    response.addProperty("action", "joinDenied");
                    response.addProperty("message", "Manager refused your join request");
                    out.write(response.toString() + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                server.getServerUI().setMessage("Error sending join response to client: " + e.getMessage());
            }
            server.removeClientWaitingMap(userId);
        }
    }

    private void handleGetRoominfo(JsonObject jsonRequest, BufferedWriter out) {
        String roomId = jsonRequest.get("roomId").getAsString();
        Room room = server.getRoom(roomId);

        JsonObject response = new JsonObject();
        response.addProperty("action", "roomInfo");

        if (room != null) {
            response.addProperty("roomId", room.getRoomId());
            response.addProperty("managerUserId", room.getUserId(room.getManagerSocket()));

            JsonArray userIdsArray = new JsonArray();
            for (Socket socket : room.getClientSockets()) {
            	if(room.getUserId(socket) != room.getManagerId()) {
            		userIdsArray.add(room.getUserId(socket));
            	}
            }
            response.add("userIds", userIdsArray);
        }

        try {
            out.write(response.toString() + "\n");
            out.flush();
        } catch (IOException e) {
            server.getServerUI().setMessage("Error sending room info response: " + e.getMessage());
        }
    }

    private void handleDrawRequest(JsonObject jsonRequest, Socket senderSocket) {
        String roomId = jsonRequest.get("roomId").getAsString();
        JsonObject lineObject = jsonRequest.getAsJsonObject("line");

        Room room = server.getRoom(roomId);
        if (room != null) {
            room.addLineToWhiteboard(lineObject);

            for (Socket socket : room.getClientSockets()) {
                if (!socket.equals(senderSocket)) {
                    try {
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                        JsonObject response = new JsonObject();
                        response.addProperty("action", "draw");
                        response.add("line", lineObject);
                        out.write(response.toString() + "\n");
                        out.flush();
                        System.out.println("Broadcasted draw message to user: " + room.getUserId(socket));
                    } catch (IOException e) {
                        server.getServerUI().setMessage("Error broadcasting draw message: " + e.getMessage());
                    }
                }
            }
        }
    }

    private String generateResponse(String action, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("action", action);
        if (message != null) {
            response.addProperty("message", message);
        }
        return response.toString();
    }

    private void handleKickRequest(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String managerUserId = jsonRequest.get("managerUserId").getAsString();
        String userIdToRemove = jsonRequest.get("userIdToRemove").getAsString();

        Room room = server.getRoom(roomId);
        if (room != null) {
            Socket managerSocket = room.getManagerSocket();

            if (userIdToRemove.equals(room.getUserId(managerSocket))) {
                try {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(managerSocket.getOutputStream(), "UTF-8"));
                    JsonObject response = new JsonObject();
                    response.addProperty("action", "kickManagerError");
                    out.write(response.toString() + "\n");
                    out.flush();
                } catch (IOException e) {
                    server.getServerUI().setMessage("Error sending kick manager error response: " + e.getMessage());
                }
                return;
            }

            Socket socketToRemove = room.getSocketByUserId(userIdToRemove);
            room.removeUserByManager(managerUserId, socketToRemove);

            for (Socket socket : room.getClientSockets()) {
                try {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                    JsonObject response = new JsonObject();
                    response.addProperty("action", "userKicked");
                    response.addProperty("userId", userIdToRemove);
                    out.write(response.toString() + "\n");
                    out.flush();
                } catch (IOException e) {
                    server.getServerUI().setMessage("Error sending user kicked response: " + e.getMessage());
                }
            }

            try {
                BufferedWriter removedUser = new BufferedWriter(new OutputStreamWriter(socketToRemove.getOutputStream(), "UTF-8"));
                JsonObject response = new JsonObject();
                response.addProperty("action", "userKicked");
                response.addProperty("userId", userIdToRemove);
                removedUser.write(response.toString() + "\n");
                removedUser.flush();

                server.removeClientOutputStream(socketToRemove);
            } catch (IOException e) {
                server.getServerUI().setMessage("Error sending user kicked response to removed user: " + e.getMessage());
            }
        }
    }
    
    private void handleDisconnectRequest(JsonObject jsonRequest) {
        String userId = jsonRequest.get("userId").getAsString();
        String roomId = jsonRequest.get("roomId").getAsString();

        Room room = server.getRoom(roomId);
        if (room != null) {
            Socket clientSocket = room.getSocketByUserId(userId);
            if (clientSocket != null) {
                room.removeUser(clientSocket);
                server.removeClientOutputStream(clientSocket);
                server.getServerUI().setMessage("User " + userId + " has been removed from room " + roomId);
                //System.out.println("User " + userId + " has been removed from room " + roomId);

                boolean isManager = userId.equals(room.getManagerId());

                for (Socket socket : room.getClientSockets()) {
                    try {
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                        JsonObject response = new JsonObject();
                        response.addProperty("action", "userQuit");
                        response.addProperty("userId", userId);
                        response.addProperty("isManager", isManager);
                        out.write(response.toString() + "\n");
                        out.flush();
                    } catch (IOException e) {
                        server.getServerUI().setMessage("Error sending user left response: " + e.getMessage());
                        //System.out.println("Error sending user left response: " + e.getMessage());
                    }
                }

                if (isManager) {
                    server.deleteRoom(roomId);
                    server.getServerUI().setMessage("Room " + roomId + " has been closed because the manager left.");
                }
            }
        }
    }
    
    private void handleJoinSuccess(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        Room room = server.getRoom(roomId);
        if (room != null) {
            for (Socket socket : room.getClientSockets()) {
                try {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                    JsonObject response = new JsonObject();
                    response.addProperty("action", "joinSuccess");
                    out.write(response.toString() + "\n");
                    out.flush();
                } catch (IOException e) {
                    server.getServerUI().setMessage("Error sending join success to client: " + e.getMessage());
                }
            }
        }
    }
    
    private void handleChatRequest(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String userId = jsonRequest.get("userId").getAsString();
        String message = jsonRequest.get("message").getAsString();

        Room room = server.getRoom(roomId);
        if (room != null) {
            room.addChatMessage(userId + ": "+ "\n" + message);
            for (Socket socket : room.getClientSockets()) {
                try {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                    JsonObject chatMessage = new JsonObject();
                    chatMessage.addProperty("action", "chat");
                    chatMessage.addProperty("userId", userId);
                    chatMessage.addProperty("message", message);
                    out.write(chatMessage.toString() + "\n");
                    out.flush();
                } catch (IOException e) {
                    server.getServerUI().setMessage("Error broadcasting chat message: " + e.getMessage());
                }
            }
        }
    }
    


    private void handleSaveRequest(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String userId = jsonRequest.get("userId").getAsString();
        Room room = server.getRoom(roomId);
        if (room != null) {
            if (userId.equals(room.getManagerId())) {
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(room.getManagerSocket().getOutputStream(), "UTF-8"));
                    JsonObject response = new JsonObject();
                    response.addProperty("action", "saveFile");
                    writer.write(response.toString() + "\n");
                    writer.flush();
                } catch (IOException e) {
                	server.getServerUI().setMessage(e.getMessage());
                }
            } else {
                sendNotManagerResponse(room.getSocketByUserId(userId));
            }
        }
    }
    
    private void handleSaveasRequest(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String userId = jsonRequest.get("userId").getAsString();
        Room room = server.getRoom(roomId);
        if (room != null) {
            if (userId.equals(room.getManagerId())) {
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(room.getManagerSocket().getOutputStream(), "UTF-8"));
                    JsonObject response = new JsonObject();
                    response.addProperty("action", "saveasFile");
                    writer.write(response.toString() + "\n");
                    writer.flush();
                } catch (IOException e) {
                	server.getServerUI().setMessage(e.getMessage());
                }
            } else {
                sendNotManagerResponse(room.getSocketByUserId(userId));
            }
        }
    
    }
    
    private void handleNewRequest(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String userId = jsonRequest.get("userId").getAsString();
        Room room = server.getRoom(roomId);
        if (room != null) {
            if (userId.equals(room.getManagerId())) {
                room.clearWhiteboard();
                for (Socket socket : room.getClientSockets()) {
                    try {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                        JsonObject response = new JsonObject();
                        response.addProperty("action", "newFile");
                        writer.write(response.toString() + "\n");
                        writer.flush();
                    } catch (IOException e) {
                    	server.getServerUI().setMessage(e.getMessage());
                    }
                }
            } else {
                sendNotManagerResponse(room.getSocketByUserId(userId));
            }
        }
    }
    
    private void handleOpenRequest(JsonObject jsonRequest) {
        String roomId = jsonRequest.get("roomId").getAsString();
        String userId = jsonRequest.get("userId").getAsString();
        JsonArray jsonArray = jsonRequest.getAsJsonArray("lines");
        
        Room room = server.getRoom(roomId);
        if (room != null) {
            if (userId.equals(room.getManagerId())) {
                room.clearWhiteboard();
                
                for (JsonElement element : jsonArray) {
                    JsonObject lineObject = element.getAsJsonObject();
                    //System.out.println(lineObject);
                    room.addLineToWhiteboard(lineObject);
                }


                for (Socket socket : room.getClientSockets()) {
                    try {
                    	//System.out.println(user clean);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                        JsonObject newFileResponse = new JsonObject();
                        newFileResponse.addProperty("action", "newFile");
                        writer.write(newFileResponse.toString() + "\n");
                        writer.flush();
                    } catch (IOException e) {
                        server.getServerUI().setMessage(e.getMessage());
                    }
                }


                for (JsonObject lineObject : room.getWhiteboardInfo()) {
                    for (Socket socket : room.getClientSockets()) {
                        try {
                        	//System.out.println(user draw);
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                            JsonObject drawResponse = new JsonObject();
                            drawResponse.addProperty("action", "draw");
                            drawResponse.add("line", lineObject);
                            writer.write(drawResponse.toString() + "\n");
                            writer.flush();
                        } catch (IOException e) {
                            server.getServerUI().setMessage(e.getMessage());
                        }
                    }
                }
            } else {
                sendNotManagerResponse(room.getSocketByUserId(userId));
            }
        }
    }

    private void sendNotManagerResponse(Socket socket) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            JsonObject response = new JsonObject();
            response.addProperty("action", "notManagerError");
            response.addProperty("message", "You are not the manager of this room.");
            writer.write(response.toString() + "\n");
            writer.flush();
        } catch (IOException e) {
        	server.getServerUI().setMessage(e.getMessage());
        }
    }

}