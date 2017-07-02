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
 * Base class for a resource on the biochip.
 * <p>
 * These resources are: mixer, detector, heater, magnet.
 * <p>
 * They can span multiple cells or just a single one.
 *
 * @author Oliver Keszocze
 */
public class Resource {
	/**
	 * The position of the resource on the chip.
	 */
	public final Rectangle position;

	/**
	 * The type of the resource.
	 *
	 * See the enum below to see what kind of resources are available.
	 */
	public final ResourceType type;

	/**
	 * The supported resources.
	 */
	public enum ResourceType {
		/**
		 * Detector type.
		 */
		detector,
		/**
		 * Heater type.
		 */
		heater,
		/**
		 * Magnet type.
		 */
		magnet,
		/**
		 * Mixer type.
		 */
		mixer,
		/**
		 * AreaAnnotation type.
		 */
		areaAnnotation
	}

	/**
	 * Creates a resource of given type at specified position.
	 * @param position Position of the resource
	 * @param type The type of the resource
	 */
	public Resource(final Rectangle position, final ResourceType type) {
		this.position = position;
		this.type = type;
	}


}
