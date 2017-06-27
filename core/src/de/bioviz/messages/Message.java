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
 * A text message that will be displayed for some time at the top of the
 * screen.
 *
 * @author Jannis Stoppe
 */
public class Message extends MessageBase {

	/**
	 * The time the message will be displayed in milliseconds.
	 */
	private long displayTime = 5000;

	/**
	 * The time when the message was created.
	 * <p>
	 * The variable will be used to check whether the message is still to be
	 * displayed.
	 */
	private long createdOn;

	/**
	 * Creates a message that will be displayed on the top of the screen.
	 *
	 * Its color is set to white as per the {@link MessageBase} constructor.
	 * @param msg
	 * 		The message that will be displayed.
	 */
	Message(final String msg) {
		super(msg);
		this.createdOn = System.currentTimeMillis();
	}

	/**
	 * Computes whether the messages has expired.
	 *
	 * @return true if the messages has expired, false otherwise
	 */
	boolean expired() {
		long currentTime = System.currentTimeMillis();

		return createdOn + displayTime <= currentTime;
	}
}
