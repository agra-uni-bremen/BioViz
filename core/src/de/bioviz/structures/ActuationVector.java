package de.bioviz.structures;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by keszocze on 31.07.15.
 */

/**
 * @author Oliver Keszöcze
 */
public class ActuationVector {

	public static String actToString(Actuation a) {
		String act;
		switch (a) {
			case ON:
				act = "1";
				break;
			case OFF:
				act = "0";
				break;
			default:
				act = "X";
		}
		return act;
	}

	public final ArrayList<Actuation> vec = new ArrayList<>();

	public static Actuation getActuation(char c) {
		if (c == '1') {
			return Actuation.ON;
		}
		if (c == '0') {
			return Actuation.OFF;
		}
		return Actuation.DONTCARE;
	}


	public ActuationVector(String s) {
		for (int i = 0; i < s.length(); ++i) {
			vec.add(getActuation(s.charAt(i)));
		}
	}

	public Actuation get(int i) {
		return vec.get(i);
	}

	public boolean isEmpty() {
		return vec.isEmpty();
	}

	public int size() {
		return vec.size();
	}

	public String toString() {
		return vec.stream().map(a -> actToString(a)).collect(Collectors.joining());
	}
}
