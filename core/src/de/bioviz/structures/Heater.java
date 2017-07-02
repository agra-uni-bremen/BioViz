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
 * @author Oliver Keszocze
 */
public class Heater extends Resource {

	/**
	 * Creates a new heater at the given position.
	 *
	 * @param pos
	 * 		Position of the heater.
	 */
	public Heater(final Rectangle pos) {
		super(pos, ResourceType.heater);
	}


	@Override
	/**
	 * @return String representation: 'Heater: <pos>'
	 */
	public String toString() {
		return "Heater: " + position;
	}
}

