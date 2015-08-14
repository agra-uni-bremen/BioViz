package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Point;

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
		int displayAt;

		for (int i = -timesteps; i < timesteps; i++) {

			float alpha = 1 - (Math.abs((float) i) / ((float) timesteps));

			// TODO possible problem here due to casting
			displayAt = (int) currentTime + i;

			Point p1 = parent.droplet.getPositionAt(displayAt);
			Point p2 = parent.droplet.getPositionAt(displayAt + 1);

			int x1 = p1.first;
			int x2 = p2.first;
			int y1 = p1.second;
			int y2 = p2.second;

			float targetX = x1 + 0.5f;
			float targetY = -y1 +
					BioViz.singleton.currentCircuit.data.getMaxCoord().second - 1;
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
		int displayAt;

		hoverTimesteps = 2 * timesteps + 8;

		int stepsToUse = timesteps;
		if (this.parent.isHovered()) {
			stepsToUse = hoverTimesteps;
		}

		for (int i = -stepsToUse; i < stepsToUse; i++) {

			this.color = this.baseColor.cpy();
			if (i >= 0) {
				this.color.a = 1 - (Math.abs((float) i + 1) / ((float) stepsToUse + 1));
			}
			else {
				this.color.a = 1 - (Math.abs((float) i) / ((float) stepsToUse + 1));
			}

			displayAt = (int)currentTime + i;
			Point p1 = parent.droplet.getPositionAt(displayAt);
			Point p2 = parent.droplet.getPositionAt(displayAt + 1);

			int x1 = p1.first;
			int x2 = p2.first;
			int y1 = p1.second;
			int y2 = p2.second;

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
