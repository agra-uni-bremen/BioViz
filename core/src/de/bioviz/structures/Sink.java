package de.bioviz.structures;

/**
 * @author Oliver Keszocze
 */
public class Sink extends BiochipField {


	/**
	 * Creates a sink.
	 *
	 * @param pos The position of the sink on the field.
	 * @param parent The Biochip this sink belongs to.
	 */
	public Sink(final Point pos,
				final Biochip parent) {
		super(pos, parent);
	}
}
