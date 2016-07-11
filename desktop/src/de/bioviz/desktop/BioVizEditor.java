package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	public BioVizEditor(BioViz bioViz){
		// width and height of the editor window
		int height = 500;
		int width = 400;
		currentViz = bioViz;
		dialog = new JDialog();
		dialog.setTitle("BioViz Editor");
		dialog.setLayout(new BorderLayout());
		editPane = new JEditorPane();
		dialog.setPreferredSize(new Dimension(width, height));
		dialog.setMinimumSize(new Dimension(width, height));

		JToolBar toolBar = new JToolBar("Hello");
		toolBar.setFloatable(false);
		JButton reload = new JButton("Preview");
		JButton save = new JButton("Save");

		reload.addActionListener(
				(e) -> {
					logger.debug("Storing file in tmp.");
					try {
						File tmpFile = File.createTempFile(file.getName(), ".BioViz_tmp", null);
						tmpFile.deleteOnExit();
						writeToFile(tmpFile);
						currentViz.scheduleLoadingOfNewFile(tmpFile);
					} catch (IOException e1) {
						logger.error("Could not create tmp file to store preview.");
					}
				}
		);

		save.addActionListener(
				(e) -> {
					logger.debug("Saving file.");
					currentViz.scheduleLoadingOfNewFile(file);
					writeToFile(file);
				}
		);

		dialog.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				currentViz.scheduleLoadingOfNewFile(file);
			}

		});

		toolBar.add(reload);
		toolBar.add(save);

		dialog.add(toolBar, BorderLayout.NORTH);

		JScrollPane editScrollPane = new JScrollPane(editPane);
		editScrollPane.setPreferredSize(new Dimension(width, height));
		editScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		dialog.add(editScrollPane, BorderLayout.CENTER);
	}

	public void show(){
		dialog.setVisible(true);
	}

	public void hide(){
		dialog.setVisible(false);
	}

	public void setFile(final File f){

		this.file = f;
		if(this.file != null) {
			try {
				editPane.setPage(this.file.toURI().toURL());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writed the content of the EditPane into the given file
	 * @param file the file to write to
	 */
	public void writeToFile(final File file){
		if(file == null){
			throw new IllegalArgumentException("No such file.");
		}
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(editPane.getText());
			fileWriter.flush();
			fileWriter.close();
		}catch(IOException e){
			logger.error("File not found or not writable " + file.getAbsolutePath());
		}
	}
}
