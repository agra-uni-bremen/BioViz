package de.dfki.revvisgdx;

import java.util.Vector;

import com.badlogic.gdx.Gdx;

import de.dfki.revvisgdx.buttons.UsageColorButton;
import de.dfki.revvisgdx.buttons.variableButton;

public class Menu implements Drawable {
//	variableButton varButton;
	private Vector<Button> buttons = new Vector<Button>();
	private float buttonOffsetX = 0;
	
	public Menu() {
		buttons.add(new variableButton());
		buttons.add(new UsageColorButton());
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
		float buttonY = -y + Gdx.graphics.getHeight() / 2f;
		
		for (Button b : buttons) {
			
			b.IsHovered((int)buttonX, (int)buttonY);
		}
		
		this.buttonOffsetX = 32 - 64 * percentageX;
	}
	
	public boolean click(int x, int y) {
		float xButton = x - Gdx.graphics.getWidth() / 2f;
		float yButton = -y + Gdx.graphics.getHeight() / 2f;
		System.out.println("Clicked at " + xButton + "/" + yButton);
		for (Button b : this.buttons) {
			if (b.IsClicked((int)xButton, (int)yButton))
				return true;
		}
		return false;
	}
	
	@Override
	public void draw() {
		int i = 1;
		for (Button b : this.buttons) {
			b.draw();
			b.y = (Gdx.graphics.getHeight() / (buttons.size() + 1)) * i - Gdx.graphics.getHeight() / 2f;
			b.x = Gdx.graphics.getWidth() / -2f + buttonOffsetX;
			i++;
		}
	}

}
