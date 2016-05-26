package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.bioviz.structures.Point;
import de.bioviz.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for drawing the routes that droplets may follow.
 * <p>
 * <p>
 * Note that there is no corresponding class in the structure package. The
 * path's positions are computed by queyring the positions of the parent droplet
 * for each time step.
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
	 * Non magic number version for turning off transparency.
	 */
	private static float noTransparency = 1;


	/**
	 * The droplet this route belongs to.
	 */
	public DrawableDroplet droplet;


	/**
	 * The color the route is drawn in of not superseeded by another option.
	 * <p>
	 * Currently that color is black.
	 */
	private Color baseColor = Color.BLACK;

	DrawableLine toTarget, fromSource;


	/**
	 * Creates a route for a given droplet.
	 *
	 * @param droplet
	 * 		The droplet this route belongs to.
	 */
	public DrawableRoute(final DrawableDroplet droplet) {
		super(TextureE.StepMarker, droplet.viz);
		this.droplet = droplet;
		super.addLOD(DEFAULT_LOD_THRESHOLD, TextureE.BlackPixel);
		toTarget = new DrawableLine(droplet.viz);
		fromSource = new DrawableLine(droplet.viz);
		this.setZ(DisplayValues.DEFAULT_ROUTE_DEPTH);
	}

	/**
	 * Returns the color used for drawingf the route.
	 * <p>
	 * The color depends on the {@link BDisplayOptions} option ColorfulRoutes.
	 * If it is set to true, the droplet's color is used instead of black.
	 *
	 * @return The color of the route
	 */
	public Color getColor() {
		Color c = baseColor.cpy();
		if (droplet.parentCircuit.getDisplayOptions().getOption(
				BDisplayOptions.ColorfulRoutes)) {
			c = droplet.getColor();
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
		DrawableCircuit circ = droplet.parentCircuit;
		int currentTime = circ.getCurrentTime();
		int displayAt;

		disableForcedLOD();

		int hoverTimesteps = 2 * routeDisplayLength + 8;

		int stepsToUse = routeDisplayLength;
		if (droplet.isHovered()) {
			stepsToUse = hoverTimesteps;
		}

		for (int i = -stepsToUse; i < stepsToUse; i++) {

			Color c = getColor();


			if (circ.getDisplayOptions().getOption(
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

			setColorImmediately(c);


			displayAt = currentTime + i;
			Point p1 = droplet.droplet.getSafePositionAt(displayAt);
			Point p2 = droplet.droplet.getSafePositionAt(displayAt + 1);

			logger.trace("Point p1: {} (timestep {})", p1, displayAt);
			logger.trace("Point p2: {} (timestep {})", p2, displayAt + 1);

			int x1 = p1.fst;
			int x2 = p2.fst;
			int y1 = p1.snd;
			int y2 = p2.snd;

			float xCoord;
			float yCoord;

			if (y1 == y2 && x2 > x1) {
				xCoord = circ.xCoordOnScreen(x1 + 0.5f);
				yCoord = circ.yCoordOnScreen(y1);
				setRotation(0);
			}
			else if (y1 == y2 && x2 < x1) {
				xCoord = circ.xCoordOnScreen(x1 - 0.5f);
				yCoord = circ.yCoordOnScreen(y1);
				setRotation(180);
			}
			else if (x1 == x2 && y2 > y1) {
				xCoord = circ.xCoordOnScreen(x1);
				yCoord = circ.yCoordOnScreen(y1 + 0.5f);
				setRotation(90);
			}
			else if (x1 == x2 && y2 < y1) {
				xCoord = circ.xCoordOnScreen(x1);
				yCoord = circ.yCoordOnScreen(y1 - 0.5f);
				setRotation(270);
			}
			else {
				continue;
			}

			setX(xCoord);
			setY(yCoord);
			setScaleX(circ.getSmoothScale());
			setScaleY(circ.getSmoothScale());

			super.draw();
		}

		boolean dropletLongIndicator = circ
				.getDisplayOptions()
				.getOption(BDisplayOptions.LongNetIndicatorsOnDroplets);
		if (dropletLongIndicator && droplet.droplet.hasNet()) {
			setForcedLOD(1f);
			Vector2 target = new Vector2(
					droplet.droplet.getNet().getTarget().fst.floatValue(),
					droplet.droplet.getNet().getTarget().snd.floatValue
							());
			Vector2 source = new Vector2(
					droplet.droplet.getFirstPosition().fst.floatValue(),
					droplet.droplet.getFirstPosition().snd.floatValue());
			Vector2 current = new Vector2
					(droplet.smoothX,
					 droplet.smoothY);

			// draw to target
			toTarget.from = current;
			toTarget.to = target;
			toTarget.setColor(droplet.getColor().add(
					Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF));
			toTarget.draw();
			
			fromSource.from = source;
			fromSource.to = current;
			fromSource.setColor(droplet.getColor().sub(
					Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF));
			fromSource.draw();
		}
	}
}
