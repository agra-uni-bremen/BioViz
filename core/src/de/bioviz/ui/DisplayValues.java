package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 * @brief Class for storing the information about how a drawable should be drawn
 *
 * @author Oliver Keszöcze
 */
public class DisplayValues {

	//! The color of the drawable
	public final Color color;
	//! The message displayed above the drawable
	public final String msg;
	//! The texture of the drawable
	public final TextureRegion texture;

	/**
	 *
	 * @param color The color of the drawable
	 * @param msg The message displayed above the drawable
	 * @param texture The texture of the drawable
	 */
	public DisplayValues(final Color color, final String msg, final TextureRegion texture) {
		this.color = color;
		this.msg = msg;
		this.texture = texture;
	}
}
