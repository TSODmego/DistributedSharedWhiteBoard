//Zhiyuan Liu 1071288
package Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Server {
    private static final String DEFAULT_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 8899;
    private static final String DEFAULT_SERVER_NAME = "Default-Name";
    private static final int DEFAULT_WORKER_COUNT = 8;

    private String address;
    private int port;
    private String servername;
    private ServerSocket serverSocket;
    private WorkerPool workerPool;
    private ServerUI serverUI;
    
    private Map<String, Room> rooms;
    private Map<Socket, BufferedWriter> clientOutputStreams;
    private Map<String, Socket> clientWaitingMap;
    
    public static void main(String[] args) {
        String address = DEFAULT_ADDRESS;
        int port = DEFAULT_PORT;
        String servername = DEFAULT_SERVER_NAME;

        if (args.length == 3) {
            address = args[0];
            try {
                port = Integer.parseInt(args[1]);
                //System.out.println(port);
                if (port > 65535 || port < 1024) {
                    System.out.println("Invalid port number: " + port + ". Using default port: " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port: " + DEFAULT_PORT);
            }
            servername = args[2];
        }

        Server server = new Server(address, port, servername);
        
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            server.serverUI = new ServerUI();
            server.serverUI.showWindow();
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
        	server.getServerUI().setMessage(e.getMessage());
        }

        server.start();
    }

    public Server(String address, int port, String servername) {
        this.address = address;
        this.port = port;
        this.servername = servername;
        this.workerPool = new WorkerPool(DEFAULT_WORKER_COUNT, this);
        this.rooms = new HashMap<>();
        this.clientOutputStreams = new HashMap<>();
        this.clientWaitingMap = new HashMap<>();
    }

    public void start() {
        serverUI.setMessage("Server " + servername +" is running on: " + address + ":" + port);

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                serverUI.setMessage("New client connected: " + clientSocket);

                workerPool.addTask(clientSocket);
            }
        } catch (BindException e) {
            String errorMessage = "Failed to start the server. The port " + port + " is already in use.";
            serverUI.setMessage(errorMessage);
            System.out.println(errorMessage);
        } catch (IOException e) {
            String errorMessage = "Failed to start the server: " + e.getMessage();
            serverUI.setMessage(errorMessage);
            System.out.println(errorMessage);
        }
    }
    
    public ServerUI getServerUI() {
        return serverUI;
    }
    
    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }
    
    public void addUserToRoom(String roomId, String userId, Socket socket) {
        Room room = rooms.get(roomId);
        if (room == null) {
            room = new Room(roomId, userId);
            rooms.put(roomId, room);
        }
        room.addUser(userId, socket);
    }
    
    public void removeUserFromRoom(String roomId, Socket socket) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.removeUser(socket);
            serverUI.setMessage("User has leaved from room " + roomId);
            if (room.isEmpty()) {
                rooms.remove(roomId);
                serverUI.setMessage("Room " + roomId + " has been removed due to being empty");
            }
        }
    }              
    
    public void createRoom(String roomId, String userId, Socket socket) {
        Room room = new Room(roomId, userId);
        room.addUser(userId, socket);
        rooms.put(roomId, room);
    }
    
    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void addClientOutputStream(Socket socket, BufferedWriter out) {
        clientOutputStreams.put(socket, out);
    }

    public void removeClientOutputStream(Socket socket) {
        clientOutputStreams.remove(socket);
    }

    public BufferedWriter getClientOutputStream(Socket socket) {
        return clientOutputStreams.get(socket);
    }

    public Map<Socket, BufferedWriter> getClientOutputStreams() {
        return clientOutputStreams;
    }
    
    public void addClientWaitingMap(String userId, Socket socket) {
        clientWaitingMap.put(userId, socket);
    }

    public void removeClientWaitingMap(String userId) {
        clientWaitingMap.remove(userId);
    }

    public Socket getClientWaitingSocket(String userId) {
        return clientWaitingMap.get(userId);
    }
    
    public void deleteRoom(String roomId) {
        rooms.remove(roomId);
    }
}