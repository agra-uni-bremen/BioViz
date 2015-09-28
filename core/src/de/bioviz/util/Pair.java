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

	/**
	 * @param fst
	 * 		Value for the first entry
	 * @param snd
	 * 		Value for the second entry
	 * @param <K>
	 * 		Type of the first entry
	 * @param <V>
	 * 		Type of the second entry
	 * @return A pair combining the provided values in a tuple
	 * @brief Creates a tuple from two values
	 */
	public static <K, V> Pair<K, V> mkPair(final K fst, final V snd) {
		return new Pair<K, V>(fst, snd);
	}

	/**
	 * @param fst
	 * 		Value for the first entry
	 * @param snd
	 * 		Value for the second entry
	 * @brief Constructor tying together two values of arbitrary type
	 */
	public Pair(final K fst, final V snd) {
		this.first = fst;
		this.second = snd;
	}


	/**
	 * @param o
	 * 		The object to compare against
	 * @return True iff o is logically equivalent to the object
	 * @brief Compares to pairs for
	 */
	@Override
	public final boolean equals(final Object o) {

		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Pair other = (Pair)o;
		return first.equals(other.first) && second.equals(other.second);
	}

	/**
	 * @return String representing the Pair of the form '(' + first + ',' +
	 * second + ')'
	 */
	public final String toString() {
		return "(" + first + "," + second + ")";
	}

}