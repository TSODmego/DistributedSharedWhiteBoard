//Zhiyuan Liu 1071288
package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JLabel;

public class JoinInPage {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
    private Client client;
	/**
	 * Launch the application.
	 */


	/**
	 * Create the application.
	 */
	public JoinInPage(Client client) {
        this.client = client;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(80, 10, 344, 68);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(80, 99, 344, 68);
		frame.getContentPane().add(textField_1);
		
		JButton backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
                StartPage startPage = new StartPage(client);
                startPage.showWindow();
                frame.dispose();
			}
		});
		backButton.setFont(new Font("微軟正黑體", Font.PLAIN, 15));
		backButton.setBounds(24, 191, 110, 60);
		frame.getContentPane().add(backButton);
		
		JButton joinButtn = new JButton("Join In");
		joinButtn.setFont(new Font("微軟正黑體", Font.PLAIN, 15));
        joinButtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textField.getText();
                String roomId = textField_1.getText();

                if (!username.isEmpty() && !roomId.isEmpty()) {
                    client.sendJoinRoomRequest(roomId, username);
                    
                } else {
                    ErrorPage errorPage = new ErrorPage();
                    errorPage.showWindow("Please enter both username and room ID.");
                }
            }
        });
		joinButtn.setBounds(310, 191, 114, 60);
		frame.getContentPane().add(joinButtn);
		
		JLabel lblNewLabel = new JLabel("User Name:");
		lblNewLabel.setBounds(10, 36, 75, 21);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Room ID:");
		lblNewLabel_1.setBounds(25, 125, 60, 15);
		frame.getContentPane().add(lblNewLabel_1);
	}
	
    public void showWindow() {
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
    
    public void closeWindow() {
    	frame.dispose();
    }

}
