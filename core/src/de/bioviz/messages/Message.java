package de.bioviz.messages;

/**
 * A text message that will be displayed for some time at the top of the
 * screen.
 *
 * @author Jannis Stoppe
 */
public class Message extends MessageBase {

	/**
	 * The time the message will be displayed in milliseconds.
	 */
	private long displayTime = 5000;

	/**
	 * The time when the message was created.
	 * <p>
	 * The variable will be used to check whether the message is still to be
	 * displayed.
	 */
	private long createdOn;

	/**
	 * Creates a message that will be displayed on the top of the screen.
	 *
	 * Its color is set to white as per the {@link MessageBase} constructor.
	 * @param msg
	 * 		The message that will be displayed.
	 */
	Message(final String msg) {
		super(msg);
		this.createdOn = System.currentTimeMillis();
	}

	/**
	 * Computes whether the messages has expired.
	 *
	 * @return true if the messages has expired, false otherwise
	 */
	boolean expired() {
		long currentTime = System.currentTimeMillis();

		return createdOn + displayTime <= currentTime;
	}
}
