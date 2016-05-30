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
	 * The default amount of time required to change from one color to the next.
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
	 * The default alpha (i.e. visibility) value. Set to 0 because sprites
	 * are supposed to "fade in".
	 */
	private static final float DEFAULT_ALPHA = 0f;

	/**
	 * Used to store all textures.
	 */
	private static TextureManager textures;

	/**
	 * Link to the visualization this sprite is used in.
	 */
	BioViz viz;

	/**
	 * The colors that are set to the four corners of this sprite.
	 * This may be set to null if no specific per-corner colors are supposed to
	 * be set. Any color that is equal to Color.BLACK will be ignored and the
	 * currentColor will be used instead.
	 */
	protected Color cornerColors[] = null;

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
	 * The link back to libgdx: the sprite that is used to draw this
	 * {@link DrawableSprite}.
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
	 * The textures that are being used at certain levels of detail.
	 * The float specifies the zoom factor until which a texture should be used.
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
     * @param texture the texture to use
     */
    public DrawableSprite(final TextureE texture,
                          final float sizeX,
                          final float sizeY, BioViz
            parent) {
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

    private void initializeSprite(final float sizeX,
                                  final float sizeY,
                                  final TextureRegion region) {
        sprite = new Sprite(region);
        sprite.setSize(sizeX, sizeY);
        sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
        sprite.setPosition(-sprite.getWidth() / 2f, -sprite.getHeight() / 2f);
    }

    public DrawableSprite(final TextureE texture, final BioViz parent) {
        this(texture, 1, 1, parent);
    }

    /**
     * @param msg Message to be displayed
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
				for (Float factor : levelOfDetailTextures.keySet()) {
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

			if (cornerColors != null) {
				for(int i = 0; i < 4; i++) {
					int intBits =
						(int)(255 * cornerColors[i].a) << 24 |
						(int)(255 * cornerColors[i].b) << 16 |
						(int)(255 * cornerColors[i].g) << 8 |
						(int)(255 * cornerColors[i].r);
					switch (i) {
					case 0:
						v[SpriteBatch.C1] = NumberUtils.intToFloatColor(intBits);
						break;
					case 1:
						v[SpriteBatch.C2] = NumberUtils.intToFloatColor(intBits);
						break;
					case 2:
						v[SpriteBatch.C3] = NumberUtils.intToFloatColor(intBits);
						break;
					case 3:
						v[SpriteBatch.C4] = NumberUtils.intToFloatColor(intBits);
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

	// TODO what is the rationale of this method?
	private void setTexture() {
		if (this.sprite != null) {
			this.sprite.setRegion(this.textures.getTexture(currentTexture));
		}
	}

	// TODO check whether this is still needed
	public void addLOD(final float scaleFactorMax, final TextureE texture) {
		this.levelOfDetailTextures.put(scaleFactorMax, texture);
	}

	public void removeLOD(final float scaleFactorMax) {
		this.levelOfDetailTextures.remove(scaleFactorMax);
	}

	public boolean isHovered() {
		if (isVisible() && this.currentColor.a > 0) {
			int mouseX = Gdx.input.getX();
			int mouseY = Gdx.input.getY();
			int resX = Gdx.graphics.getWidth();
			int resY = Gdx.graphics.getHeight();

			Rectangle viewport = viz.currentCircuit.getViewBounds();

			float viewMouseX =
					(((float) mouseX / (float) resX) * viewport.width +
					 viewport.x);
			float viewMouseY =
					-(((float) mouseY / (float) resY) * viewport.height +
					  viewport.y);

			float xCoord = viz.currentCircuit.xCoordInCells(this.getX());
			float yCoord = viz.currentCircuit.yCoordInCells(this.getY());

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
		float transitionProgress = Math.max(0, Math.min(1, (float) (
				new Date().getTime() - colorTransitionStartTime) / (float) (
				colorTransitionEndTime - colorTransitionStartTime)));
		float totalProgress =
				(float) -(Math.pow((transitionProgress - 1), 4)) + 1;

		currentColor = this.originColor.cpy().mul(1 - totalProgress).add(
				this.targetColor.cpy().mul(totalProgress));
	}

	/**
	 * Returns a copy of the sprite's target color.
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
		this.colorTransitionEndTime = d.getTime() + getColorTransitionDuration();
	}

	public static int getColorTransitionDuration() {
		return colorTransitionDuration;
	}

	public static void setColorTransitionDuration(int colorTransitionDuration) {
		DrawableSprite.colorTransitionDuration = colorTransitionDuration;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	protected float getForcedLOD() {
		return forcedLOD;
	}

	protected void setForcedLOD(float forcedLOD) {
		this.forcedLOD = forcedLOD;
	}
	
	protected void disableForcedLOD() {
		this.forcedLOD = -1f;
	}
}
