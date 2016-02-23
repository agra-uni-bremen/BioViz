package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawableRoute extends DrawableSprite {

	private static Logger logger = LoggerFactory.getLogger(DrawableRoute.class);
	
	private enum routeDrawingMode {
		perField,
		longEnd
	}
	
	private routeDrawingMode currentRouteDrawingMode =
			routeDrawingMode.longEnd;

	public static int routeDisplayLength = 0;
	public static int hoverTimesteps = 2 * routeDisplayLength + 8;

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


		// TODO drawing of routes is now broken :(
		// I totally do not get this if condition an what is supposed to be broken? (Oliver)
		if (currentRouteDrawingMode == routeDrawingMode.perField) {
			
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
				
				if (i >= 0) {
					c.a = 1 - (Math.abs((float) i + 1) / ((float) stepsToUse + 1));
				} else {
					c.a = 1 - (Math.abs((float) i) / ((float) stepsToUse + 1));
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
					this.rotation = 0;
				} else if (y1 == y2 && x2 < x1) {
					xCoord = droplet.parentCircuit.xCoordOnScreen(x1 - 0.5f);
					yCoord = droplet.parentCircuit.yCoordOnScreen(y1);
					this.rotation = 180;
				} else if (x1 == x2 && y2 > y1) {
					xCoord = droplet.parentCircuit.xCoordOnScreen(x1);
					yCoord = droplet.parentCircuit.yCoordOnScreen(y1 + 0.5f);
					this.rotation = 90;
				} else if (x1 == x2 && y2 < y1) {
					xCoord = droplet.parentCircuit.xCoordOnScreen(x1);
					yCoord = droplet.parentCircuit.yCoordOnScreen(y1 - 0.5f);
					this.rotation = 270;
				} else {
					continue;
				}

				this.x = xCoord;
				this.y = yCoord;
				this.scaleX = droplet.parentCircuit.smoothScaleX;
				this.scaleY = droplet.parentCircuit.smoothScaleY;

				super.draw();
			}
		} else if (currentRouteDrawingMode == routeDrawingMode.longEnd) {
			this.setForcedLOD(1f);
			Point target = this.droplet.droplet.getNet().getTarget();
			Point source = this.droplet.droplet.getFirstPosition();
			Point current = this.droplet.droplet.getPositionAt(currentTime);
			
			Point toTarget = new Point(
					target.fst - current.fst, target.snd - current.snd);
			final float len = (float)Math.sqrt(
					toTarget.fst * toTarget.fst + toTarget.snd * toTarget.snd); 
			this.x = droplet.parentCircuit.xCoordOnScreen(
					(current.fst + target.fst) / 2f);
			this.y = droplet.parentCircuit.yCoordOnScreen(
					(current.snd + target.snd) / 2f);
			this.scaleX = droplet.parentCircuit.smoothScaleX * len;
			this.scaleY = 2f;
			this.rotation = (float)
					(Math.atan2(toTarget.snd, toTarget.fst) * (180f / Math.PI));
			this.setColor(this.baseColor);
			
			super.draw();
		}
	}

}
