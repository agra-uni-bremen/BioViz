package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

import de.bioviz.structures.Point;
import de.bioviz.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for drawing the routes that droplets may follow.
 *
 *
 * Note that there is no corresponding class in the structure package. The
 * path's positions are computed by queyring the positions of the parent
 * droplet for each time step.
 */
public class DrawableRoute extends DrawableSprite {
	/**
	 * Class-wide logging facility.
	 */
	private static Logger logger = LoggerFactory.getLogger(DrawableRoute
																   .class);

	/**
	 * What length of the route will be displayed.
	 */
	public static int routeDisplayLength = 0;


	/**
	 * What length of the route will be displayed when hovering over a droplet.
	 */
	private static int hoverTimesteps = 2 * routeDisplayLength + 8;


	/**
	 * Non magic number version for turning off transparency.
	 */
	private static float noTransparency = 1;


	/**
	 * The droplet this route belongs to.
	 */
	public DrawableDroplet droplet;




	/**
	 * The color the route is drawn in of not superseeded by another option.
	 *
	 * Currently that color is black.
	 */
	private Color baseColor = Color.BLACK.cpy();


	/**
	 * Creates a route for a given droplet.
	 * @param droplet The droplet this route belongs to.
	 */
	public DrawableRoute(final DrawableDroplet droplet) {
		super(TextureE.StepMarker, droplet.viz);
		this.droplet = droplet;
		super.addLOD(DEFAULT_LOD_THRESHOLD, TextureE.BlackPixel);
	}

	/**
	 * Returns the color used for drawingf the route.
	 *
	 * The color depends on the {@link BDisplayOptions} option ColorfulRoutes.
	 * If it is set to true, the droplet's color is used instead of black.
	 *
	 * @return The color of the route
	 */
	public Color getColor() {
		Color c = this.baseColor.cpy();
		if (droplet.parentCircuit.displayOptions.getOption(
				BDisplayOptions.ColorfulRoutes)) {
			c = this.droplet.getColor().cpy();
		}
		return c;
	}

	@Override
	/**
	 * Actually draws the route on the canvas.
	 *
	 * This includes computing the length of the route to show (i.e. how many
	 * steps in time you go backwards and/or forward), the color of the route
	 * and the transparency.
	 */
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

			Color c = getColor();


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
