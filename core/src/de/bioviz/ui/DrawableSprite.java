/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.NumberUtils;

import de.bioviz.messages.MessageCenter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;

/**
 * This is a wrapper for the 2d drawing methods.
 *
 * @author Jannis Stoppe
 */
public abstract class DrawableSprite implements Drawable {

	/**
	 * How much display is being shifted.
	 */
	public static final float COORDINATE_SHIFT = 0.5f;

	/**
	 * The default amount of time required to change from one color to the
	 * next.
	 */
	public static final int DEFAULT_COLOR_TRANSITION_DURATION = 500;

	/**
	 * The default value at which the sprites switch to one-block display
	 * instead of the more detailed textures.
	 */
	public static final float DEFAULT_LOD_THRESHOLD = 8f;

	/**
	 * The amount of time required to change from one color to the next.
	 */
	private static int colorTransitionDuration =
			DEFAULT_COLOR_TRANSITION_DURATION;

	/**
	 * Used to log any events occuring in the {@link DrawableSprite} class.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(DrawableSprite.class);

	/**
	 * The default alpha (i.e. visibility) value. Set to 0 because sprites are
	 * supposed to "fade in".
	 */
	private static final float DEFAULT_ALPHA = 0f;

	/**
	 * Used to store all textures.
	 */
	private static TextureManager textures;


	/**
	 * The colors that are set to the four corners of this sprite. This may be
	 * set to null if no specific per-corner colors are supposed to be set. Any
	 * color that is equal to Color.BLACK will be ignored and the currentColor
	 * will be used instead.
	 */
	protected Color[] cornerColors = null;


	/**
	 * Link to the visualization this sprite is used in.
	 */
	BioViz viz;

	/**
	 * The x coordinate of this sprite.
	 */
	private float x = 0;

	/**
	 * The y coordinate of this sprite.
	 */
	private float y = 0;

	/**
	 * The depth drawing order.
	 */
	private float z = 0;

	/**
	 * The x scaling factor of this sprite.
	 */
	private float scaleX = 1;

	/**
	 * The y scaling factor of this sprite.
	 */
	private float scaleY = 1;

	/**
	 * The rotation of this sprite.
	 */
	private float rotation = 0;

	/**
	 * Whether or not this sprite should be drawn at all.
	 */
	private boolean isVisible = true;

	/**
	 * The link back to libgdx: the sprite that is used to draw this {@link
	 * DrawableSprite}.
	 */
	private Sprite sprite;

	/**
	 * The color that is currently being targeted (important for the smooth
	 * transition, together with currentColor and originColor).
	 */
	private Color targetColor = Color.WHITE.cpy();

	/**
	 * The current color - could be any color between originColor and
	 * targetColor.
	 */
	private Color currentColor = Color.WHITE.cpy();

	/**
	 * The color the current transition was started from.
	 */
	private Color originColor = Color.WHITE.cpy();

	/**
	 * The time at which the current color transition started.
	 */
	private long colorTransitionStartTime = 0;

	/**
	 * The time at which the current color transition is supposed to end.
	 */
	private long colorTransitionEndTime = 0;

	/**
	 * The textures that are being used at certain levels of detail. The float
	 * specifies the zoom factor until which a texture should be used.
	 */
	private HashMap<Float, TextureE> levelOfDetailTextures = new HashMap<>();

	/**
	 * The texture to be used atm.
	 */
	private TextureE currentTexture;

	/**
	 * Forces a specific level of detail value that overrides the one that is
	 * derived from the current zoom level.
	 */
	private float forcedLOD = -1f;

	/**
	 * This constructor checks if the given texture has been loaded before and
	 * does so if that's not the case. A sprite is initialized accordingly.
	 *
	 * @param texture
	 * 		the texture to use
	 * @param parent
	 * 		The parent BioViz instance
	 */
	public DrawableSprite(final TextureE texture, final BioViz parent) {
		if (parent == null) {
			throw new RuntimeException("sprite parent must not be null");
		}


		if (textures == null) {
			textures = new TextureManager();
		}


		currentTexture = texture;
		this.addLOD(Float.MAX_VALUE, texture);
		this.targetColor.a = DEFAULT_ALPHA;
		this.currentColor.a = DEFAULT_ALPHA;
		this.viz = parent;
	}

