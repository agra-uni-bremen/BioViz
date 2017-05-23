package de.bioviz.structures;

/**
 * @author Oliver Keszocze
 */
public class Magnet extends Resource {

	/**
	 * Creates a magnet at the specified position.
	 *
	 * @param pos
	 * 		Position of the magnet.
	 */
	public Magnet(final Rectangle pos) {
		super(pos, ResourceType.magnet);
	}

	/**
	 * @return The string representation of the Magnet: 'Magnet: <pos>'
	 */
	@Override
	public String toString() {
		return "Magnet: " + position;
	}
}
