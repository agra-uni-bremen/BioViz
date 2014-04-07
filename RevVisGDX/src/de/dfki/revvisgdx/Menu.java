package de.dfki.revvisgdx;

import java.util.Vector;

import com.badlogic.gdx.Gdx;

import de.dfki.revvisgdx.buttons.FunctionButton;
import de.dfki.revvisgdx.buttons.MovingRuleHighlightButton;
import de.dfki.revvisgdx.buttons.UsageColorButton;
import de.dfki.revvisgdx.buttons.variableButton;

public class Menu implements Drawable {
//	variableButton varButton;
	private Vector<Button> buttons = new Vector<Button>();
	private float buttonOffsetX = 0;
	
	public Menu() {
		try {
			buttons.add(new FunctionButton("data/Preset1.png", Presets.class.getMethod("setConstGarbage")));
			buttons.add(new FunctionButton("data/Preset2.png", Presets.class.getMethod("setBoxesAndUsage")));
			buttons.add(new FunctionButton("data/Preset3.png", Presets.class.getMethod("setColourizedUsage")));
			buttons.add(new FunctionButton("data/Preset4.png", Presets.class.getMethod("setGreyNeighboursWithBlackTargets")));
			buttons.add(new FunctionButton("data/Preset5.png", Presets.class.getMethod("setColourizeLineType")));
			buttons.add(new FunctionButton("data/Preset6.png", Presets.class.getMethod("setMovingRuleBoxOverlay")));
			buttons.add(new FunctionButton("data/Preset7.png", Presets.class.getMethod("setMovingRuleColoured")));
			buttons.add(new FunctionButton("data/Preset8.png", Presets.class.getMethod("setColourizeUsageAbsolute")));
			buttons.add(new FunctionButton("data/Preset9.png", Presets.class.getMethod("setMovingRuleColouredAbsolute")));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buttons.add(new variableButton());
		buttons.add(new UsageColorButton());
		buttons.add(new MovingRuleHighlightButton());
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