	/**
	 * @brief Initializes the sprite with a given size and texture.
	 * @param sizeX The size of the sprite in x direction
	 * @param sizeY The size of the sprite in y direction
	 * @param region The texture that is being displayed
	 */
	private void initializeSprite(final float sizeX,
								  final float sizeY,
								  final TextureRegion region) {
		sprite = new Sprite(region);
		sprite.setSize(sizeX, sizeY);
		sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
		sprite.setPosition(-sprite.getWidth() / 2f, -sprite.getHeight() / 2f);
	}

	/**
	 * @param msg
	 * 		Message to be displayed
	 * @brief Displays a text above the sprite
	 */
	public void displayText(final String msg) {
		MessageCenter mc = viz.messageCenter;
		if (msg != null) {
			mc.addHUDMessage(this.hashCode(), msg, this.getX(), this.getY());
		} else {
			mc.removeHUDMessage(this.hashCode());
		}
	}

	@Override
	public void draw() {
		draw(this.z);
	}

	public void draw(final float z) {

		if (isVisible()) {

			if (sprite == null) {
				TextureRegion region = textures.getTexture(currentTexture);
				initializeSprite(1, 1, region);
			}

			// if LOD is set, enable LOD calculation and set
			// sprite accordingly
			if (this.levelOfDetailTextures.size() > 0) {
				float bestLODFactor = Float.MAX_VALUE;
				float targetLODFactor = this.scaleX;
				if (this.forcedLOD >= 0) {
					targetLODFactor = this.forcedLOD;
				}
				boolean foundLOD = false;
				for (final Float factor : levelOfDetailTextures.keySet()) {
					if (factor >= targetLODFactor && factor <= bestLODFactor) {
						bestLODFactor = factor;
						foundLOD = true;
					}
				}
				if (foundLOD) {
					currentTexture =
							levelOfDetailTextures.get(bestLODFactor);
				}

				this.setTexture();
			}

			update();

			this.sprite.setPosition(getX() - sprite.getWidth() / 2f,
									getY() - sprite.getHeight() / 2f);
			this.sprite.setScale(getScaleX(), getScaleY());
			this.sprite.setRotation(getRotation());
			this.sprite.setColor(currentColor);
			float[] v = this.sprite.getVertices();

			final int rgbMult = 255;

			if (cornerColors != null) {
				for (int i = 0; i < 4; i++) {
					int intBits =
							(int) (rgbMult * cornerColors[i].a) << 24 |
							(int) (rgbMult * cornerColors[i].b) << 16 |
							(int) (rgbMult * cornerColors[i].g) << 8 |
							(int) (rgbMult * cornerColors[i].r);
					switch (i) {
						case 0:
							v[SpriteBatch.C1] =
									NumberUtils.intToFloatColor(intBits);
							break;
						case 1:
							v[SpriteBatch.C2] =
									NumberUtils.intToFloatColor(intBits);
							break;
						case 2:
							v[SpriteBatch.C3] =
									NumberUtils.intToFloatColor(intBits);
							break;
						case 3:
							v[SpriteBatch.C4] =
									NumberUtils.intToFloatColor(intBits);
							break;
						default:
							break;
					}
				}
			}
			viz.batch.draw(this.sprite, this.viz.camera.combined, z);
		}
	}

	public void setDimensions(final float dimX, final float dimY) {
		this.setScaleX(dimX / this.sprite.getWidth());
		this.setScaleY(dimY / this.sprite.getHeight());
	}

	/**
	 * This sets the texture of the sprite object that is used to actually draw
	 * this DrawableSprite to the data that is supplied by (our) currentTexture
	 * and TextureManager data.</br>
	 * This therefore does not use any parameters.
	 * Instead, you should set the currentTexture field before drawing this
	 * instance.
	 */
	private void setTexture() {
		if (this.sprite != null) {
			this.sprite.setRegion(
					DrawableSprite.textures.getTexture(currentTexture));
		}
	}

	/**
	 * Adds a texture to the list of LOD textures.
	 * @param scaleFactorMax the scaling factor at which the respective
	 * texture should be shown.
	 * @param texture the texture to be added.
	 */
	public void addLOD(final float scaleFactorMax, final TextureE texture) {
		this.levelOfDetailTextures.put(scaleFactorMax, texture);
	}

