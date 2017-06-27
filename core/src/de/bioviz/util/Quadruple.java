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

package de.bioviz.util;

/**
 * A very simple quadruple class
 * <p>
 * This class is necessary as, for no good reason at all, Java does not come
 * with any means of creating tuples.
 *
 * @param <F>
 * 		Type for the first entry of the quadruple
 * @param <S>
 * 		Type for the second entry of the quadruple
 * @param <T>
 * 		Type for the third entry of the quadruple
 * @param <L>
 * 		Type for the fourth entry of the quadruple
 * @author Oliver Keszocze
 */
public class Quadruple<F, S, T, L> {
	/**
	 * First stored data.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	public final F fst;

	/**
	 * Second stored data.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	public final S snd;

	/**
	 * Third stored data.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	public final T thd;

	/**
	 * Fourth stored data.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	public final L fth;

	/**
	 * @param first
	 * 		First entry of the quadruple
	 * @param second
	 * 		Second entry of the quadruple
	 * @param third
	 * 		Third entry of the quadruple
	 * @param fourth
	 * 		Fourth entry of the quadruple
	 */
	public Quadruple(final F first, final S second, final T third, final L
			fourth) {
		fst = first;
		snd = second;
		thd = third;
		fth = fourth;
	}

	/**
	 * @param that
	 * 		The object to compare against
	 * @return True iff o is logically equivalent to the object
	 * @brief Checks equality of the Quadruple with another object.
	 * <p>
	 * An object is treated as equals if it also is a quadruple and the values
	 * in the positions are equal as determined by their equals() method.
	 */
	@Override
	public boolean equals(final Object that) {

		if (this == that) {
			return true;
		}

		if (that == null || getClass() != that.getClass()) {
			return false;
		}


		Quadruple thatQ = (Quadruple) that;

		return fst.equals(thatQ.fst) && snd.equals(thatQ.snd) &&
			   thd.equals(thatQ.thd) && fth.equals(thatQ.fth);
	}

	@Override
	/**
	 * @return The sum of the hashcodes
	 */
	public int hashCode() {
		return fst.hashCode() + snd.hashCode() + thd.hashCode() +
			   fth.hashCode();
	}


	@Override
	/**
	 * @return String representing the Pair of the form '(' + first + ',' +
	 * second
	 * + ',' + third + ',' fourth ')'
	 */
	public String toString() {
		return "(" + fst + "," + snd + "," + thd + "," + fth + ")";
	}

}
