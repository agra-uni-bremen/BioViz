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

package de.bioviz.structures;

/**
 * Note that currently only the abstraction of a mixer is used. You can not
 * define a mixer to be a certain set of movements (e.g., a spiral).
 *
 * @author Oliver Keszocze
 */
public class Mixer extends Resource {

	/**
	 * The unique ID of the mixer.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	public final int id;

	/**
	 * Stores the timing information on this mixer.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	public final Range timing;

	/**
	 * Standard constructor requiring every value to be present.
	 *
	 * @param id
	 * 		Unique mixer id
	 * @param pos
	 * 		The region, given as a rectangle, that is covered by the mixer
	 * @param timing
	 * 		When the mixer is present on the chip
	 */
	public Mixer(final int id, final Rectangle pos, final Range timing) {
		super(pos, ResourceType.mixer);
		this.id = id;
		this.timing = timing;
	}

	/**
	 * @return String representation in the form "Mixer+<ID>: <timing>
	 * <position>"
	 */
	public String toString() {
		return "Mixer " + this.id + ": " + timing + " " + position;
	}

}
