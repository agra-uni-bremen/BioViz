package de.dfki.revvisgdx;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class ClassicUIContainer extends JFrame {

	public ClassicUIContainer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container container = getContentPane();
		container.setLayout(new BorderLayout());

		LwjglAWTCanvas canvas = new LwjglAWTCanvas(new RevVisGDX(), false);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JLabel label = new JLabel("This is a label!");

		JButton button = new JButton();
		button.setText("Press me");

		panel.add(label);
		panel.add(button);
		container.add(panel, BorderLayout.WEST);
		container.add(canvas.getCanvas(), BorderLayout.CENTER);

		pack();
		setVisible(true);
		setSize(800, 600);
	}

	public static void main(String[] args) {

		JFrame frame = new ClassicUIContainer();

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
