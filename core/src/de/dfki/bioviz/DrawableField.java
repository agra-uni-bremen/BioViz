package de.dfki.bioviz;

import com.badlogic.gdx.graphics.Color;
import de.dfki.bioviz.structures.BiochipField;


public class DrawableField extends DrawableSprite {
	
	public BiochipField field;
	
	static final Color fieldDefaultColor = new Color(0.8f, 0.9f, 1f, 1f);
	static final Color fieldAdjacentActivationColor = new Color(1f, 0.3f, 0.2f, 1f);

	public DrawableField(BiochipField field) {
		super("GridMarker.png");
		this.field = field;
	}

	@Override
	public String generateSVG() {
		if (field.isEnabled) {
			return "<image x=\"" + this.field.x + "\" y=\"" + (-this.field.y + BioViz.singleton.currentCircuit.data.field[0].length - 1) + "\" width=\"1\" height=\"1\" xlink:href=\"field.svg\" />";
		} else {
			return "";
		}
	}
	
	@Override
	public void draw() {
		if (this.field.isEnabled) {
			float xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(field.x);
			float yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(field.y);

			this.x = xCoord;
			this.y = yCoord;
			this.scaleX = BioViz.singleton.currentCircuit.smoothScaleX;
			this.scaleY = BioViz.singleton.currentCircuit.smoothScaleY;

			if (BioViz.singleton.currentCircuit.getHighlightAdjacency() && BioViz.singleton.currentCircuit.data.getAdjacentActivations().contains(this.field))
				this.color = fieldAdjacentActivationColor;
			else
				this.color = fieldDefaultColor;

			super.draw();
		}
	}
}
