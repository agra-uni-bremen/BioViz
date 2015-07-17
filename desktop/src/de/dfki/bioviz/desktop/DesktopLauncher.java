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

import de.dfki.bioviz.BioVizEvent;
import de.dfki.bioviz.BioVizGDX;
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
		
		BioVizGDX revVis = new BioVizGDX();

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
	            BioVizGDX.singleton.currentCircuit.autoAdvance = !BioVizGDX.singleton.currentCircuit.autoAdvance;
	        }
	    });
		
		time = new JSlider(JSlider.HORIZONTAL, 0, timeMax, 0);
		time.setPreferredSize(new Dimension(128, 64));
		time.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent ce) {
	            BioVizGDX.singleton.currentCircuit.currentTime = ((JSlider)ce.getSource()).getValue();
	        }
	    });
		tc = new timerCallback(time);
		
		JButton adjacencyButton = new JButton();
		adjacencyButton.setText("(A)djacency");
		adjacencyButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            BioViz.singleton.currentCircuit.toggleHighlightAdjacency();
	        }
	    });
		
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
			BioVizGDX.singleton.addTimeChangedListener(this);
		}
		@Override
		public void bioVizEvent() {
			this.time.setValue((int)BioVizGDX.singleton.currentCircuit.currentTime);
		}
		
	}
}
