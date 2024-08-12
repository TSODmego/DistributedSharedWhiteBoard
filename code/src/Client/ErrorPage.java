//Zhiyuan Liu 1071288
package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;

public class ErrorPage {

	private JFrame frmError;
    private JTextArea errorMessageField;


	/**
	 * Create the application.
	 */
	public ErrorPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmError = new JFrame();
		frmError.setTitle("Message");
		frmError.setBounds(100, 100, 366, 218);
		frmError.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmError.setAlwaysOnTop(true);
		frmError.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("MESSAGE");
		lblNewLabel.setFont(new Font("华文琥珀", Font.PLAIN, 26));
		lblNewLabel.setBounds(114, 0, 117, 33);
		frmError.getContentPane().add(lblNewLabel);
		
		errorMessageField = new JTextArea();
		errorMessageField.setEditable(false);
		errorMessageField.setLineWrap(true);
		errorMessageField.setWrapStyleWord(true);
		errorMessageField.setFont(new Font("Monospaced", Font.PLAIN, 18));
		errorMessageField.setBounds(10, 28, 330, 141);
		frmError.getContentPane().add(errorMessageField);
	}
	
	public void showWindow(String errorMessage) {
		errorMessageField.setText(errorMessage);
		frmError.setVisible(true);
		frmError.setLocationRelativeTo(null);
	}
	
	public void closeWindow() {
		frmError.dispose();
	}
}



