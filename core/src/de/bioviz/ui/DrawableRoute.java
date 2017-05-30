package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Point;
import de.bioviz.structures.Rectangle;

/**
 * Class responsible for drawing the routes that droplets may follow.
 * <p/>
 * Note that there is no corresponding class in the structure package. The
 * path's positions are computed by queyring the positions of the parent droplet
 * for each time step.
 */
public class DrawableRoute extends DrawableSprite {


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
	 * <p/>
	 * Currently that color is black.
	 */
	private Color baseColor = Color.BLACK;

	/**
	 * Creates a route for a given droplet.
	 *
	 * @param droplet
	 * 		The droplet this route belongs to.
	 */
	DrawableRoute(final DrawableDroplet droplet) {
		super(TextureE.StepMarker, droplet.viz);
		this.droplet = droplet;
		super.addLOD(DEFAULT_LOD_THRESHOLD, TextureE.BlackPixel);
		this.setZ(DisplayValues.DEFAULT_ROUTE_DEPTH);
	}

	/**
	 * Returns the color used for drawing the route.
	 * <p/>
	 * The color depends on the {@link BDisplayOptions} option ColorfulRoutes.
	 * If it is set to true, the droplet's color is used instead of black.
	 *
	 * @return The color of the route
	 */
	@Override
	public Color getColor() {
		Color c = baseColor.cpy();
		if (droplet.parentAssay.getDisplayOptions().getOption(
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


		DrawableAssay circ = droplet.parentAssay;
		int currentTime = circ.getCurrentTime();
		int displayAt;

		disableForcedLOD();

		int hoverTimesteps = 2 * circ.getDisplayRouteLength() + 8;

		int stepsToUse = circ.getDisplayRouteLength();
		if (droplet.isHovered()) {
			stepsToUse = hoverTimesteps;
		}

		for (int i = -stepsToUse; i < stepsToUse; i++) {

			Color c = getColor();


			if (circ.getDisplayOptions().getOption(
					BDisplayOptions.SolidPaths)) {
				c.a = noTransparency;
			} else {
				if (i >= 0) {
					c.a = 1 -
						  (Math.abs((float) i + 1) / ((float) stepsToUse + 1));
				} else {
					c.a = 1 - (Math.abs((float) i) / ((float) stepsToUse + 1));
				}
			}

			setColorImmediately(c);


			displayAt = currentTime + i;
			Rectangle r1 = droplet.droplet.getSafePositionAt(displayAt);
			Rectangle r2 = droplet.droplet.getSafePositionAt(displayAt + 1);

			Point p1 = r1.upperLeft();
			Point p2 = r2.upperLeft();


			int x1 = p1.fst;
			int x2 = p2.fst;
			int y1 = p1.snd;
			int y2 = p2.snd;

			float xCoord;
			float yCoord;


			// TODO @Jannis why do we add/sub .5 here?
			final float half = 0.5f;

			if (y1 == y2 && x2 > x1) {
				xCoord = circ.xCoordOnScreen(x1 + half);
				yCoord = circ.yCoordOnScreen(y1);
				setRotation(0);
			} else if (y1 == y2 && x2 < x1) {
				xCoord = circ.xCoordOnScreen(x1 - half);
				yCoord = circ.yCoordOnScreen(y1);
				setRotation(180);
			} else if (x1 == x2 && y2 > y1) {
				xCoord = circ.xCoordOnScreen(x1);
				yCoord = circ.yCoordOnScreen(y1 + half);
				setRotation(90);
			} else if (x1 == x2 && y2 < y1) {
				xCoord = circ.xCoordOnScreen(x1);
				yCoord = circ.yCoordOnScreen(y1 - half);
				setRotation(270);
			} else {
				continue;
			}

			setX(xCoord);
			setY(yCoord);
			setScaleX(circ.getSmoothScale());
			setScaleY(circ.getSmoothScale());

			super.draw();
		}
	}
}
