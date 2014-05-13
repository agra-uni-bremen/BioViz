package de.dfki.revvisgdx;

import java.util.Vector;

import com.badlogic.gdx.Gdx;

import de.dfki.revvisgdx.buttons.FunctionButton;
import de.dfki.revvisgdx.buttons.MovingRuleHighlightButton;
import de.dfki.revvisgdx.buttons.ToggleButton;
import de.dfki.revvisgdx.buttons.UsageColorButton;
import de.dfki.revvisgdx.buttons.variableButton;

public class Menu implements Drawable {
//	variableButton varButton;
	private Vector<Button> buttons = new Vector<Button>();
	private float buttonOffsetY = 64;
	private float lastMouseX = 0f;
	private float buttonShiftPercentage = 0f;
	
	public Menu() {
		try {
			buttons.add(new FunctionButton("data/Preset0.png", Presets.class.getMethod("setDefault")));
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
	}
	
	public void MouseCoords(int x, int y) {
		float mouseAreaMin = Gdx.graphics.getHeight() - 128f;
		float mouseAreaMax = Gdx.graphics.getHeight() - 64f;
		float percentageY;
		if (y < mouseAreaMax)
			if (y > mouseAreaMin)
				percentageY = (y - mouseAreaMin) / (mouseAreaMax - mouseAreaMin);
			else
				percentageY = 0;
		else
			percentageY = 1;
		
		float buttonX = x - Gdx.graphics.getWidth() / 2f;
		float buttonY = -y + Gdx.graphics.getHeight() / 2f;
		
		lastMouseX = buttonX;
		for (Button b : buttons) {
			
			b.IsHovered((int)buttonX, (int)buttonY);
		}
		
		this.buttonOffsetY = 24 * percentageY;
		buttonShiftPercentage = percentageY;
	}
	
	public boolean click(int x, int y) {
		float xButton = x - Gdx.graphics.getWidth() / 2f;
		float yButton = -y + Gdx.graphics.getHeight() / 2f;
		for (Button b : this.buttons) {
			if (b.IsClicked((int)xButton, (int)yButton))
				return true;
		}
		return false;
	}
	
	@Override
	public void draw() {
		int i = 1;
		float placePerButton = (Gdx.graphics.getWidth() / (buttons.size() + 1f));
		float desiredPlacePerButton = 128 + 32;
		float desiredShift = Math.max(0, desiredPlacePerButton - placePerButton);
		
		for (Button b : this.buttons) {
			b.draw();
			float originalX = (Gdx.graphics.getWidth() / (buttons.size() + 1)) * i - Gdx.graphics.getWidth() / 2f;
			float diffToMouse = lastMouseX - originalX;
			float factor = Math.signum(diffToMouse);
			diffToMouse = Math.abs(diffToMouse);
			
			float offset = 0;
			if (diffToMouse < placePerButton)
				offset = buttonShiftPercentage * desiredShift * (diffToMouse / placePerButton);
			else
				offset = buttonShiftPercentage * desiredShift * (placePerButton / diffToMouse);
			
			b.x = originalX + (-factor * offset);
//			b.y = Gdx.graphics.getHeight() / -2f + buttonOffsetY;
			b.y = Gdx.graphics.getHeight() / -2f + 48;
			i++;
		}
	}

}
