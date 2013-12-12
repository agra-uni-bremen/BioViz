package de.dfki.revvisgdx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class MessageCenter {
	private Vector<Message> messages;
	BitmapFont font;
	private int spacing = 18;
	
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
	
	public void addMessage(String message) {
		Message m = new Message();
		m.message = message;
		
		// Meh. libgdx doesn't draw line breaks... 
		if (message.contains("\n")) {
			String[] lines = message.split("\n");
			for (int i = 0; i < lines.length; i++) {
				addMessage(lines[i]);
			}
		} else {
			this.messages.add(m);
		}
		
		if (messages.size() > 20)
			messages.remove(0);
	}

	public void render() {
//		SystemViewMain.Singleton.myBatch.begin();
		if (font == null)
			font = new BitmapFont();
		
		Matrix4 normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		RevVisGDX.singleton.batch.setProjectionMatrix(normalProjection);
		
		int yCoord = Gdx.graphics.getHeight() - spacing;
		for (Message m : this.messages) {
			if (m.color != null)
				font.setColor(m.color);
			int start_x = spacing;
			int start_y = yCoord;
			font.draw(RevVisGDX.singleton.batch, m.message, start_x, start_y); // TODO name of closestHit
			//SystemViewMain.Singleton.myBatch.disableBlending();

			

			yCoord -= spacing;
		}
		
		for (HUDMessage s: this.HUDMessages.values()) {
			font.setColor(1f,1f, 1f, 1.0f);
			int x = (int) s.x;
			int y = (int) s.y;
			font.draw(RevVisGDX.singleton.batch, s.message, x, y);
		}

		long curTime = System.currentTimeMillis();
		while(this.messages.size() > 0 && this.messages.get(0).displayTime + this.messages.get(0).createdOn < curTime) {
			this.messages.remove(0);
		}
//		SystemViewMain.Singleton.myBatch.end();
	}
	
	public void addHUDMessage(int key, String message, float x, float y) {
		addHUDMessage(key, message, x, y, null);
	}
	
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
