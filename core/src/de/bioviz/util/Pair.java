
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

package de.bioviz.util;

/**
 * A very simple tuple class.
 *
 * This class is necessary as, for no good reasons at all, Java does not come
 * with any means of creating tuples.
 *
 * @param <K>
 * 		Type for the first entry of the tuple
 * @param <V>
 * 		Type for the snd entry of the tuple
 *
 * @author Oliver Keszocze
 */
public class Pair<K, V> {

	/**
	 * The first entry of the tuple.
	 */
	public final K fst;

	/**
	 * The second entry of the tuple.
	 */
	public final V snd;

	/**
	 * @brief Creates a new pair containing the values provided as parameters.
	 * @param fst Value of the first entry of the pair
	 * @param snd Value of the second entry of the pair
	 */
	public Pair(final K fst, final V snd) {
		this.fst = fst;
		this.snd = snd;
	}

	/**
	 *
	 * @param fst Value of the first entry of the pair
	 * @param snd Value of the second entry of the pair
	 * @param <K> Type of the first entry of the pair
	 * @param <V> Type of the second entry of the pair
	 * @return Pair of type (K,V) with values (fst,snd)
	 */
	public static <K, V> Pair<K, V> mkPair(final K fst, final V snd) {
		return new Pair<K, V>(fst, snd);
	}


	/**
	 * @param o
	 * 		The object to compare against
	 * @return True iff o is logically equivalent to the object
	 * @brief Checks equality of the pair with another object.
	 * <p>
	 * An object is treated as equals if it also is a pair and the values in the
	 * first and snd position are equal as determined by their equals() method.
	 */
	@Override
	public boolean equals(final Object o) {

		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		return fst.equals(((Pair) o).fst) && snd.equals(((Pair) o).snd);
	}

	@Override
	/**
	 * @return The sum of the hashcodes, i.e. fst.hashCode()+snd.hashCode()
	 */
	public int hashCode() {
		return fst.hashCode() + snd.hashCode();
	}


	@Override
	/**
	 * @return String representing the Pair of the form '(' + first + ',' + snd
	 * + ')'
	 */
	public String toString() {
		return "(" + fst + "," + snd + ")";
	}




}
