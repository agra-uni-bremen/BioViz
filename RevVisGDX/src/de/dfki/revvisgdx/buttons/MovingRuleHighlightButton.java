package de.dfki.revvisgdx.buttons;

import de.dfki.revvisgdx.Button;
import de.dfki.revvisgdx.RevVisGDX;

public class MovingRuleHighlightButton extends Button {
	public MovingRuleHighlightButton() {
		super("data/MovingRuleHighlightNone.png");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Clicked() {
		super.Clicked();
		
		RevVisGDX.singleton.currentCircuit.toggleMovingRuleHighlight();
		switch(RevVisGDX.singleton.currentCircuit.highlightHoveredGateMovingRule) {
		case boxes:
			this.setTexture("data/MovingRuleHighlightBoxes.png");
			break;
		case none:
			this.setTexture("data/MovingRuleHighlightNone.png");
			break;
		case whiteBars:
			this.setTexture("data/MovingRuleHighlightBars.png");
			break;
		default:
			break;
		}
	}
}
