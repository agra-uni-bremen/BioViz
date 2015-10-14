package de.bioviz.util;

/**
 * Created by keszocze on 27.07.15.
 */

/**
 * @param <K>
 * 		Type for the first entry of the tuple
 * @param <V>
 * 		Type for the second entry of the tuple
 */
public class Pair<K, V> {

	/**
	 * The first entry of the tuple
	 */
	public final K first;

	/**
	 * The second entry of the tuple
	 */
	public final V second;

	public static <K, V> Pair<K, V> mkPair(K first, V second) {
		return new Pair<K, V>(first, second);
	}

	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}


	/**
	 * @param o
	 * 		The object to compare against
	 * @return True iff o is logically equivalent to the object
	 * @brief Compares to pairs for
	 */
	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		return first.equals(((Pair)o).first) && second.equals(((Pair)o).second);
	}

	/**
	 * @return String representing the Pair of the form '(' + first + ',' +
	 * second + ')'
	 */
	public String toString() {
		return "(" + first + "," + second + ")";
	}

}