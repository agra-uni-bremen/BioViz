package de.bioviz.ui;

/**
 * All things that can get drawn should implement a draw() method. That's
 * what this interface is for.
 * 
 * @author jannis
 *
 */
public interface Drawable {
	public void draw();
	public String generateSVG();
}
