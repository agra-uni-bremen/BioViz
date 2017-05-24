package de.bioviz.structures;

import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Model of an actuation vector.
 * <p>
 * The index within the vector corresponds to the time step.
 * <p>
 * Created by keszocze on 31.07.15.
 *
 * @author Okeszocze
 */
public class ActuationVector {

	/**
	 * A simple ArrayList stores the actual actuation states.
	 */
	private ArrayList<Actuation> vec = new ArrayList<>();

	/**
	 * @param val
	 * 		The Actuation value
	 * @param len
	 * 		The length of the ActuationVector
	 * @brief Creates an Actuation vector of given length that contains the
	 * specified value only.
	 */
	public ActuationVector(final Actuation val, final int len) {
		vec.ensureCapacity(len);
		for (int i = 0; i < len; ++i) {
			vec.add(val);
		}
	}

	/**
	 * Creates an actuation vector from a string.
	 * <p>
	 * The string is, char by char, transformed into an actuation vector by
	 * calling the charToActuation method.
	 *
	 * @param s
	 * 		String representing an actuation vector
	 */
	public ActuationVector(final String s) {
		for (int i = 0; i < s.length(); ++i) {
			vec.add(charToActuation(s.charAt(i)));
		}
	}


	/**
	 * Converts an Actuation into a String.
	 * <p>
	 * Actuation.ON -> "1"
	 * Actuation.OFF -> "0"
	 * Actuation.DONTCARE -> "X"
	 *
	 * @param a
	 * 		Actuation that is to converted into a String
	 * @return String representation of a single actuation
	 */
	public static String actuationToString(final Actuation a) {
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

	/**
	 * Converts a character into an Actuation.
	 * <p>
	 * A '1' becomes Actuation.ON, a '0' becomes Actuation.OFF all other
	 * characters are mapped to Actuation.DONTCARE.
	 *
	 * @param c
	 * 		The character to convert
	 * @return The Actuation that was encoded in the character
	 */
	public static Actuation charToActuation(final char c) {
		if (c == '1') {
			return Actuation.ON;
		}
		if (c == '0') {
			return Actuation.OFF;
		}
		return Actuation.DONTCARE;
	}

	/**
	 *
	 * @param pos The position whose actuation is being set
	 * @param act What actuation the position should have
	 */
	public void set(final int pos, final Actuation act) {
		vec.set(pos, act);
	}

	/**
	 * Retrieves an entry of the actuation vector.
	 * <p>
	 * Note that this method does not perform any bound checks. That means that
	 * the container used to store the actuation entries may throw an
	 * exception.
	 *
	 * @param i
	 * 		Position within the vector
	 * @return The actuation at position i
	 */
	public Actuation get(final int i) {
		return vec.get(i);
	}


	/**
	 * @return true if the vector is empty, false otherwise
	 */
	public boolean isEmpty() {
		return vec.isEmpty();
	}

	/**
	 * @return The size of the actuation vector
	 */
	public int size() {
		return vec.size();
	}


	/**
	 * String representation of the actuation vector.
	 * <p>
	 * The representation is created by mapping the vector's entries using the
	 * actuationToString method and then concatenating them.
	 *
	 * @return String representation of the actuation vector
	 */
	public String toString() {
		return vec.stream().map(ActuationVector::actuationToString).collect(
				Collectors.joining());
	}
}
