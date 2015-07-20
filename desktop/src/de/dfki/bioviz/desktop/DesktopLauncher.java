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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

import de.dfki.bioviz.BioVizEvent;
import de.dfki.bioviz.BioViz;


public class DesktopLauncher extends JFrame {

	public JSlider time;
	timerCallback tc;
	public static DesktopLauncher singleton;
	
	public DesktopLauncher(int timeMax) {
		singleton = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		BioViz revVis = new BioViz();

		LwjglAWTCanvas canvas = new LwjglAWTCanvas(revVis);// LwjglAWTCanvas(revVis, false);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(128, 600));

		JLabel label = new JLabel("<html><body>Totally classic<br/>UI elements<br/></body></html>");

		JButton defaultButton = new JButton();
		defaultButton.setText("Autoplay");
		defaultButton.addActionListener(e -> BioViz.singleton.currentCircuit.autoAdvance = !BioViz.singleton.currentCircuit.autoAdvance);
		
		time = new JSlider(JSlider.HORIZONTAL, 0, timeMax, 0);
		time.setPreferredSize(new Dimension(128, 64));
		time.addChangeListener(ce -> BioViz.singleton.currentCircuit.currentTime = ((JSlider) ce.getSource()).getValue());
		tc = new timerCallback(time);
		
		JButton adjacencyButton = new JButton();
		adjacencyButton.setText("(A)djacency");
		adjacencyButton.addActionListener(e -> BioViz.singleton.currentCircuit.toggleHighlightAdjacency());
		
		panel.add(label);
		panel.add(defaultButton);
		panel.add(time);
		
		panel.add(adjacencyButton);
		
		
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
	}
	
	private class timerCallback implements BioVizEvent {
		private JSlider time;
		public timerCallback(JSlider slider) {
			this.time = slider;
			BioViz.singleton.addTimeChangedListener(this);
		}
		@Override
		public void bioVizEvent() {
			this.time.setValue((int)BioViz.singleton.currentCircuit.currentTime);
		}
		
	}
}
