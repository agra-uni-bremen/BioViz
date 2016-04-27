package de.bioviz.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
		.FreeTypeFontParameter;
import com.badlogic.gdx.math.Matrix4;
import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Vector;

/**
 * This class provides some methods to draw text.
 *
 * @author Jannis Stoppe, Oliver Keszocze
 */
public class MessageCenter {


	private Vector<Message> messages;

	/**
	 * The font that is used to display infos on top of fields and droplets.
	 */
	private BitmapFont font;


	/**
	 * The font that is used to display logging messages.
	 */
	private BitmapFont messageFont;

	/**
	 * Whether messages are to be displayed.
	 */
	public boolean hidden = false;


	/**
	 * The maximal amount of messages that are displayed at once.
	 */
	public static final int MAX_MESSAGES_IN_UI = 32;

	private final float SCALEINCSTEP = 2f;

	private float textRenderResolution = 16;
	private float textRenderResolutionMinimum = 4f;
	public float getTextRenderResolution() {return textRenderResolution;}
	public void setTextRenderResolution(float value) {
		this.textRenderResolution = Math.max(
				textRenderResolutionMinimum,
				value);
		fontInvalidated = true;
		logger.debug("setting HUD font size to " +
				this.textRenderResolution);
	}

	private float msgTextRenderResolution = 8f;
	private float msgTextRenderResolutionMinimum = 4f;
	public float getmsgTextRenderResolution() {return msgTextRenderResolution;}
	public void setmsgTextRenderResolution(float value) {
		this.msgTextRenderResolution = Math.max(
				msgTextRenderResolutionMinimum,
				value);
		fontInvalidated = true;
		logger.debug("setting message font size to " +
				this.msgTextRenderResolution);
	}

	private HashMap<Integer, HUDMessage> HUDMessages =
			new HashMap<Integer, HUDMessage>();

	private boolean fontInvalidated = true;


	/**
	 * The central logging device for this class.
	 */
	static Logger logger = LoggerFactory.getLogger(MessageCenter.class);

	BioViz parent;


	/**
	 * Creates a new message center that will pass messages to a BioViz
	 * instance.
	 *
	 * @param parent
	 * 		the {@link BioViz} this MessageCenter is attached to.
	 */
	public MessageCenter(final BioViz parent) {
		this.parent = parent;
		messages = new Vector<Message>();
	}

