package de.bioviz.structures;

/**
 * Note that currently only the abstraction of a mixer is used. You can not
 * define a mixer to be a certain set of movements (e.g., a spiral).
 *
 * @author Oliver Keszocze
 */
public class Mixer {

	/**
	 * The unique ID of the mixer.
	 */
	public final int id;

	/**
	 * Stores the timing information on this mixer.
	 */
	public final Range timing;

	/**
	 * The position of the mixer.
	 */
	public final Rectangle position;

	/**
	 * Standard constructor requiring every value to be present.
	 *
	 * @param id
	 * 		Unique mixer id
	 * @param pos
	 * 		The region, given as a rectangle, that is covered by the mixer
	 * @param timing
	 * 		When the mixer is present on the chip
	 */
	public Mixer(final int id, final Rectangle pos, final Range timing) {
		this.position=pos;
		this.id = id;
		this.timing = timing;
	}

	/**
	 * @return String representation in the form "Mixer+<ID>: <timing>
	 * <position>"
	 */
	public String toString() {
		return "Mixer " + this.id + ": " + timing + " " + position;
	}

}
