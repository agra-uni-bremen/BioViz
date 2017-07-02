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

package de.bioviz.svg;

import de.bioviz.structures.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * This enum represents the possible directions for a gradient.
 *
 * @author malu.
 */
public enum GradDir {
	/** Gradient from bottomright to topleft. */
	TOPLEFT(Point.NORTH, Point.WEST),
	/** Gradient from bottomleft to topright. */
	TOPRIGHT(Point.NORTH, Point.EAST),
	/** Gradient from topright to bottomleft. */
	BOTTOMLEFT(Point.SOUTH, Point.WEST),
	/** Gradient from topleft to bottomright. */
	BOTTOMRIGHT(Point.SOUTH, Point.EAST),
	/** Gradient from left to right. */
	LEFTRIGHT(Point.EAST),
	/** Gradient from right to left. */
	RIGHTLEFT(Point.WEST),
	/** Gradient from top to bottom. */
	TOPBOTTOM(Point.SOUTH),
	/** Gradient from bottom to top. */
	BOTTOMTOP(Point.NORTH);

	/**
	 * A list containing the gradient orientations.
	 */
	private final List<Point> orientations = new ArrayList<>();

	/**
	 * Constructor for a gradDir.
	 *
	 * @param orientation the orientations of this gradDir
	 */
	GradDir(final Point... orientation) {
		for (final Point p : orientation) {
			orientations.add(p);
		}
	}

	/**
	 * Getter for the orientations.
	 *
	 * @return list with Point objects
	 */
	public List<Point> getOrientation() {
		return orientations;
	}

	/**
	 * Checks if a gradient has a certain orientation.
	 *
	 * @param orientation the orientation to check
	 * @return true or false
	 */
	public boolean hasOrientation(final Point orientation) {
		return orientations.contains(orientation);
	}
}