	/**
	 * Retrives the font that is used to display stuff on top of
	 * droplets/fields.
	 *
	 * @return The font that is used to display stuff on top of droplets/fields
	 */
	public BitmapFont getFont() {
		if (fontInvalidated) {
			fontInvalidated = false;
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
					Gdx.files.internal("images/FreeUniversal-Regular.ttf"));
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = (int)textRenderResolution;
			parameter.color = Color.WHITE.cpy();
			parameter.borderWidth = 2;
			parameter.borderColor = Color.BLACK.cpy();
			parameter.genMipMaps = true;
			BitmapFont font12 =
					generator.generateFont(parameter); // font size 12 pixels
			generator.dispose(); // don't forget to dispose to avoid memory
			// leaks!

			generator = new FreeTypeFontGenerator(
					Gdx.files.internal("images/Anonymous_Pro.ttf"));
			parameter = new FreeTypeFontParameter();
			parameter.size = (int)msgTextRenderResolution;
			parameter.color = Color.BLACK.cpy();
			this.messageFont = generator.generateFont(parameter);
			generator.dispose();
			logger.debug("set up font");

			font = font12;//new BitmapFont();
		}
		return font;
	}





	/**
	 * Add a message that is shown for some time and then disappears.
	 *
	 * @param message
	 * 		the message to be displayed
	 */
	public void addMessage(final String message) {
		Message m = new Message(message);

		// Meh. libgdx doesn't draw line breaks...
		if (message.contains("\n")) {
			String[] lines = message.split("\n");
			for (String line : lines) {
				addMessage(line);
			}
		}
		else {
			this.messages.add(m);
		}

		if (messages.size() > MAX_MESSAGES_IN_UI) {
			messages.remove(0);
		}
	}

	public void render() {
		if (!hidden) {
			if (font == null || fontInvalidated) {
				getFont();
			}

			Matrix4 normalProjection =
					new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),
											   Gdx.graphics.getHeight());
			parent.batch.setProjectionMatrix(normalProjection);

			int spacing = 2 + (int)getmsgTextRenderResolution();
			int yCoord = Gdx.graphics.getHeight() - spacing;
			for (Message m : this.messages) {
				if (m.color != null) {
					messageFont.setColor(m.color);
				}
				else {
					messageFont.setColor(Color.WHITE);
				}
				int startX = spacing;
				int startY = yCoord;
				messageFont.draw(parent.batch, m.message, startX,
								 startY);
				yCoord -= spacing;
			}

			for (HUDMessage s : this.HUDMessages.values()) {
				Color targetColor = s.color.cpy();

				float hideAt = textRenderResolution;
				float showAt = (textRenderResolution * 2);
				if (s.hideWhenZoomedOut) {
					// Hide when zoomed out
					if (this.parent.currentCircuit.getScaleX() < hideAt) {
						targetColor.a = 0;
					}
					else if (this.parent.currentCircuit.getScaleX() < showAt) {
						float val = this.parent.currentCircuit.getScaleX();
						val = (val - hideAt) / (showAt - hideAt);
						targetColor.a = val;
					}
					else {
						targetColor.a = 1;
					}
				}

				font.setColor(targetColor);

				final GlyphLayout layout = new GlyphLayout(font, s.message);
				// or for non final texts: layout.setText(font, text);

				final float fontX = s.x -
									layout.width / 2f +
									Gdx.graphics.getWidth() / 2f;
				final float fontY = s.y +
									layout.height / 2f +
									Gdx.graphics.getHeight() / 2f;

				font.draw(parent.batch, layout, fontX, fontY);
			}

			while (this.messages.size() > 0 &&
				   this.messages.get(0).expired()) {
				this.messages.remove(0);
			}
		}
	}

	/**
	 * Use this to draw text somewhere on the screen.
	 *
	 * @param key
	 * 		a unique identifier; subsequent calls with the same key will erase
	 * 		the
	 * 		previous message, so you can move the text around
	 * @param message
	 * 		the message to display
	 * @param x
	 * 		the x coordinate to show the message at
	 * @param y
	 * 		the y coordinate to show the message at
	 */
	public void addHUDMessage(final int key, final String message, final float
			x, final float y) {
		addHUDMessage(key, message, x, y, null, -1f);
	}

	/**
	 * Use this to draw text somewhere on the screen.
	 *
	 * @param key
	 * 		a unique identifier; subsequent calls with the same key will erase
	 * 		the
	 * 		previous message, so you can move the text around
	 * @param message
	 * 		the message to display
	 * @param x
	 * 		the x coordinate to show the message at
	 * @param y
	 * 		the y coordinate to show the message at
	 * @param col
	 * 		the color of the message
	 */
	public void addHUDMessage(final int key, final String message, final float
			x, final float y, final Color col, final float size) {
		HUDMessage hm;
		if (!this.HUDMessages.containsKey(key)) {
			hm = new HUDMessage(message, x, y);
			HUDMessages.put(key, hm);
		}
		else {
			hm = HUDMessages.get(key);
			hm.message = message;
			hm.x = x;
			hm.y = y;
		}
		if (col != null) {
			hm.color = col;
		}
		hm.size = size;
	}

	/**
	 *  Removes a message from the HUD message queue.
	 * @param key ID of the message to be removed.
	 */
	public void removeHUDMessage(final int key) {
		if (this.HUDMessages.containsKey(key)) {
			this.HUDMessages.remove(key);
		}
	}

	/**
	 * Clears all HUD messages.
	 */
	public void clearHUDMessages() {
		this.HUDMessages.clear();
	}

	public void resetScales() {
		resetMsgScale();
		resetHUDScale();
	}

	public void resetMsgScale() {
		setmsgTextRenderResolution(8f);
	}

	public void resetHUDScale() {
		setTextRenderResolution(16f);
	}

	public void incScales() {
		incScaleHUD();
		incScaleMsg();
	}

	public void incScaleHUD() {
		setTextRenderResolution(getTextRenderResolution() + SCALEINCSTEP);
	}

	public void incScaleMsg() {
		setmsgTextRenderResolution(getmsgTextRenderResolution() + SCALEINCSTEP);
	}

	public void decScales() {
		decScaleHUD();
		decScaleMsg();
	}

	public void decScaleHUD() {
		setTextRenderResolution(getTextRenderResolution() - SCALEINCSTEP);
	}

	public void decScaleMsg() {
		setmsgTextRenderResolution(getmsgTextRenderResolution() - SCALEINCSTEP);
	}

	public void setScales(final float scale) {
		setScaleHUD(scale);
		setScaleMsg(scale);
	}

	public void setScaleHUD(final float scaleHUD) {
		setTextRenderResolution(scaleHUD);
	}

	public void setScaleMsg(final float scaleMsg) {
		setmsgTextRenderResolution(scaleMsg);
	}
}
