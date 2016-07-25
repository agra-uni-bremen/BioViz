package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import static de.bioviz.desktop.DesktopLauncher.getFileFromStream;


/**
 * This class implements an errorViewer for parser errors.
 *
 */
public class ErrorViewer{

	/**
	 * Logger instance.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(ErrorViewer.class);

	/** The JFrame instance. */
	private JFrame frame;
	/** The texArea instance. */
	private JTextArea textArea;
	/** The error type. */
	ERROR_TYPE type;
	/** The bioViz instance. */
	private BioViz currentViz;

	/**
	 * Constructs a new ErrorViewer.
	 *
	 * @param bioViz the bioviz instance
	 * @param title the title for the frame
	 * @param type the type of errors to show
	 * @param iconPath the path to the icon
	 */
	public ErrorViewer(final BioViz bioViz, final String title, final
	ERROR_TYPE type) {
		final int height = 400;
		final int width = 300;
		frame = new JFrame(title);

		frame.setPreferredSize(new Dimension(width, height));
		textArea = new JTextArea(1,1);
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(width, height));
		frame.add(scrollPane);
		frame.pack();

		currentViz = bioViz;
		this.type = type;
	}

	/**
	 * Sets the icon image.
	 * @param iconPath the icon image path
	 */
	public void setIcon(final String iconPath){
		try {
			frame.setIconImage(ImageIO.read(getFileFromStream(iconPath)));
		} catch (IOException e) {
			logger.error("Could not load icon image.");
		}
	}

	/**
	 * Adds hard errors to the textArea.
	 */
	private void addErrors(){
		if (currentViz.currentCircuit != null) {
			addLines(currentViz.currentCircuit.getData().hardErrors);
		}
	}

	/**
	 * Adds soft errors to the textArea.
	 */
	private void addWarnings(){
		if (currentViz.currentCircuit != null) {
			addLines(currentViz.currentCircuit.getData().errors);
		}
	}

	/**
	 * Reloads the content for the current bioviz.
	 */
	public void reload(){
		textArea.setText("");
		if (type.equals(ERROR_TYPE.HARD)) {
			addErrors();
		} else if (type.equals(ERROR_TYPE.SOFT)) {
			addWarnings();
		} else if (type.equals(ERROR_TYPE.BOTH)) {
			addErrors();
			addWarnings();
		}
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
	private void addLines(final List<String> stringList) {
		for (final String s : stringList) {
			addLine(s);
		}
	}

	/**
	 * Makes the errorviewer visible.
	 */
	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Enum for the errorTypes.
	 */
	public enum ERROR_TYPE {
		HARD,
		SOFT,
		BOTH
	}
}
