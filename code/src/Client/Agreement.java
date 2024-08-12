//Zhiyuan Liu 1071288
package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Agreement {

	private JFrame frmAgree;
    private JTextArea messageField;
    private JButton agreeButton;
    private JButton refuseButton;
    private String userId;
    private String roomId;
    private static Client client;

	/**
	 * Create the application.
	 */
    public Agreement(Client client, String userId, String roomId) {
        this.client = client;
        this.userId = userId;
        this.roomId = roomId;
        initialize();
    }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAgree = new JFrame();
		frmAgree.setTitle("Message");
		frmAgree.setBounds(100, 100, 366, 286);
		frmAgree.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmAgree.setAlwaysOnTop(true);
		frmAgree.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("MESSAGE");
		lblNewLabel.setFont(new Font("华文琥珀", Font.PLAIN, 26));
		lblNewLabel.setBounds(114, 0, 117, 33);
		frmAgree.getContentPane().add(lblNewLabel);
		
		messageField = new JTextArea();
		messageField.setEditable(false);
		messageField.setLineWrap(true);
		messageField.setWrapStyleWord(true);
		messageField.setFont(new Font("Monospaced", Font.PLAIN, 18));
		messageField.setBounds(10, 28, 330, 141);
		frmAgree.getContentPane().add(messageField);
		
		agreeButton = new JButton("Agree");
		agreeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	            client.sendJoinResponse(roomId, userId, true);
	            frmAgree.dispose();
			}
		});
		agreeButton.setFont(new Font("宋体", Font.PLAIN, 18));
		agreeButton.setBounds(10, 179, 154, 58);
		frmAgree.getContentPane().add(agreeButton);
		
		refuseButton = new JButton("Refuse");
		refuseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	            client.sendJoinResponse(roomId, userId, false);
	            frmAgree.dispose();
			}
		});
		refuseButton.setFont(new Font("宋体", Font.PLAIN, 18));
		refuseButton.setBounds(186, 179, 154, 60);
		frmAgree.getContentPane().add(refuseButton);
	}
	
	public void showWindow(String message) {
		messageField.setText(message);
		frmAgree.setVisible(true);
		frmAgree.setLocationRelativeTo(null);
	}
}

