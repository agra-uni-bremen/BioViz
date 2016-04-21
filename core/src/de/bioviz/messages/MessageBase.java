package de.bioviz.messages;

import com.badlogic.gdx.graphics.Color;

public class MessageBase {
	/**
	 * The message that will be displayed on the top of the screen.
	 */
	public String message;

	/**
	 * The color used for displaying the message.
	 */
	public Color color;

	public MessageBase(final String message) {
		this.message = message;
		this.color = Color.WHITE.cpy();
	}

}
