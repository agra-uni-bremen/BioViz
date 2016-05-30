package de.bioviz.structures;

/**
 * @author Oliver Keszocze
 */
public class Sink extends BiochipField {

	/**
	 * The direction from which the sink is entered.
	 */
	Direction direction;

	/**
	 * Creates a sink.
	 *
	 * @param pos The position of the sink on the field.
	 * @param direction The direction from which the sink is entered.
	 * @param parent The Biochip this sink belongs to.
	 */
	public Sink(final Point pos,
				final Direction direction,
				final Biochip parent) {
		super(pos, parent);
		this.direction = direction;
	}
}
