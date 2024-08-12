//Zhiyuan Liu 1071288
package Server;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class ServerUI {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerUI window = new ServerUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerUI() {
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
		textField.setEditable(false);
		textField.setBounds(10, 10, 414, 241);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
	}
	
    public void setMessage(String message) {
    	textField.setText(message + "\n");
    }
    
    public void showWindow() {
    	frame.setVisible(true);
    	frame.setLocationRelativeTo(null);
    }

}
