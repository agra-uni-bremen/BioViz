package de.bioviz.messages;


/**
 * @author Jannis Stoppe
 */
public class HUDMessage extends MessageBase {


	/**
	 * The message's x-position.
	 */
	public final float x;

	/**
	 * The messages's y-position.
	 */
	public final float y;


	/**
	 *
	 * Creates a HUDMessage that is to be displayed at the specified position.
	 *
	 * @param message The message to display
	 * @param x The x-position for the message
	 * @param y The y-position for the message
	 */
	HUDMessage(final String message, final float x, final float y) {
		super(message);
		this.x = x;
		this.y = y;
	}
}
