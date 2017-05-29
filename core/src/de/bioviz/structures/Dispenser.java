package de.bioviz.structures;

/**
 * @author Oliver Keszocze
 */
public class Dispenser extends BiochipField {

	/**
	 * The fluid ID that is dispensed by this dispenser.
	 */
	public final int fluidID;



	/**
	 * Creates a Dispenser on a biochip.
	 *
	 * Note that the direction from which the fluid is dispensed must not be
	 * part of the grid.
	 *
	 * @param pos The position to which droplet are dispensed
	 * @param fluidID The fluid ID of the liquid that is dispensed.
	 * @param parent The biochip this dispenser belongs to.
	 */
	public Dispenser(final Point pos, final int fluidID,
					  final Biochip parent) {
		super(pos, parent);
		this.fluidID = fluidID;
	}
}
