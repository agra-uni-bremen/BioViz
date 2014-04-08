package de.dfki.revvisgdx;

import com.badlogic.gdx.graphics.Color;

public class Button extends DrawableSprite {

	public Button(String textureFilename) {
		super(textureFilename);
		this.color = Color.WHITE.cpy().mul(1, 1, 1, 0.75f);
	}
	
	private boolean isMouseOver(int x, int y) {
		if (Math.abs(this.x - x) < 32) {
			if (Math.abs(this.y - y) < 32) {
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
		this.color.a = 1;
	}
	
	public void UnHovered() {
		this.color.a = 0.75f;
	}
}