package de.bioviz.desktop;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.util.List;

/**
 * Created by max on 7/25/16.
 */
public class TextViewer {
	/** The JFrame instance. */
	JFrame frame;
	/** The texArea instance. */
	JTextArea textArea;

	/**
	 * Creates a new TextViewer with a default size.
	 */
	public TextViewer(){
		final int height = 400;
		final int width = 300;
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(width, height));
		textArea = new JTextArea(1,1);
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(width, height));
		frame.add(scrollPane);
		frame.pack();
	}

	/**
	 * Adds a line to the textArea.
	 * @param s the string
	 */
	private void addLine(final String s) {
		textArea.append(s);
		textArea.append("\n");
	}

	/**
	 * Adds a list of strings to the textArea.
	 * @param stringList the list of strings
	 */
	void addLines(final List<String> stringList) {
		for (final String s : stringList) {
			addLine(s);
		}
	}

	/**
	 * Makes the viewer visible.
	 */
	public void show() {
		frame.setVisible(true);
	}
}
