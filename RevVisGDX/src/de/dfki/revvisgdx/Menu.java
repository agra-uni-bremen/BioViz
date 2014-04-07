package de.dfki.revvisgdx;

import java.util.Vector;

import com.badlogic.gdx.Gdx;

import de.dfki.revvisgdx.buttons.variableButton;

public class Menu implements Drawable {
	variableButton varButton;
	private Vector<Button> buttons = new Vector<Button>();
	private float buttonOffsetX = 0;
	
	public Menu() {
		varButton = new variableButton();
		buttons.add(varButton);
		
	}
	
	public void MouseCoords(int x, int y) {
		float mouseAreaRight = 256;
		float mouseAreaLeft = 128f;
		float percentageX ;
		if (x < mouseAreaRight)
			if (x > mouseAreaLeft)
				percentageX = (x - mouseAreaLeft) / (mouseAreaRight - mouseAreaLeft);
			else
				percentageX = 0;
		else
			percentageX = 1;
		
		float buttonX = x - Gdx.graphics.getWidth() / 2f;
		float buttonY = y - Gdx.graphics.getHeight() / 2f;
		
		for (Button b : buttons) {
			
			b.IsHovered((int)buttonX, (int)buttonY);
		}
		
		this.buttonOffsetX = 64 - 64 * percentageX;
	}
	
	public boolean click(int x, int y) {
		x -= Gdx.graphics.getWidth() / 2f;
		y -= Gdx.graphics.getHeight() / 2f;
		System.out.println("Clicked at " + x + "/" + y);
		for (Button b : this.buttons) {
			if (b.IsClicked(x, y))
				return true;
		}
		return false;
	}
	
	@Override
	public void draw() {
		varButton.x = Gdx.graphics.getWidth() / -2f + buttonOffsetX;
		
		for (Button b : this.buttons) {
			b.draw();
		}
	}

}
