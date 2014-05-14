package de.dfki.revvisgdx;

import java.util.Vector;

import com.badlogic.gdx.Gdx;

import de.dfki.revvisgdx.buttons.FunctionButton;

public class Menu implements Drawable {
	private Vector<Button> buttons = new Vector<Button>();
	private float lastMouseX = 0f;
	private float buttonShiftPercentage = 0f;
	private int lines = 1;
	
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
		float placePerButton = (Gdx.graphics.getWidth() / (buttons.size() + 1f));
		float desiredPlacePerButton = 128 + 32;
		
		lines = (int)((desiredPlacePerButton / 1.5f) / placePerButton) + 1;
		placePerButton *= lines;
		
		float desiredShift = Math.max(0, desiredPlacePerButton - placePerButton);
		
		int buttonsPerLine = buttons.size() / lines;
		int currentButton = 1;
		int currentLine = 1;
		
		for (Button b : this.buttons) {
			b.draw();
			float originalX = (Gdx.graphics.getWidth() / (buttonsPerLine + 1)) * currentButton - Gdx.graphics.getWidth() / 2f;
			float diffToMouse = lastMouseX - originalX;
			float factor = Math.signum(diffToMouse);
			diffToMouse = Math.abs(diffToMouse);
			
			float offset = 0;
			if (diffToMouse < placePerButton)
				offset = buttonShiftPercentage * desiredShift * (diffToMouse / placePerButton);
			else
				offset = buttonShiftPercentage * desiredShift * (placePerButton / diffToMouse);
			
			b.x = originalX + (-factor * offset);
			b.y = Gdx.graphics.getHeight() / -2f - 32 + (72 * currentLine);
			currentButton++;
			if (currentButton > buttonsPerLine) {
				currentButton = 1;
				currentLine++;
			}
		}
	}

}
