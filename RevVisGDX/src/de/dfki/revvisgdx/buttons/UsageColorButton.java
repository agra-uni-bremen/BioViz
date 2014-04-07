package de.dfki.revvisgdx.buttons;

import de.dfki.revvisgdx.Button;
import de.dfki.revvisgdx.RevVisGDX;

public class UsageColorButton extends Button {
	public UsageColorButton() {
		super("data/VariableButtonUsageNone.png");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Clicked() {
		super.Clicked();
		
		RevVisGDX.singleton.currentCircuit.toggleLineUsageColouring();
		switch(RevVisGDX.singleton.currentCircuit.drawLinesColourizedWhenUsed) {
		case absolute:
			this.setTexture("data/VariableButtonUsageAbsolute.png");
			break;
		case none:
			this.setTexture("data/VariableButtonUsageNone.png");
			break;
		case relative:
			this.setTexture("data/VariableButtonUsageRelative.png");
			break;
		default:
			break;
		}
	}
}
