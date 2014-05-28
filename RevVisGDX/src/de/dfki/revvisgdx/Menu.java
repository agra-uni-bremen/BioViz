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
		float desiredPlacePerButton = 128 + 38;
		int buttonsPerLine = (int)(Gdx.graphics.getWidth() / desiredPlacePerButton);
		int buttonsInLastLine = buttons.size() % buttonsPerLine;
		if (buttonsInLastLine == 0)
			buttonsInLastLine = buttonsPerLine;
		
		lines = (int)(buttons.size() / buttonsPerLine);
		if (buttons.size() % buttonsPerLine != 0)
			lines++;
		
		int currentButton = 1;
		int currentLine = lines;
		float placePerButton = (Gdx.graphics.getWidth() / (buttonsPerLine + 1));
		
		for (Button b : this.buttons) {
			
			float originalX = placePerButton * currentButton - Gdx.graphics.getWidth() / 2f;
			
			b.x = originalX;
			b.y = Gdx.graphics.getHeight() / -2f - 32 + (72 * currentLine);
			if (currentLine == 1) {
				b.x += placePerButton * ((buttonsPerLine - buttonsInLastLine) / 2f);
			}
			
			currentButton++;
			if (currentButton > buttonsPerLine) {
				currentButton = 1;
				currentLine--;
			}
			
			b.draw();
		}
	}

}
