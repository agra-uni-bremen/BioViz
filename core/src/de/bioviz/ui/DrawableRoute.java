package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

import de.bioviz.structures.Point;
import de.bioviz.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawableRoute extends DrawableSprite {

	private static Logger logger = LoggerFactory.getLogger(DrawableRoute
																   .class);

	public static int routeDisplayLength = 0;
	public static int hoverTimesteps = 2 * routeDisplayLength + 8;

	public static float noTransparency = 1;

	public DrawableDroplet droplet;

	public Color baseColor = Color.BLACK.cpy();


	public DrawableRoute(DrawableDroplet droplet) {
		super(TextureE.StepMarker, droplet.viz);
		this.droplet = droplet;
		super.addLOD(DEFAULT_LOD_THRESHOLD, TextureE.BlackPixel);
	}

	@Override
	public void draw() {
		int currentTime = droplet.parentCircuit.currentTime;
		int displayAt;

		this.disableForcedLOD();

		hoverTimesteps = 2 * routeDisplayLength + 8;

		int stepsToUse = routeDisplayLength;
		if (this.droplet.isHovered()) {
			stepsToUse = hoverTimesteps;
		}

		for (int i = -stepsToUse; i < stepsToUse; i++) {

			Color c = this.baseColor.cpy();
			if (droplet.parentCircuit.displayOptions.getOption(
					BDisplayOptions.ColorfulRoutes)) {
				c = this.droplet.getColor().cpy();
			}


			if (droplet.parentCircuit.displayOptions.getOption(
					BDisplayOptions.SolidPaths)) {
				c.a = noTransparency;
			}
			else {
				if (i >= 0) {
					c.a = 1 -
						  (Math.abs((float) i + 1) / ((float) stepsToUse + 1));
				}
				else {
					c.a = 1 - (Math.abs((float) i) / ((float) stepsToUse + 1));
				}
			}

			this.setColorImmediately(c);


			displayAt = currentTime + i;
			Point p1 = droplet.droplet.getSafePositionAt(displayAt);
			Point p2 = droplet.droplet.getSafePositionAt(displayAt + 1);

			logger.trace("Point p1: {} (timestep {})", p1, displayAt);
			logger.trace("Point p2: {} (timestep {})", p2, displayAt + 1);

			int x1 = p1.fst;
			int x2 = p2.fst;
			int y1 = p1.snd;
			int y2 = p2.snd;

			float xCoord = droplet.parentCircuit.xCoordOnScreen(x1 + 0.5f);
			float yCoord = droplet.parentCircuit.yCoordOnScreen(y1);

			if (y1 == y2 && x2 > x1) {
				xCoord = droplet.parentCircuit.xCoordOnScreen(x1 + 0.5f);
				yCoord = droplet.parentCircuit.yCoordOnScreen(y1);
				this.setRotation(0);
			}
			else if (y1 == y2 && x2 < x1) {
				xCoord = droplet.parentCircuit.xCoordOnScreen(x1 - 0.5f);
				yCoord = droplet.parentCircuit.yCoordOnScreen(y1);
				this.setRotation(180);
			}
			else if (x1 == x2 && y2 > y1) {
				xCoord = droplet.parentCircuit.xCoordOnScreen(x1);
				yCoord = droplet.parentCircuit.yCoordOnScreen(y1 + 0.5f);
				this.setRotation(90);
			}
			else if (x1 == x2 && y2 < y1) {
				xCoord = droplet.parentCircuit.xCoordOnScreen(x1);
				yCoord = droplet.parentCircuit.yCoordOnScreen(y1 - 0.5f);
				this.setRotation(270);
			}
			else {
				continue;
			}

			this.setX(xCoord);
			this.setY(yCoord);
			this.setScaleX(droplet.parentCircuit.smoothScaleX);
			this.setScaleY(droplet.parentCircuit.smoothScaleY);

			super.draw();
		}

		boolean dropletLongIndicator = droplet.parentCircuit.displayOptions
				.getOption(BDisplayOptions.LongNetIndicatorsOnDroplets);
		if (dropletLongIndicator) {
			this.setForcedLOD(1f);
			Pair<Float, Float> target = new Pair<Float, Float>(
					this.droplet.droplet.getNet().getTarget().fst.floatValue(),
					this.droplet.droplet.getNet().getTarget().snd.floatValue
							());
			Pair<Float, Float> source = new Pair<Float, Float>(
					this.droplet.droplet.getFirstPosition().fst.floatValue(),
					this.droplet.droplet.getFirstPosition().snd.floatValue());
			Pair<Float, Float> current = new Pair<Float, Float>
					(this.droplet.droplet.smoothX,
					 this.droplet.droplet.smoothY);

			// draw to target
			DrawableLine.draw(target, current,
							  this.droplet.getColor().cpy().add(0.2f, 0.2f,
																0.2f, 0));
			DrawableLine.draw(source, current,
							  this.droplet.getColor().cpy().sub(0.2f, 0.2f,
																0.2f, 0));
		}
	}
}
