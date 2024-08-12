//Zhiyuan Liu 1071288
package Client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.awt.Font;

public class Chat {

    private JFrame frmChat;
    private JTextField messageField;
    private JTextArea chatHistoryTextArea;
    private Client client;
    private List<String> chatHistory;


    /**
     * Create the application.
     */
    public Chat(Client client) {
        this.client = client;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
    	chatHistory = new ArrayList<>();
    	
        frmChat = new JFrame();
        frmChat.setTitle("Chat");
        frmChat.setBounds(100, 100, 481, 535);
        frmChat.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frmChat.getContentPane().setLayout(null);

        messageField = new JTextField();
        messageField.setBounds(10, 397, 352, 75);
        frmChat.getContentPane().add(messageField);
        messageField.setColumns(10);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                sendChatRequest(message);
                messageField.setText("");
                chatHistoryTextArea.setCaretPosition(chatHistoryTextArea.getDocument().getLength());
            }
        });
        sendButton.setBounds(372, 397, 83, 75);
        frmChat.getContentPane().add(sendButton);

        JPanel chatHistoryPanel = new JPanel();
        chatHistoryPanel.setBounds(10, 10, 445, 380);
        frmChat.getContentPane().add(chatHistoryPanel);
        chatHistoryPanel.setLayout(null);

        chatHistoryTextArea = new JTextArea();
        chatHistoryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        chatHistoryTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatHistoryTextArea);
        scrollPane.setBounds(0, 0, 445, 380);
        chatHistoryPanel.add(scrollPane);
    }

    private void sendChatRequest(String message) {
        if (message.trim().isEmpty()) {
            showErrorMessage("Message cannot be empty.");
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "chat");
        jsonObject.addProperty("roomId", client.getRoomId());
        jsonObject.addProperty("userId", client.getUserId());
        jsonObject.addProperty("message", message);

        client.sendRequest(jsonObject.toString());
    }

    private void showErrorMessage(String errorMessage) {
        ErrorPage errorPage = new ErrorPage();
        errorPage.showWindow(errorMessage);
    }
    
    public void showWindow() {
        frmChat.setVisible(true);
        frmChat.setLocationRelativeTo(null);
    }
    
    public void appendMessage(String userId, String message) {
        String formattedMessage = userId + ":\n" + message + "\n\n";
        chatHistory.add(formattedMessage);
        chatHistoryTextArea.append(formattedMessage);
    }
    
    public void appendHistory(String message) {
        chatHistory.add(message);
        chatHistoryTextArea.append(message + "\n");
    }
}