package de.bioviz.ui;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by max on 6/18/17.
 */
public class DrawableArrowHead extends DrawableSprite{

	/**
	 * This constructor checks if the given texture has been loaded before and
	 * does so if that's not the case. A sprite is initialized accordingly.
	 *
	 * @param texture the texture to use
	 * @param parent
	 */
	public DrawableArrowHead(final BioViz parent) {
		super(TextureE.ArrowHead, parent);
	}

	public void draw(){
		super.draw();
	}
}
