package de.bioviz.messages;

/**
 * A text message that will be displayed for some time at the top of the
 * screen.
 *
 * @author jannis
 */
public class Message extends MessageBase {

	/**
	 * The time the message will be displayed in milliseconds.
	 */
	public long displayTime = 5000;

	/**
	 * The time when the message was created.
	 * <p>
	 * The variable will be used to check whether the message is still to be
	 * displayed.
	 */
	public long createdOn;

	/**
	 * @param msg
	 * 		The message that will be displayed.
	 */
	public Message(final String msg) {
		super(msg);
		this.createdOn = System.currentTimeMillis();
	}

	/**
	 * Computes whether the messages has expired.
	 *
	 * @return true if the messages has epxired, false otherwise
	 */
	public boolean expired() {
		long currentTime = System.currentTimeMillis();

		return createdOn + displayTime <= currentTime;
	}
}
