package de.bioviz.structures;

/**
 * @author Oliver Keszocze
 */
public class Heater extends Resource {

	/**
	 * Creates a new heater at the given position.
	 *
	 * @param pos
	 * 		Position of the heater.
	 */
	public Heater(final Rectangle pos) {
		super(pos, ResourceType.heater);
	}


	@Override
	/**
	 * @return String representation: 'Heater: <pos>'
	 */
	public String toString() {
		return "Heater: " + position;
	}
}

