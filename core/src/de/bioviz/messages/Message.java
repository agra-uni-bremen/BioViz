package de.bioviz.messages;

import com.badlogic.gdx.graphics.Color;

public class Message {
	public String message;
	public long displayTime = 5000;
	public long createdOn;
	public Color color;
	
	public Message(String msg) {
		this.createdOn = System.currentTimeMillis();
		this.message=msg;
	}
}
