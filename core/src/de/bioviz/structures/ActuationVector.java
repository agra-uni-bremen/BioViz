package de.bioviz.structures;

import java.util.ArrayList;

/**
 * Created by keszocze on 31.07.15.
 */
public class ActuationVector {
	public  enum Actuation { ON, OFF , DONTCARE}

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
		for (int i=0; i< s.length(); ++i) {
			vec.add(getActuation(s.charAt(i)));
		}
	}

}
