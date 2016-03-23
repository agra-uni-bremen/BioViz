package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

/**
 *
 * Class for storing the information about how a drawable should be drawn.
 *
 * @author Oliver Keszocze
 */
public class DisplayValues {

	/**
	 * The color of the drawable.
	 */
	private Color color;

	/**
	 * The message displayed above the drawable.
	 */
	private String msg;

	/**
	 * The texture of the drawable.
	 */
	private TextureE texture;

	/**
	 *
	 * @param color The color of the drawable
	 * @param msg The message displayed above the drawable
	 * @param texture The texture of the drawable
	 */
	public DisplayValues(
			final Color color,
			final String msg,
			final TextureE texture) {
		this.color = color;
		this.msg = msg;
		this.texture = texture;
	}

	/**
	 * @return the color of the drawable.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the message displayed above the drawable.
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return the texture of the drawable.
	 */
	public TextureE getTexture() {
		return texture;
	}
}
