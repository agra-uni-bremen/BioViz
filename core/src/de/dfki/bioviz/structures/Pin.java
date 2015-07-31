package de.dfki.bioviz.structures;

import java.util.ArrayList;

/**
 * Created by keszocze on 31.07.15.
 */
public class Pin {

	public final int pinID;

	public final ArrayList<Point> cells = new ArrayList<>();

	public Pin(int pinID) {
		this.pinID=pinID;
	}

	public Pin(int pinID, ArrayList<Point> cells) {
		this(pinID);
		this.cells.addAll(cells);
	}
}
