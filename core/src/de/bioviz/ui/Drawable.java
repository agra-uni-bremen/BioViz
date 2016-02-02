package de.bioviz.ui;

/**
 * All things that can get drawn should implement a draw() method. That's
 * what this interface is for.
 *
 * @author jannis
 *
 */
public interface Drawable {

	/**
	 * The draw method triggers this Drawable's drawing process, i.e. it
	 * gets drawn onto the screen.
	 */
	void draw();
}