	/**
	 * Removes a texture from this sprite's LOD list.
	 * Notice that this is currently not used but is simply added as the
	 * counterpart to to the respective add method.
	 * @param scaleFactorMax the scale factor at which the LOD texture is used.
	 */
	public void removeLOD(final float scaleFactorMax) {
		this.levelOfDetailTextures.remove(scaleFactorMax);
	}

	public boolean isHovered() {
		if (isVisible() && this.currentColor.a > 0) {
			int mouseX = Gdx.input.getX();
			int mouseY = Gdx.input.getY();
			int resX = Gdx.graphics.getWidth();
			int resY = Gdx.graphics.getHeight();

			Rectangle viewport = viz.currentAssay.getViewBounds();

			float viewMouseX =
					((float) mouseX / (float) resX) * viewport.width +
					viewport.x;
			float viewMouseY =
					-(((float) mouseY / (float) resY) * viewport.height +
					  viewport.y);

			float xCoord = viz.currentAssay.xCoordInCells(this.getX());
			float yCoord = viz.currentAssay.yCoordInCells(this.getY());

			boolean aboveX = viewMouseX > xCoord - COORDINATE_SHIFT;
			boolean belowX = viewMouseX < xCoord + COORDINATE_SHIFT;
			boolean aboveY = viewMouseY > yCoord - COORDINATE_SHIFT;
			boolean belowY = viewMouseY < yCoord + COORDINATE_SHIFT;

			if (aboveX && belowX && aboveY && belowY) {
				return true;
			}
		}
		return false;
	}

	protected void update() {
		float totalProgress = 1;
		if (colorTransitionEndTime > colorTransitionStartTime) {
			float transitionProgress = Math.max(0, Math.min(1, (float) (
					new Date().getTime() - colorTransitionStartTime) / (float) (
					colorTransitionEndTime - colorTransitionStartTime)));
			totalProgress =
					(float) -Math.pow(transitionProgress - 1, 4) + 1;
		}

		currentColor = originColor.cpy().mul(1 - totalProgress).add(
				targetColor.cpy().mul(totalProgress));
	}

	/**
	 * Returns a copy of the sprite's target color.
	 *
	 * @return Copy of the targetColor variable.
	 */
	public Color getColor() {
		return targetColor.cpy();
	}

	/**
	 * Sets the sprite's color.
	 *
	 * @param color
	 * 		The color this sprite is going to have
	 */
	public void setColor(final Color color) {
		if (!this.targetColor.equals(color)) {
			originColor = this.currentColor;
			this.targetColor = color;
			Date d = new Date();
			this.colorTransitionStartTime = d.getTime();
			this.colorTransitionEndTime = d.getTime() +
										  getColorTransitionDuration();
		}
	}

	/**
	 * Sets the color of this sprite without fading towards it.
	 *
	 * @param color
	 * 		the color this sprite should assume immediately
	 */
	public void setColorImmediately(final Color color) {
		this.originColor = color;
		this.targetColor = color;
		Date d = new Date();
		this.colorTransitionStartTime = d.getTime();
		this.colorTransitionEndTime = d.getTime() +
									  getColorTransitionDuration();
	}

	/**
	 * @brief Returns the duration of the color transition.
	 * @return The duration the transition between colors takes.
	 */
	public static int getColorTransitionDuration() {
		return colorTransitionDuration;
	}

	/**
	 * @brief Sets the color transition time.
	 * @param colorTransitionDuration The new transition time for colors.
	 */
	public static void setColorTransitionDuration(
			final int colorTransitionDuration) {
		DrawableSprite.colorTransitionDuration = colorTransitionDuration;
	}

	public float getX() {
		return x;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(final float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(final float z) {
		this.z = z;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(final float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(final float scaleY) {
		this.scaleY = scaleY;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(final float rotation) {
		this.rotation = rotation;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(final boolean visibility) {
		this.isVisible = visibility;
	}

	protected float getForcedLOD() {
		return forcedLOD;
	}

	protected void setForcedLOD(final float forcedLOD) {
		this.forcedLOD = forcedLOD;
	}

	protected void disableForcedLOD() {
		this.forcedLOD = -1f;
	}
}
