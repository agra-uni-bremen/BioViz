package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

/**
 * Class for storing the information about how a drawable should be drawn.
 *
 * @author Oliver Keszocze
 */
public class DisplayValues {

	/*
	The following values store the drawing order meaning which things get
	drawn first
	 */

	/**
	 * The default field depth.
	 *
	 * As the depth is 0 it will always be drawn first (i.e. at the bottom
	 * of all drawings).
	 */
	public static final float DEFAULT_FIELD_DEPTH = 0f;

	/**
	 * Th default droplet depth.
	 *
	 * Only fields are drawn earlier.
	 */
	public static final float DEFAULT_DROPLET_DEPTH = 10f;

	/**
	 * The default line depth.
	 *
	 * Lines are drawn above droplets.
	 */
	public static final float DEFAULT_LINE_DEPTH = 15f;

	/**
	 * The default route depth.
	 *
	 * Routes are drawn on top of everything.
	 */
	public static final float DEFAULT_ROUTE_DEPTH = 20f;

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
	 * @param color
	 * 		The color of the drawable
	 * @param msg
	 * 		The message displayed above the drawable
	 * @param texture
	 * 		The texture of the drawable
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
	 * Returns a copy of the color of the drawable.
	 *
	 * @return the color of the drawable.
	 */
	public Color getColor() {
		return color.cpy();
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
