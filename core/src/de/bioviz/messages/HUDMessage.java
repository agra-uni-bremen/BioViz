package de.bioviz.messages;


/**
 * @author Jannis Stoppe
 */
public class HUDMessage extends MessageBase {

	/**
	 * The message's x-position.
	 */
	public float x;

	/**
	 * The messages's y-position.
	 */
	public float y;

	/**
	 * The message's size.
	 */
	public float size;

	/**
	 *
	 * Creates a HUDMessage that is to be displayed at the specified position.
	 *
	 * The initial size of the message is negative.
	 * @TODO What does this actually mean?
	 *
	 * @param message The message to display
	 * @param x The x-position for the message
	 * @param y The y-position for the message
	 */
	public HUDMessage(final String message, final float x, final float y) {
		super(message);
		this.x = x;
		this.y = y;
		this.size = -1f;
	}
}
