package de.dfki.bioviz.util;

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

	public String toString() {
		return "(" + first + "," + second + ")";
	}

}