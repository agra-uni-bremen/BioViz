package de.dfki.bioviz;

import com.badlogic.gdx.graphics.Color;

public class DrawableRoute extends DrawableSprite {

	public static int timesteps = 4;
	private DrawableDroplet parent;
	
	public Color baseColor = Color.BLACK;
	
	public DrawableRoute(DrawableDroplet parent) {
		super("StepMarker.png");
		this.parent = parent;
	}

	@Override
	public String generateSVG() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void draw() {
		long currentTime = BioViz.singleton.currentCircuit.currentTime;
		long displayAt;
		
		for(int i = -timesteps; i < timesteps; i++) {
			
			this.color = this.baseColor.cpy();
			this.color.a = 1 - (Math.abs((float)i) / ((float)this.timesteps));
			
			displayAt = currentTime + i;
			int x1 = parent.droplet.getXAt(displayAt);
			int x2 = parent.droplet.getXAt(displayAt + 1);
			int y1 = parent.droplet.getYAt(displayAt);
			int y2 = parent.droplet.getYAt(displayAt + 1);
			
			float xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(x1 + 0.5f);
			float yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(y1);
			
			if (y1 == y2 && x2 > x1) {
				xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(x1 + 0.5f);
				yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(y1);
				this.rotation = 0;
			} else if (y1 == y2 && x2 < x1) {
				xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(x1 - 0.5f);
				yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(y1);
				this.rotation = 180;
			} else if (x1 == x2 && y2 > y1) {
				xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(x1);
				yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(y1 + 0.5f);
				this.rotation = 90;
			} else if (x1 == x2 && y2 < y1) {
				xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(x1);
				yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(y1 - 0.5f);
				this.rotation = 270;
			} else {
				continue;
			}

			this.x = xCoord;
			this.y = yCoord;
			this.scaleX = BioViz.singleton.currentCircuit.smoothScaleX;
			this.scaleY = BioViz.singleton.currentCircuit.smoothScaleY;
			
			super.draw();
		}
	}

}
