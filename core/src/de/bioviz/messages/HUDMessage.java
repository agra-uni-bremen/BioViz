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

package de.bioviz.messages;


/**
 * @author Jannis Stoppe
 */
public class HUDMessage extends MessageBase {


	/**
	 * The message's x-position.
	 */
	public final float x;

	/**
	 * The messages's y-position.
	 */
	public final float y;


	/**
	 *
	 * Creates a HUDMessage that is to be displayed at the specified position.
	 *
	 * @param message The message to display
	 * @param x The x-position for the message
	 * @param y The y-position for the message
	 */
	HUDMessage(final String message, final float x, final float y) {
		super(message);
		this.x = x;
		this.y = y;
	}
}
