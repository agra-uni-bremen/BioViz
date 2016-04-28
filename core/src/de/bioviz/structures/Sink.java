package de.bioviz.structures;

/**
 * @author Oliver Keszocze
 */
public class Sink extends BiochipField {

	Direction direction;

	public Sink(Point pos, Direction direction, Biochip parent) {
		super(pos,parent);
		this.direction = direction;
	}
}
