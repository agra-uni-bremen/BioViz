package de.dfki.revvisgdx;

public class Message {
	public String message;
	public long displayTime = 100000;
	public long createdOn;
	
	public Message() {
		this.createdOn = System.currentTimeMillis();
	}
}
