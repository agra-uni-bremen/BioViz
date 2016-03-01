package de.bioviz.messages;

import com.badlogic.gdx.graphics.Color;

public class HUDMessage extends MessageBase {
	public float x;
	public float y;
	public float size;
	public boolean hideWhenZoomedOut = true;

	public HUDMessage(String message, float x, float y) {
		super(message);
		this.x = x;
		this.y = y;
		this.size = -1f;
	}
}
