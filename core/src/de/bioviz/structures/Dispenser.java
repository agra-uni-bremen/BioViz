package de.bioviz.structures;

/**
 * @author Oliver Keszocze
 */
public class Dispenser extends BiochipField {

	int fluidID;
	Direction direction;

	public Dispenser(Point pos, int fluidID, Direction dispenseFrom, Biochip parent) {
		super(pos,parent);
		this.fluidID = fluidID;
		this.direction=dispenseFrom;
	}

}
