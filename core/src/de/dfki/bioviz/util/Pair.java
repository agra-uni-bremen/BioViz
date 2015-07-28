package de.dfki.bioviz.util;

/**
 * Created by keszocze on 27.07.15.
 */
public class Pair<K, V> {

	private final K first;
	private final V second;

	public static <K, V> Pair<K, V> mkPair(K first, V second) {
		return new Pair<K, V>(first, second);
	}

	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}

	public K first() {
		return first;
	}

	public V second() {
		return second;
	}

}