package de.bioviz.messages;

import com.badlogic.gdx.graphics.Color;

public class HUDMessage {
	public String message;
	public float x;
	public float y;
	public Color color;
	public float size;
	public boolean hideWhenZoomedOut = true;

	public HUDMessage(String message, float x, float y) {
		this.message = message;
		this.x = x;
		this.y = y;
		this.color = Color.WHITE;
		this.size = -1f;
	}
}
