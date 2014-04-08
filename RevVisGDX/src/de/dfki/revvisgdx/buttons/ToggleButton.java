package de.dfki.revvisgdx.buttons;

import de.dfki.revvisgdx.Button;

public class ToggleButton extends Button {
	Object toToggleOn;
	String toggleVarName, textureOn, textureOff;
	
	public ToggleButton(String textureFilenameOn, String textureFilenameOff, Object toggleOn, String varName) {
		super(textureFilenameOn);
		this.toToggleOn = toggleOn;
		this.toggleVarName = varName;
		this.textureOn = textureFilenameOn;
		this.textureOff = textureFilenameOff;
	}

	@Override
	public void Clicked() {
		try {
			boolean newValue = !toToggleOn.getClass().getField(toggleVarName).getBoolean(toToggleOn);
			toToggleOn.getClass().getField(toggleVarName).setBoolean(toToggleOn, newValue);
			if (newValue)
				this.setTexture(textureOn);
			else
				this.setTexture(textureOff);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
