package de.bioviz.structures;

import java.util.ArrayList;

/**
 * Class for a Pin.
 * <p>
 * A pin is a device that controls the actuations of multiple cells
 * simultaneously.
 */
public class Pin {

	/**
	 * The ID of the pin.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	public final int pinID;

	/**
	 * The cells that are controlled by this pin.
	 */
	public final ArrayList<Point> cells = new ArrayList<>();

	/**
	 * Creates a pin that is not connected to any cells.
	 * @param pinID The ID of this pin
	 */
	public Pin(final int pinID) {
		this.pinID = pinID;
	}

	/**
	 * Creates a pint that is connected to a single cell.
	 * @param pinID The ID of this pin.
	 * @param pos The position of the cell this pin is connected to.
	 */
	public Pin(final int pinID, final Point pos) {
		this(pinID);
		cells.add(pos);
	}

	/**
	 * Creates a String representation of this pin object.
	 * @return "Pin=" [ID]
	 */
	public String toString() {
		return "Pin=" + pinID;
	}


}
