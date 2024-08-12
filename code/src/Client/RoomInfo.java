//Zhiyuan Liu 1071288
package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.datatransfer.*;

public class RoomInfo {
    private JFrame frame;
    private JTextArea roomIdArea;
    private JTextArea managerIdArea;
    private JPanel userIdPanel;
    private Client client;
    private List<JCheckBox> userCheckBoxes;

    public RoomInfo(Client client) {
        this.client = client;
        userCheckBoxes = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("Room Information");
        frame.setBounds(100, 100, 467, 618);
        frame.getContentPane().setLayout(null);

        roomIdArea = new JTextArea();
        roomIdArea.setEditable(false);
        roomIdArea.setBounds(90, 10, 263, 77);
        frame.getContentPane().add(roomIdArea);

        managerIdArea = new JTextArea();
        managerIdArea.setEditable(false);
        managerIdArea.setBounds(90, 92, 325, 48);
        frame.getContentPane().add(managerIdArea);

        userIdPanel = new JPanel();
        userIdPanel.setBounds(90, 150, 325, 311);
        userIdPanel.setLayout(new BoxLayout(userIdPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(userIdPanel);
        scrollPane.setBounds(90, 150, 325, 311);
        frame.getContentPane().add(scrollPane);

        JButton kickButton = new JButton("Kick");
        kickButton.setFont(new Font("宋体", Font.PLAIN, 16));
        kickButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                kickSelectedUsers();
            }
        });
        kickButton.setBounds(266, 493, 149, 60);
        frame.getContentPane().add(kickButton);

        JLabel managerIdLabel = new JLabel("ManagerID:");
        managerIdLabel.setFont(new Font("宋体", Font.PLAIN, 13));
        managerIdLabel.setBounds(10, 106, 70, 35);
        frame.getContentPane().add(managerIdLabel);

        JLabel roomIdLabel = new JLabel("RoomID:");
        roomIdLabel.setFont(new Font("宋体", Font.PLAIN, 13));
        roomIdLabel.setBounds(10, 32, 70, 35);
        frame.getContentPane().add(roomIdLabel);

        JLabel usersListLabel = new JLabel("UsersList:");
        usersListLabel.setFont(new Font("宋体", Font.PLAIN, 13));
        usersListLabel.setBounds(10, 155, 70, 35);
        frame.getContentPane().add(usersListLabel);

        JButton copyButton = new JButton("COPY");
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyRoomIdToClipboard();
            }
        });
        copyButton.setBounds(363, 11, 70, 76);
        frame.getContentPane().add(copyButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("宋体", Font.PLAIN, 16));
        refreshButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		client.sendGetRoomInfoRequest();
        		
        	}
        });
        refreshButton.setBounds(90, 493, 140, 60);
        frame.getContentPane().add(refreshButton);
    }

    public void updateRoomInfo(String roomId, String managerUserId, List<String> userIds) {
        roomIdArea.setText(roomId);
        managerIdArea.setText(managerUserId);
        userIdPanel.removeAll();
        userCheckBoxes.clear();

        if (userIds != null) {
            for (String userId : userIds) {
                JCheckBox checkBox = new JCheckBox(userId);
                userCheckBoxes.add(checkBox);
                userIdPanel.add(checkBox);
            }
        }

        userIdPanel.revalidate();
        userIdPanel.repaint();
    }

    private void kickSelectedUsers() {
        String currentUserId = client.getUserId();
        String managerId = managerIdArea.getText();

        if (!currentUserId.equals(managerId)) {
            ErrorPage errorPage = new ErrorPage();
            errorPage.showWindow("Sorry, only the manager can use the kick functionality.");
            return;
        }

        List<String> selectedUserIds = new ArrayList<>();
        for (JCheckBox checkBox : userCheckBoxes) {
            if (checkBox.isSelected()) {
                String selectedUserId = checkBox.getText();
                if (!selectedUserId.equals(managerId)) {
                    selectedUserIds.add(selectedUserId);
                }
            }
        }

        for (String userId : selectedUserIds) {
            client.sendKickRequest(userId);
        }
        client.sendGetRoomInfoRequest();
    }

    private void copyRoomIdToClipboard() {
        String roomId = roomIdArea.getText();
        StringSelection stringSelection = new StringSelection(roomId);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        ErrorPage errorPage = new ErrorPage();
        errorPage.showWindow( "Copied successfully");
    }

    public void showWindow() {
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public void requestRoomInfo() {
        client.sendGetRoomInfoRequest();
    }
    
    
}