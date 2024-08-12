//Zhiyuan Liu 1071288

package Client;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Colors {

    private JFrame frame;
    private Color currentColor;

    public Colors(Color initialColor) {
    	this.currentColor = initialColor;
    	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 487, 360);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel showColorPannel = new JPanel();
        showColorPannel.setBounds(27, 10, 343, 113);
        frame.getContentPane().add(showColorPannel);

        JSlider sliderH = new JSlider(0, 360, 0);
        sliderH.setBounds(42, 145, 369, 34);
        frame.getContentPane().add(sliderH);

        JSlider sliderS = new JSlider(0, 100, 100);
        sliderS.setBounds(45, 255, 369, 34);
        frame.getContentPane().add(sliderS);

        JSlider sliderV = new JSlider(0, 100, 100);
        sliderV.setBounds(42, 200, 369, 34);
        frame.getContentPane().add(sliderV);

        
        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                Whiteboard.setBrushColor(currentColor); 
                frame.dispose();
        	}
        });
        doneButton.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        doneButton.setBounds(380, 10, 81, 63);
        frame.getContentPane().add(doneButton);

        JLabel lblNewLabel = new JLabel("H:");
        lblNewLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 15));
        lblNewLabel.setBounds(22, 145, 28, 34);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblS = new JLabel("S:");
        lblS.setFont(new Font("微軟正黑體", Font.PLAIN, 15));
        lblS.setBounds(22, 255, 28, 34);
        frame.getContentPane().add(lblS);

        JLabel lblV = new JLabel("V:");
        lblV.setFont(new Font("微軟正黑體", Font.PLAIN, 15));
        lblV.setBounds(22, 199, 28, 34);
        frame.getContentPane().add(lblV);

        // 将初始颜色转换为 HSV
        float[] hsv = new float[3];
        Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), hsv);
        float hue = hsv[0] * 360;
        float saturation = hsv[1] * 100;
        float brightness = hsv[2] * 100;

        sliderH.setValue((int) hue);
        sliderS.setValue((int) saturation);
        sliderV.setValue((int) brightness);
        showColorPannel.setBackground(currentColor);
        
        sliderH.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	updateColorWindow(showColorPannel, sliderH, sliderS, sliderV);
            }
        });
        sliderS.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	updateColorWindow(showColorPannel, sliderH, sliderS, sliderV);
            }
        });
        sliderV.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	updateColorWindow(showColorPannel, sliderH, sliderS, sliderV);
            }
        });
        
        
        

    }
    
    public void updateColor(Color color) {
    	this.currentColor = color;
    }

    private void updateColorWindow(JPanel colourPannel, JSlider sliderH, JSlider sliderS, JSlider sliderV) {
        float hue = sliderH.getValue() / 360f;
        float saturation = sliderS.getValue() / 100f;
        float value = sliderV.getValue() / 100f;

        Color color = Color.getHSBColor(hue, saturation, value);
        colourPannel.setBackground(color);
        this.currentColor = color;
    }
    
    public void showWindow() {
    	frame.setVisible(true);
    	frame.setLocationRelativeTo(null);
    }
    
    private void showErrorMessage(String errorMessage) {
        ErrorPage errorPage = new ErrorPage();
        errorPage.showWindow(errorMessage);
    }
}
