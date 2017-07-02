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

package de.bioviz.structures;

/**
 * This class stores the informations for an areaAnnotation.
 *
 * @author Maximilian Luenert
 */
public class AreaAnnotation extends Resource {

	/**
	 * The annotation text.
	 */
	private String annotation;

	/**
	 * Constructs a new AreaAnnotation with the given rectangle an annotation.
	 *
	 * @param rectangle
	 * 		the position of the new AreaAnnotation
	 * @param annotation
	 * 		the text of the new AreaAnnotation
	 */
	public AreaAnnotation(final Rectangle rectangle, final String annotation) {
		super(rectangle, ResourceType.areaAnnotation);
		this.annotation = annotation;
	}

	/**
	 * Returns a String representation of the AreaAnnotation.
	 *
	 * @return string representation
	 */
	@Override
	public String toString() {
		return annotation + position;
	}

	/**
	 * Gets the annotation text.
	 *
	 * @return String
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * Sets the annotation text.
	 *
	 * @param annotation
	 * 		the annotation text
	 */
	public void setAnnotation(final String annotation) {
		this.annotation = annotation;
	}

}
