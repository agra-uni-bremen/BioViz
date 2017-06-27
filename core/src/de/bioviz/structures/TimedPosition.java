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
 * @author Jannis Stoppe
 * Note: This class has a natural ordering that is inconsistent with equals.
 */
class TimedPosition implements Comparable<TimedPosition> {
	/** The timing of this position. */
	long time;

	/** x-coordinate of the position. */
	int x;

	/** y-cordinate of the position. */
	int y;

	/**
	 * @param t Timing
	 * @param x x-coordinate of the position
	 * @param y y-coorinate of the position
	 */
	TimedPosition(final long t, final int x, final int y) {
		this.time = t;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return The time of this position
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return The x-coordinate of this position
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return The y-coordinate of this position
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return The position as a Point
	 */
	public Point getPos() {
		return new Point(x, y);
	}

	@Override
	/**
	 * Compares two timed positions by their time.
	 *
	 * The actual position of the timed position is *not* considered!
	 *
	 * @return Long.compar(this.time, t.time)
	 */
	public int compareTo(final TimedPosition t) {
		return Long.compare(this.time, t.time);
	}
}
