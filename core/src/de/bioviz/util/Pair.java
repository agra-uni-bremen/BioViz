package de.bioviz.util;

/**
 * Created by keszocze on 27.07.15.
 */
public class Pair<K, V> {

	public final K first;
	public final V second;

	public static <K, V> Pair<K, V> mkPair(final K first, final V second) {
		return new Pair<K, V>(first, second);
	}

	public Pair(final K first, final V second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		return first.equals(((Pair) o).first) && second.equals(((Pair) o).second);
	}

	/**
	 * @brief Returns the string representation of the pair
	 * @return String of the form "(first,second)"
	 */
	public String toString() {
		return "(" + first + "," + second + ")";
	}

}
