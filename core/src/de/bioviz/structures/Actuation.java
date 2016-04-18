package de.bioviz.structures;

/**
 * Models the possible cell states with respect to actuation.
 * <p>
 * Created by keszocze on 14.10.15.
 *
 * @author Oliver Keszocze
 */
public enum Actuation {
	/**
	 * A cell is actuated/turned on.
	 */
	ON,
	/**
	 * A cell is not actuated/turned off.
	 */
	OFF,
	/**
	 * The actuated state of a cell is of no interest.
	 */
	DONTCARE
}
