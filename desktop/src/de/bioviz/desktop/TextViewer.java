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
 * Created by max on 7/25/16.
 */
public class TextViewer {

	/**
	 * Logger instance.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(TextViewer.class);

	/** The JFrame instance. */
	JFrame frame;
	/** The texArea instance. */
	JTextArea textArea;

	/**
	 * Creates a new TextViewer with a default size.
	 */
	public TextViewer() {
		final int height = 400;
		final int width = 300;
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(width, height));
		textArea = new JTextArea(1, 1);
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
	void addLine(final String s) {
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
	 * Sets the icon image.
	 * @param iconPath the icon image path
	 */
	public void setIcon(final String iconPath) {
		try {
			frame.setIconImage(ImageIO.read(getFileFromStream(iconPath)));
		} catch (final IOException e) {
			logger.error("Could not load icon image.");
		}
	}

	/**
	 * Makes the viewer visible.
	 */
	public void show() {
		frame.setVisible(true);
	}
}
