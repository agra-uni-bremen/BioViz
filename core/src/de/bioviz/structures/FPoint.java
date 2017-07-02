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

package de.bioviz.structures;

import de.bioviz.util.Pair;

/**
 * @author Oliver Keszocze
 */
public class FPoint extends Pair<Float, Float> {


	/**
	 * Creates a point from two separate coordinates.
	 *
	 * @param x
	 * 		x coordinate of the point
	 * @param y
	 * 		y coordinate of the point
	 */
	public FPoint(final float x, final float y) {
		super(Float.valueOf(x), Float.valueOf(y));
	}


	/**
	 * Copy constructor for Point objects.
	 *
	 * @param p
	 * 		Point to copy
	 * 		<p>
	 * 		It is only a real copy constructor as long as Integer(int) is.
	 */
	public FPoint(final FPoint p) {
		this(Float.valueOf(p.fst), Float.valueOf(p.snd));
	}





	/**
	 * Adds a point by coordinate-wise addition.
	 * <p>
	 * This effectively moves the point on the 2D plane.
	 *
	 * @param p
	 * 		the point to add to this
	 * @return new Point created by adding the coordinates of p to this's
	 * coordinates
	 */
	public FPoint add(final FPoint p) {
		return new FPoint(fst + p.fst, snd + p.snd);
	}




	/**
	 * Computes positions on the line between two points.
	 *
	 * The computed position is  a pair of Floats, not of Integers!
	 *
	 * @param from
	 * 		The point to start from.
	 * @param to
	 * 		The point to move to.
	 * @param progressPercentage
	 * 		How much distance between the two points (in percent) has been
	 * 		traveled. Must be between 0 and 1 (both inclusive)
	 * @return A position on the line between the two points.
	 */
	public static FPoint lineBetween(
			final FPoint from,
			final FPoint to,
			final float progressPercentage) {


		final float xStart = from.fst;
		final float yStart = from.snd;

		final FPoint direction = FPoint.direction(from, to);

		final float xDiff = direction.fst;
		final float yDiff = direction.snd;

		final float lineX = xStart + progressPercentage * xDiff;
		final float lineY = yStart + progressPercentage * yDiff;

		return new FPoint(lineX, lineY);

	}

	/**
	 * Computes the position half way between two points.
	 * @param from The position to start from.
	 * @param to The position to end in.
	 * @return The position half way between the two points.
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	public static FPoint halfwayBetween(
			final FPoint from, final FPoint to
	) {
		return lineBetween(from, to, 0.5f);
	}

	/**
	 * Computest the angle between the line between to points and the horizon.
	 *
	 * @param from
	 * 		The point to start to draw the line from.
	 * @param to
	 * 		The point to end the line in.
	 * @return The angle of the line between the two points and the horizon.
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	public static float angleOfLineBetween(
			final FPoint from,
			final FPoint to
	) {

		final FPoint direction = FPoint.direction(from, to);


		final float xDiff = direction.fst;
		final float yDiff = direction.snd;

		// threshold for float comparison
		final float epsilon = 0.0000001f;
		// special case: vertical line
		if (Math.abs(xDiff) < epsilon) {
			return yDiff > 0 ? 90f : 270f;
		}

		final float slope = yDiff / xDiff;

		float angle = (float) Math.toDegrees(Math.atan(slope));

		// we only have angles between +90° and -90°, for the other cases we
		// need to add 180°

		if (xDiff < 0) {
			angle += 180;
		}

		return angle;

	}

	/**
	 * Computes the directed vector between to points.
	 *
	 * @param from
	 * 		The point to start from.
	 * @param to
	 * 		The point to end in.
	 * @return The directed vector between the two points.
	 */
	public static FPoint direction(final FPoint from, final FPoint to) {
		final float xStart = from.fst;
		final float yStart = from.snd;
		final float xEnd = to.fst;
		final float yEnd = to.snd;

		return new FPoint(xEnd - xStart, yEnd - yStart);
	}


}
