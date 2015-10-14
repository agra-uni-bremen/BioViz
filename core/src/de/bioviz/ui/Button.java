package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

/**
 * Base class for the menu buttons you see on the bottom of the screen.
 * 
 * @author jannis
 *
 */
public class Button extends DrawableSprite {

	public Button(String textureFilename, BioViz parent) {
		super(textureFilename, parent);
		this.setColor(Color.WHITE.cpy().mul(1, 1, 1, 0.75f));
	}
	
	private boolean isMouseOver(int x, int y) {
		if (Math.abs(this.x - x) < this.sprite.getWidth() / 2f) {
			if (Math.abs(this.y - y) < this.sprite.getHeight() / 2f) {
				return true;
			}
		}
		return false;
	}
	
	public boolean IsClicked(int x, int y) {
		
		if (isMouseOver(x, y)) {
			Clicked();
			return true;
		}
		
		return false;
	}
	
	private boolean oldHoverState = false;
	public boolean IsHovered(int x, int y) {
		if (isMouseOver(x, y)) {
			if (!oldHoverState) {
				Hovered();
				oldHoverState = true;
			}
			return true;
		}
		if (oldHoverState) {
			UnHovered();
			oldHoverState = false;
		}
		return false;
	}

	public void Clicked() {
		
	}
	
	public void Hovered() {
		Color c = this.getColor();
		c.a = 1;
		this.setColor(c);
	}
	
	public void UnHovered() {
		Color c = this.getColor();
		c.a = 0.75f;
		this.setColor(c);
	}

	@Override
	public String generateSVG() {
		// TODO Auto-generated method stub
		return "";
	}
}
