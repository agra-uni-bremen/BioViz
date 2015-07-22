package de.dfki.bioviz;

import java.sql.Date;
import java.text.DateFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;

/**
 * This class provides some methods to draw text.
 * 
 * @author jannis
 *
 */
public class MessageCenter {
	private Vector<Message> messages;
	private BitmapFont font;
	public boolean hidden = false;
	
	public static final int SEVERITY_DEBUG 		= 0b00001;
	public static final int SEVERITY_INFO 		= 0b00010;
	public static final int SEVERITY_WARNING 	= 0b00100;
	public static final int SEVERITY_ERROR 		= 0b01000;
	
	final static int showInUI = SEVERITY_INFO | SEVERITY_WARNING | SEVERITY_ERROR;
	final static int showInConsole = SEVERITY_DEBUG | SEVERITY_INFO | SEVERITY_WARNING | SEVERITY_ERROR;
	
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
	
	/**
	 * Add a message that is shown for some time and then disappears.
	 * 
	 * @param message the message to be displayed
	 */
	public void addMessage(String message, int severity) {
		addMessage(message, severity, false);
	}
	
	private void addMessage(String message, int severity, boolean recursion) {
		if ((showInUI & severity) > 0) {
			Message m = new Message();
			m.message = getSeverityName(severity) + ": " + message;

			// Meh. libgdx doesn't draw line breaks... 
			if (message.contains("\n")) {
				String[] lines = message.split("\n");
				for (String line : lines) {
					addMessage(line, severity);
				}
			} else {
				this.messages.add(m);
			}

			if (messages.size() > 50)
				messages.remove(0);
		}
		if (!recursion && ((showInConsole & severity) > 0)) {
			System.out.println("[" + new java.util.Date().getTime() + "] [" + getSeverityName(severity) + "] " + message);
		}
	}
	
	public static String getSeverityName(int severity) {
		String result = "";
		if ((severity & SEVERITY_DEBUG) > 0)
			result += "Debug";
		if ((severity & SEVERITY_INFO) > 0)
			result += "Info";
		if ((severity & SEVERITY_WARNING) > 0)
			result += "Warning";
		if ((severity & SEVERITY_ERROR) > 0)
			result += "Error";
		return result;
	}

	public void render() {
		if (!hidden) {
			if (font == null)
				font = new BitmapFont();

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
				font.draw(BioViz.singleton.batch, new Date(m.createdOn).toString() + ": " + m.message, start_x, start_y); // TODO name of closestHit



				yCoord -= spacing;
			}

			for (HUDMessage s: this.HUDMessages.values()) {
				font.setColor(s.color);
				int x = (int) s.x;
				int y = (int) s.y;
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
		if(col != null)
			hm.color = col;
	}
	
	public void removeHUDMessage(int key) {
		if (this.HUDMessages.containsKey(key)) {
			this.HUDMessages.remove(key);
		}
	}
}
