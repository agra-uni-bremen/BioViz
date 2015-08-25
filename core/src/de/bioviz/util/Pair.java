package de.bioviz.util;

/**
 * Created by keszocze on 27.07.15.
 */
public class Pair<K, V> {

	public final K first;
	public final V second;

	public static <K, V> Pair<K, V> mkPair(K first, V second) {
		return new Pair<K, V>(first, second);
	}

	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Pair other = (Pair)o;
		return first.equals(other.first) && second.equals(other.second);
	}

	public String toString() {
		return "(" + first + "," + second + ")";
	}

}