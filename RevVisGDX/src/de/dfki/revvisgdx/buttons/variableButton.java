package de.dfki.revvisgdx.buttons;

import de.dfki.revvisgdx.Button;
import de.dfki.revvisgdx.RevVisGDX;

public class variableButton extends Button {

	public variableButton() {
		super("data/VariableButtonPixel.png");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Clicked() {
		super.Clicked();
		
		RevVisGDX.singleton.currentCircuit.toggleLineWidth();
		switch(RevVisGDX.singleton.currentCircuit.lineType) {
			case full:
				this.setTexture("data/VariableButtonFull.png");
				break;
			case hidden:
				this.setTexture("data/VariableButtonHidden.png");
				break;
			case pixelWide:
				this.setTexture("data/VariableButtonPixel.png");
				break;
			case usageWide:
				this.setTexture("data/VariableButtonUsage.png");
				break;
			default:
				break;
		}
	}

}
