//Zhiyuan Liu 1071288
package Client;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class CreatePage {

	private JFrame frame;
	private JTextField textField;
    private Client client;
	/**


	/**
	 * Create the application.
	 */
	public CreatePage(Client client) {
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
		
		JButton joinButtn = new JButton("Create");
		joinButtn.setFont(new Font("微軟正黑體", Font.PLAIN, 15));
		joinButtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textField.getText();

                if (!username.isEmpty()) {
                    client.sendCreateRoomRequest(username);
                    frame.dispose();
                } else {
                    ErrorPage errorPage = new ErrorPage();
                    errorPage.showWindow("Please enter a username.");
                }
            }
        });
		joinButtn.setBounds(310, 191, 114, 60);
		frame.getContentPane().add(joinButtn);
		
		JLabel lblNewLabel = new JLabel("User Name:");
		lblNewLabel.setBounds(10, 33, 75, 21);
		frame.getContentPane().add(lblNewLabel);
	}

    public void showWindow() {
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
    
    public void closeWindow() {
        frame.dispose();
    }
}
