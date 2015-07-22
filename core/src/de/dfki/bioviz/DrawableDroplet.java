package de.dfki.bioviz;

import structures.Droplet;

public class DrawableDroplet extends DrawableSprite {
	
	public Droplet droplet;

	public DrawableDroplet(Droplet droplet) {
		super("Droplet.png");
		this.droplet = droplet;
	}

	@Override
	public String generateSVG() {
		// TODO Auto-generated method stub
		return "<image x=\"" + this.x + "\" y=\"" + this.y + "\" width=\"1\" height=\"1\" xlink:href=\"droplet.svg\" />";
	}

	@Override
	public void draw() {		
		droplet.targetX = droplet.getXAt(BioViz.singleton.currentCircuit.currentTime);
		droplet.targetY = droplet.getYAt(BioViz.singleton.currentCircuit.currentTime);
		
		droplet.update();
		
		float xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(droplet.smoothX);
		float yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(droplet.smoothY);
		
		this.x = xCoord;
		this.y = yCoord;
		this.scaleX = BioViz.singleton.currentCircuit.smoothScaleX;
		this.scaleY = BioViz.singleton.currentCircuit.smoothScaleY;
		
		super.draw();
	}
}
