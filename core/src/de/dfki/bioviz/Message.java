package de.dfki.bioviz;

import com.badlogic.gdx.graphics.Color;

public class Message {
	public String message;
	public long displayTime = 100000;
	public long createdOn;
	public Color color;
	
	public Message() {
		this.createdOn = System.currentTimeMillis();
	}
}
