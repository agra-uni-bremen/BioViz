package de.bioviz.structures;

import de.bioviz.ui.TextureE;
import de.bioviz.util.Pair;

/**
 * @author Oliver Keszocze
 */
public class Dispenser extends BiochipField {

	/**
	 * The fluid ID that is dispensed by this dispenser.
	 */
	public final int fluidID;
	public final Direction direction;

	public Dispenser(Point pos, int fluidID, Direction dispenseFrom, Biochip parent) {
		super(pos,parent);
		this.fluidID = fluidID;
		this.direction=dispenseFrom;
	}
}
