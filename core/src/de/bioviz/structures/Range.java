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
 * Range of integers with membership tests.
 *
 * @author Oliver Keszocze
 */
public class Range {

	/**
	 * Don't care value for the range.
	 */
	public static final int DONTCARE = 0;

	/**
	 * The begin of the range.
	 */
	private final int begin;

	/**
	 * The end of the range.
	 */
	public final int end;

	/**
	 * Creates a new range given the start and end.
	 * <p>
	 * Integer values < 1 are treated as don't care.
	 *
	 * @param begin
	 * 		The beginning of the range
	 * @param end
	 * 		The end of the range
	 */
	public Range(final int begin, final int end) {

		this.begin = (begin < 1) ? DONTCARE : begin;
		this.end = (end < 1) ? DONTCARE : end;
	}

	/**
	 * Check whether an integer is in the range.
	 *
	 * @param i
	 * 		The integer to test
	 * @return true if th the integer is within the range, false otherwise
	 */
	public boolean inRange(final int i) {
		return i >= 0 && i >= begin && (end == DONTCARE || i <= end);
	}


	/**
	 * Creates a String representation of the range.
	 * <p>
	 * Don't care values are represented by '*'.
	 *
	 * @return "(" [begin] "," [end] ")"
	 */
	@Override
	public String toString() {
		String fst = (begin == DONTCARE) ? "*" : String.valueOf(begin);
		String snd = (end == DONTCARE) ? "*" : String.valueOf(end);

		return "(" + fst + "," + snd + ")";
	}
}
