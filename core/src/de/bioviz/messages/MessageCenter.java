package de.bioviz.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Matrix4;

import de.bioviz.ui.BioViz;

import java.util.HashMap;
import java.util.Vector;

/**
 * This class provides some methods to draw text.
 * 
 * @author jannis
 *
 */
public class MessageCenter  {


	private Vector<Message> messages;
	private BitmapFont font;
	public boolean hidden = false;

	public static final int MAX_MESSAGES_IN_UI	= 5;

	public BitmapFont getFont() {
		if (font == null)
			font = new BitmapFont();
		return font;
	}
	
	private class HUDMessage {
		public String message;
		public float x;
		public float y;
		public Color color;
		
		public HUDMessage(String message, float x, float y) {
			this.message = message;
			this.x = x;
			this.y = y;
			this.color = Color.WHITE;
		}
	}
	public HashMap<Integer, HUDMessage> HUDMessages = new HashMap<Integer, HUDMessage>();

	public MessageCenter() {
		messages = new Vector<Message>();
	}

	public boolean isHidden() {
		return hidden;
	}


	/**
	 * Add a message that is shown for some time and then disappears.
	 * 
	 * @param message the message to be displayed
	 */
	public void addMessage(String message) {
		addMessage(message,false);
	}
	
	private void addMessage(String message,boolean recursion) {
			Message m = new Message(message);

			// Meh. libgdx doesn't draw line breaks... 
			if (message.contains("\n")) {
				String[] lines = message.split("\n");
				for (String line : lines) {
					addMessage(line, true);
				}
			} else {
				this.messages.add(m);
			}

			if (messages.size() > MAX_MESSAGES_IN_UI) {
				messages.remove(0);
			}
	}

	public void render() {
		if (!hidden) {
			if (font == null) {
				font = new BitmapFont();
			}

			Matrix4 normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
			BioViz.singleton.batch.setProjectionMatrix(normalProjection);

			int spacing = 18;
			int yCoord = Gdx.graphics.getHeight() - spacing;
			for (Message m : this.messages) {
				if (m.color != null)
					font.setColor(m.color);
				else
					font.setColor(Color.WHITE);
				int start_x = spacing;
				int start_y = yCoord;
				font.draw(BioViz.singleton.batch, m.message, start_x, start_y); // TODO name of closestHit



				yCoord -= spacing;
			}

			for (HUDMessage s: this.HUDMessages.values()) {
				font.setColor(s.color);
				TextBounds tb = font.getBounds(s.message);
				int x = (int) ((s.x - tb.width / 2f) + Gdx.graphics.getWidth() / 2);
				int y = (int) ((s.y + tb.height / 2f) + Gdx.graphics.getHeight() / 2);
				font.draw(BioViz.singleton.batch, s.message, x, y);
			}

			long curTime = System.currentTimeMillis();
			while(this.messages.size() > 0 && this.messages.get(0).displayTime + this.messages.get(0).createdOn < curTime) {
				this.messages.remove(0);
			}
		}
	}

	/**
	 * Use this to draw text somewhere on the screen.
	 * @param key a unique identifier; subsequent calls with the same key will erase
	 * the previous message, so you can move the text around
	 * @param message the message to display
	 * @param x the x coordinate to show the message at
	 * @param y the y coordinate to show the message at
	 */
	public void addHUDMessage(int key, String message, float x, float y) {
		addHUDMessage(key, message, x, y, null);
	}
	
	/**
	 * Use this to draw text somewhere on the screen.
	 * @param key a unique identifier; subsequent calls with the same key will erase
	 * the previous message, so you can move the text around
	 * @param message the message to display
	 * @param x the x coordinate to show the message at
	 * @param y the y coordinate to show the message at
	 * @param col the color of the message
	 */
	public void addHUDMessage(int key, String message, float x, float y, Color col) {
		HUDMessage hm ;
		if (!this.HUDMessages.containsKey(key)) {
			hm = new HUDMessage(message, x, y);
			HUDMessages.put(key, hm);
		} else {
			hm = HUDMessages.get(key);
			hm.message = message;
			hm.x = x;
			hm.y = y;
		}
		if(col != null) {
			hm.color = col;
		}
	}
	
	public void removeHUDMessage(int key) {
		if (this.HUDMessages.containsKey(key)) {
			this.HUDMessages.remove(key);
		}
	}
}
