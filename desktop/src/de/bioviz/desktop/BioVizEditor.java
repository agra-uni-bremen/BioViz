package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static de.bioviz.desktop.DesktopLauncher.getFileFromStream;

/**
 * This class provides an editor to change the currently opened Bio file.
 *
 * It is possible to preview the changes and store them.
 *
 * @author Maximilian Luenert
 */
public class BioVizEditor {

	/**
	 * Logger instance.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(BioVizEditor.class);

	/**
	 * Dialog window.
	 */
	private JDialog dialog;

	/**
	 * BioViz instance.
	 */
	private BioViz currentViz;

	/**
	 * EditPane.
	 */
	private JEditorPane editPane;

	/**
	 * The edited file.
	 */
	private File file;


	/**
	 * Constructs the editor.
	 *
	 * @param bioViz the BioViz instance.
	 */
	public BioVizEditor(final BioViz bioViz) {
		// width and height of the editor window
		final int height = 500;
		final int width = 400;
		currentViz = bioViz;
		dialog = new JDialog();
		dialog.setTitle("BioViz Editor");
		dialog.setLayout(new BorderLayout());

		// stop the canvas from doing stuff when editing
		dialog.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowGainedFocus(WindowEvent e) {
				DesktopLauncher.setAllowHotkeys(false);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				DesktopLauncher.setAllowHotkeys(true);
			}
		});

		editPane = new JEditorPane();
		dialog.setPreferredSize(new Dimension(width, height));
		dialog.setMinimumSize(new Dimension(width, height));

		JToolBar toolBar = new JToolBar("Hello");
		toolBar.setFloatable(false);
		JButton reload = new JButton("Preview");
		JButton save = new JButton("Save");

		// Save the file into tmp and load the tmp file.
		reload.addActionListener(
				(action) -> {
					logger.debug("Storing file in tmp.");
					try {
						File tmpFile = File.createTempFile(file.getName(), ".BioViz_tmp", null);
						tmpFile.deleteOnExit();
						writeToFile(tmpFile);
						currentViz.scheduleLoadingOfNewFile(tmpFile);
					} catch (final IOException e) {
						logger.error("Could not create tmp file to store preview.");
					}
				}
		);

		// Save the file to disk and reload it.
		save.addActionListener(
				(action) -> {
					logger.debug("Saving file.");
					writeToFile(file);
					currentViz.callReloadFileListeners();
				}
		);

		// Reload the file in currentViz on close.
		// If the changes were only previewed we reload the old file.
		dialog.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				currentViz.callReloadFileListeners();
			}

		});

		toolBar.add(reload);
		toolBar.add(save);

		dialog.add(toolBar, BorderLayout.NORTH);

		JScrollPane editScrollPane = new JScrollPane(editPane);
		editScrollPane.setPreferredSize(new Dimension(width, height));
		editScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		dialog.add(editScrollPane, BorderLayout.CENTER);
	}

	/**
	 * Shows the Editor window.
	 */
	public void show() {
		dialog.setVisible(true);
	}

	/**
	 * Sets the path to the icon image to use.
	 * @param iconPath the path to the icon image
	 */
	public void setIcon(final String iconPath) {
		try {
			dialog.setIconImage(ImageIO.read(getFileFromStream(iconPath)));
		} catch (final IOException e) {
			logger.error("Could not load icon image.");
		}
	}

	/**
	 * Sets the file to edit.
	 *
	 * @param f the file
	 */
	public void setFile(final File f) {

		this.file = f;
		if (this.file != null) {
			try {
				editPane.setPage(this.file.toURI().toURL());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writed the content of the EditPane into the given file.
	 * @param storageFile the storageFile to write to
	 */
	public void writeToFile(final File storageFile) {
		if (storageFile == null) {
			throw new IllegalArgumentException("No such storageFile.");
		}
		try {
			FileWriter fileWriter = new FileWriter(storageFile);
			fileWriter.write(editPane.getText());
			fileWriter.flush();
			fileWriter.close();
		} catch (final IOException e) {
			logger.error("File not found or not writable " +
					storageFile.getAbsolutePath());
		}
	}
}
