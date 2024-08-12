//Zhiyuan Liu 1071288
package Client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JTextArea;


public class StartPage {
    private JFrame StartPage;
    private Client client;
    private JoinInPage joinInPage;
    
    public StartPage(Client client) {
        this.client = client;
        initialize();
    }

    private void initialize() {
        StartPage = new JFrame();
        StartPage.setTitle("Start");
        StartPage.setBounds(100, 100, 463, 290);
        StartPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StartPage.getContentPane().setLayout(null);

        JButton userButton = new JButton("User");
        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                joinInPage = new JoinInPage(client);
                joinInPage.showWindow();
                StartPage.dispose();
            }
        });
        userButton.setFont(new Font("微軟正黑體", Font.PLAIN, 20));
        userButton.setBounds(20, 144, 184, 89);
        StartPage.getContentPane().add(userButton);

        JButton managerButton = new JButton("Manager");
        managerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreatePage createPage = new CreatePage(client);
                createPage.showWindow();
                StartPage.dispose();
            }
        });
        managerButton.setFont(new Font("微軟正黑體", Font.PLAIN, 20));
        managerButton.setBounds(223, 144, 195, 89);
        StartPage.getContentPane().add(managerButton);
        
        JTextArea txtrYouCanJoin = new JTextArea();
        txtrYouCanJoin.setFont(new Font("Monospaced", Font.PLAIN, 20));
        txtrYouCanJoin.setText("You can join in room by User,\r\nOr creat a room by Manager!");
        txtrYouCanJoin.setEditable(false);
        txtrYouCanJoin.setBounds(20, 10, 398, 119);
        StartPage.getContentPane().add(txtrYouCanJoin);
    }



    public void showWindow() {
        StartPage.setVisible(true);
        StartPage.setLocationRelativeTo(null);
    }
    
    public JoinInPage getJoinInPage() {
        return joinInPage;
    }
}