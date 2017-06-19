package de.bioviz.ui;

/**
 * This class implements an arrowhead that can be used e.g. for DrawableLines.
 *
 * @author Maximilian Luenert
 */
public class DrawableArrowHead extends DrawableSprite {

	/**
	 * This constructor checks if the given texture has been loaded before and
	 * does so if that's not the case. A sprite is initialized accordingly.
	 *
	 * @param parent
	 * 					the parent BioViz
	 */
	public DrawableArrowHead(final BioViz parent) {
		super(TextureE.ArrowHead, parent);
	}

	/**
	 * Draws the arrowHead on the biochip.
	 */
	public void draw() {
		super.draw();
	}
}
