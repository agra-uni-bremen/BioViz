package de.dfki.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

public class DrawableRoute extends DrawableSprite {

	public static int timesteps = 0;
	public static int hoverTimesteps = 2 * timesteps + 8;
	
	private DrawableDroplet parent;
	
	public Color baseColor = Color.BLACK;
	
	public DrawableRoute(DrawableDroplet parent) {
		super("StepMarker.png");
		this.parent = parent;
		super.addLOD(defaultLODThreshold, "BlackPixel.png");
	}

	@Override
	public String generateSVG() {
		String result = "";
		long currentTime = BioViz.singleton.currentCircuit.currentTime;
		long displayAt;
		
		for(int i = -timesteps; i < timesteps; i++) {
			
			float alpha = 1 - (Math.abs((float)i) / ((float)timesteps));
			displayAt = currentTime + i;
			int x1 = parent.droplet.getXAt(displayAt);
			int x2 = parent.droplet.getXAt(displayAt + 1);
			int y1 = parent.droplet.getYAt(displayAt);
			int y2 = parent.droplet.getYAt(displayAt + 1);
			
			float targetX = x1 + 0.5f;
			float targetY = -y1 + BioViz.singleton.currentCircuit.data.field[0].length - 1;
			if (y1 == y2 && x2 > x1) {
				result += "<image x=\"" + targetX + "\" y=\"" + targetY + "\" width=\"1\" height=\"1\" xlink:href=\"StepMarker.svg\" />";
			} else if (y1 == y2 && x2 < x1) {
				result += "<image x=\"" + targetX + "\" y=\"" + targetY + "\" width=\"1\" height=\"1\" transform=\"rotate(180 " + targetX + " " + (targetY + 0.5f) + " )\" opacity=\"" + alpha + "\" xlink:href=\"StepMarker.svg\" />";
			} else if (x1 == x2 && y2 > y1) {
				result += "<image x=\"" + targetX + "\" y=\"" + targetY + "\" width=\"1\" height=\"1\" transform=\"rotate(270 " + targetX + " " + (targetY + 0.5f) + " )\" opacity=\"" + alpha + "\" xlink:href=\"StepMarker.svg\" />";
			} else if (x1 == x2 && y2 < y1) {
				result += "<image x=\"" + targetX + "\" y=\"" + targetY + "\" width=\"1\" height=\"1\" transform=\"rotate(90 " + targetX + " " + (targetY + 0.5f) + " )\" opacity=\"" + alpha + "\" xlink:href=\"StepMarker.svg\" />";
			} else {
				continue;
			}
		}
		return result;
	}
	
	@Override
	public void draw() {
		long currentTime = BioViz.singleton.currentCircuit.currentTime;
		long displayAt;
		
		hoverTimesteps = 2 * timesteps + 8;
		
		int stepsToUse = timesteps;
		if (this.parent.isHovered()) {
			stepsToUse = hoverTimesteps;
		}
		
		for(int i = -stepsToUse; i < stepsToUse; i++) {
			
			this.color = this.baseColor.cpy();
			if (i >= 0)
				this.color.a = 1 - (Math.abs((float)i + 1) / ((float)stepsToUse + 1));
			else
				this.color.a = 1 - (Math.abs((float)i) / ((float)stepsToUse + 1));
			
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
