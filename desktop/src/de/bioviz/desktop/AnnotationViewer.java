/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU
 * General Public License along with BioViz. If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import de.bioviz.ui.DrawableAssay;

import java.util.List;

/**
 * Created by max on 7/25/16.
 */
public class AnnotationViewer extends TextViewer {

	/** The bioviz instance. */
	private BioViz currentViz;

	/**
	 * Constructs a new AnnotationViewer.
	 * @param bioViz the bioviz instance.
	 */
	public AnnotationViewer(final BioViz bioViz) {
		frame.setTitle("Annotations");
		currentViz = bioViz;
	}

	/**
	 * Loads the annotations.
	 */
	private void loadAnnotations() {
		DrawableAssay currentAssay = currentViz.currentAssay;
		if (currentAssay != null) {
			List<String> annotations = currentAssay.getData().getAnnotations();
			for (final String annotation : annotations) {
				addLine(annotation.substring(2));
			}
		}
	}

	/**
	 * Reloads the contents.
	 */
	public void reload() {
		textArea.setText("");
		loadAnnotations();
	}
}
