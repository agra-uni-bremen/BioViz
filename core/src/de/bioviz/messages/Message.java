package de.bioviz.messages;

import com.badlogic.gdx.graphics.Color;

/**
 * A text message that will be displayed for some time at the top of the screen.
 *
 * @author jannis
 */
public class Message {

	/**
	 * The messages that will be displayed on the top of the screen.
	 */
	public String message;

	/**
	 * The time the message will be displayed in milliseconds.
	 */
	public long displayTime = 5000;

	/**
	 * The time when the message was created.
	 *
	 * The variable will be used to check whether the message is still to be
	 * displayed.
	 */
	public long createdOn;

	/**
	 * The color used for displaying the message.
	 *
	 * TODO this value is never set, i.e. is always null.
	 * This is, obviously, useless. The MessageCenter even checks whether it
	 * is null or not before chosing a color on its own.
	 */
	public Color color;

	/**
	 *
	 * @param msg The message that will be displayed.
	 */
	public Message(final String msg) {
		this.createdOn = System.currentTimeMillis();
		this.message = msg;
	}
}
