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
	GradDir(Point... orientation) {
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
