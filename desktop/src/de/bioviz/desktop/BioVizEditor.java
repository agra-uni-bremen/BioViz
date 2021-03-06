/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
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
	private JFrame frame;

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
		frame = new JFrame();
		frame.setTitle("BioViz Editor");
		frame.setLayout(new BorderLayout());

		// stop the canvas from doing stuff when editing
		frame.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowGainedFocus(final WindowEvent e) {
				DesktopLauncher.setAllowHotkeys(false);
			}

			@Override
			public void windowLostFocus(final WindowEvent e) {
				DesktopLauncher.setAllowHotkeys(true);
			}
		});

		editPane = new JEditorPane();
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));

		JToolBar toolBar = new JToolBar("Hello");
		toolBar.setFloatable(false);
		JButton reload = new JButton("Preview");
		JButton save = new JButton("Save");

		// Save the file into tmp and load the tmp file.
		reload.addActionListener(
				action -> {
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
				action -> {
					logger.debug("Saving file.");
					writeToFile(file);
					currentViz.callReloadFileListeners();
				}
		);

		// Reload the file in currentViz on close.
		// If the changes were only previewed we reload the old file.
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				currentViz.callReloadFileListeners();
			}

		});

		toolBar.add(reload);
		toolBar.add(save);

		frame.add(toolBar, BorderLayout.NORTH);

		JScrollPane editScrollPane = new JScrollPane(editPane);
		editScrollPane.setPreferredSize(new Dimension(width, height));
		editScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		frame.add(editScrollPane, BorderLayout.CENTER);
	}

	/**
	 * Shows the Editor window.
	 */
	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Sets the path to the icon image to use.
	 * @param iconPath the path to the icon image
	 */
	public void setIcon(final String iconPath) {
		try {
			frame.setIconImage(ImageIO.read(getFileFromStream(iconPath)));
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
				logger.error("Failed to set editPane page");
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
		try (FileWriter fileWriter = new FileWriter(storageFile)) {
			fileWriter.write(editPane.getText());
			fileWriter.flush();
		} catch (final IOException e) {
			logger.error("File not found or not writable " +
					storageFile.getAbsolutePath());
		}
	}
}
