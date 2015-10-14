package de.bioviz.util;

/**
 * Created by keszocze on 27.07.15.
 */

/**
 * @param <K>
 * 		Type for the first entry of the tuple
 * @param <V>
 * 		Type for the snd entry of the tuple
 */
public class Pair<K, V> {

	/**
	 * The first entry of the tuple.
	 */
	public final K fst;

	/**
	 * The snd entry of the tuple.
	 */
	public final V snd;

	public static <K, V> Pair<K, V> mkPair(final K first, final V second) {
		return new Pair<K, V>(first, second);
	}

	public Pair(final K fst, final V snd) {
		this.fst = fst;
		this.snd = snd;
	}


	/**
	 * @param o
	 * 		The object to compare against
	 * @return True iff o is logically equivalent to the object
	 * @brief Checks equality of the pair with another object.
	 * <p>
	 * An object is treated as equals if it also is a pair and the values in the
	 * first and snd position are equal as determined by their equals()
	 * method.
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
		return fst.hashCode()+ snd.hashCode();
	}

	/**
	 * @return String representing the Pair of the form '(' + first + ',' +
	 * snd + ')'
	 */
	public String toString() {
		return "(" + fst + "," + snd + ")";
	}

}
