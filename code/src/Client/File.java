//Zhiyuan Liu 1071288
package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class File {

	private JFrame frame;
	private Client client;
	/**
	 * Create the application.
	 */
	public File(Client client) {
		this.client = client;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 280, 439);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    client.sendNewRequest();;
			}
		});
		btnNew.setFont(new Font("宋体", Font.PLAIN, 20));
		btnNew.setBounds(10, 10, 244, 86);
		frame.getContentPane().add(btnNew);
		
		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    client.sendOpenRequest();
			}
		});
		btnOpen.setFont(new Font("宋体", Font.PLAIN, 20));
		btnOpen.setBounds(10, 108, 244, 86);
		frame.getContentPane().add(btnOpen);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	
			    client.sendSaveRequest();
			}
		});
		btnSave.setFont(new Font("宋体", Font.PLAIN, 20));
		btnSave.setBounds(10, 204, 244, 86);
		frame.getContentPane().add(btnSave);
		
		JButton btnSaveas = new JButton("saveAs");
		btnSaveas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.sendSaveAsRequest();
			}
		});
		btnSaveas.setFont(new Font("宋体", Font.PLAIN, 20));
		btnSaveas.setBounds(10, 300, 244, 86);
		frame.getContentPane().add(btnSaveas);
	}
	
    public void showWindow() {
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
