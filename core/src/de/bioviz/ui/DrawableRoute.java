/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.FPoint;
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


	/**
	 * Actually draws the route on the canvas.
	 *
	 * This includes computing the length of the route to show (i.e. how many
	 * steps in time you go backwards and/or forward), the color of the route
	 * and the transparency.
	 */
	@Override
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
			final Rectangle r1 = droplet.droplet.getSafePositionAt(displayAt);
			final Rectangle r2 =
					droplet.droplet.getSafePositionAt(displayAt + 1);


			if (r1.equals(r2)) {
				continue;
			}

			final FPoint p1 = r1.centerFloat();
			final FPoint p2 = r2.centerFloat();



			/*
			The routes are drawn by placing the arrow halfway between the two
			adjacent cells and then rotating it until it faces the correct
			direction.

			The position to place the arrow on is determined by computing the
			line between the two centers of the cells and then halving it.
			 */

			final FPoint halfWay = FPoint.halfwayBetween(p1,p2);
			final float xCoord = circ.xCoordOnScreen(halfWay.fst);
			final float yCoord = circ.yCoordOnScreen(halfWay.snd);


			// the angle is set according to the relative positions of the cell
			// centers that are the source and target of the arrow.
			float rotationAngle = FPoint.angleOfLineBetween(p1,p2);
			setRotation(rotationAngle);

			setX(xCoord);
			setY(yCoord);
			setScaleX(circ.getSmoothScale());
			setScaleY(circ.getSmoothScale());

			super.draw();
		}
	}
}
