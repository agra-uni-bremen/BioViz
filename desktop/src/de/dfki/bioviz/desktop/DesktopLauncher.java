package de.dfki.bioviz.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.dfki.bioviz.RevVisGDX;


public class DesktopLauncher extends JFrame {

	public JSlider time;
	public static DesktopLauncher singleton;
	
	public DesktopLauncher(int timeMax) {
		singleton = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		RevVisGDX revVis = new RevVisGDX();

		LwjglAWTCanvas canvas = new LwjglAWTCanvas(revVis);// LwjglAWTCanvas(revVis, false);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(128, 600));

		JLabel label = new JLabel("<html><body>Totally classic<br/>UI elements<br/>for Olli &lt;3</body></html>");

		JButton defaultButton = new JButton();
		defaultButton.setText("Autoplay");
		defaultButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            RevVisGDX.singleton.currentCircuit.autoAdvance = !RevVisGDX.singleton.currentCircuit.autoAdvance;
	        }
	    });
		
		time = new JSlider(JSlider.HORIZONTAL, 0, timeMax, 0);
		time.setPreferredSize(new Dimension(128, 64));
		time.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent ce) {
	            RevVisGDX.singleton.currentCircuit.currentTime = ((JSlider)ce.getSource()).getValue();
	        }
	    });
		
		panel.add(label);
		panel.add(defaultButton);
		panel.add(time);
		
		
		container.add(panel, BorderLayout.WEST);
		container.add(canvas.getCanvas(), BorderLayout.CENTER);

		pack();
		setVisible(true);
		setSize(800, 600);
	}

	public static void main(String[] args) {

		try {
			// Set System L&F
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}

		JFrame frame = new DesktopLauncher(10);

//		JPanel panel = new JPanel();
//		panel.setLayout(new FlowLayout());
//
//		JLabel label = new JLabel("This is a label!");
//
//		JButton button = new JButton();
//		button.setText("Press me");
//
//		panel.add(label);
//		panel.add(button);
//
//		frame.add(panel);
//		frame.setSize(300, 300);
//		frame.setLocationRelativeTo(null);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);
	}
}
