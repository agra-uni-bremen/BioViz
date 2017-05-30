package de.bioviz.structures;

import de.bioviz.util.Pair;

/**
 * @author Oliver Keszocze
 */
public class Point extends Pair<Integer, Integer> {

	/**
	 * Direction pointing north.
	 */
	public static final Point NORTH = new Point(0, 1);

	/**
	 * Direction pointing east.
	 */
	public static final Point EAST = new Point(1, 0);

	/**
	 * Direction pointing south.
	 */
	public static final Point SOUTH = new Point(0, -1);

	/**
	 * Direction pointing west.
	 */
	public static final Point WEST = new Point(-1, 0);

	/**
	 * Collects all compass directions.
	 */
	public static final Point[] DIRECTIONS = {NORTH, EAST, SOUTH, WEST};


	/**
	 * Creates a point from two separate coordinates.
	 *
	 * @param x
	 * 		x coordinate of the point
	 * @param y
	 * 		y coordinate of the point
	 */
	public Point(final int x, final int y) {
		super(Integer.valueOf(x), Integer.valueOf(y));
	}


	/**
	 * Copy constructor for Point objects.
	 *
	 * @param p
	 * 		Point to copy
	 * 		<p>
	 * 		It is only a real copy constructor as long as Integer(int) is.
	 */
	public Point(final Point p) {
		this(Integer.valueOf(p.fst), Integer.valueOf(p.snd));
	}


	/**
	 * Computes the hashcode for the Point.
	 * <p>
	 * This hashCode method has been taken from http://stackoverflow
	 * .com/a/26981910
	 */
	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public int hashCode() {
		return (fst << 16) + snd;
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
	public Point add(final Point p) {
		return new Point(fst + p.fst, snd + p.snd);
	}


	/**
	 * Creates a Point object pointing in the specified direction.
	 * <p>
	 * This is a very weird way of performing some kind of typecast.
	 *
	 * @param dir
	 * 		Direction that is to be translated into a point.
	 * @return Point corresponding to the direction.
	 */
	public static Point pointFromDirection(final Direction dir) {
		switch (dir) {
			case NORTH:
				return NORTH;
			case EAST:
				return EAST;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
			default:
				return new Point(0, 0); // this shouldn't be ever reached!
		}
	}


	/**
	 * Check whether one point can be reached by another point in exactly one
	 * time step moving horizontally, vertically or by not moving at all.
	 *
	 * @param p1
	 * 		first point
	 * @param p2
	 * 		second point
	 * @return true if p2 is reachable from p1 in one time step
	 */
	public static boolean reachable(final Point p1, final Point p2) {
		if (p1.equals(p2)) {
			return true;
		}
		for (final Point direction : DIRECTIONS) {
			if (p1.add(direction).equals(p2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether two points are adjacent to each other.
	 *
	 * @param p1
	 * 		first Point
	 * @param p2
	 * 		second Point
	 * @return true if p1 and p2 are adjacent, false otherwise
	 * @note This method does only compare the points by examining their
	 * coordinates. It does *not* take into account the grid, the droplets are
	 * moving on.
	 */
	public static boolean adjacent(final Point p1, final Point p2) {
		if (p1 == null || p2 == null) {
			return false;
		}

		return (Math.abs(p1.snd - p2.snd) <= 1) &&
			   (Math.abs(p1.fst - p2.fst) <= 1);
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
	public static Pair<Float, Float> lineBetween(
			final Point from,
			final Point to,
			final float progressPercentage) {


		final int xStart = from.fst;
		final int yStart = from.snd;

		final Point direction = Point.direction(from, to);

		final int xDiff = direction.fst;
		final int yDiff = direction.snd;

		final float lineX = xStart + progressPercentage * xDiff;
		final float lineY = yStart + progressPercentage * yDiff;

		return new Pair<>(lineX, lineY);

	}

	/**
	 * Computes the position half way between two points.
	 * @param from The position to start from.
	 * @param to The position to end in.
	 * @return The position half way between the two points.
	 */
	public static Pair<Float, Float> halfwayBetween(
			final Point from, final Point to
	) {
		return lineBetween(from,to,0.5f);
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
			final Point from,
			final Point to
	) {

		final Point direction = Point.direction(from, to);


		final int xDiff = direction.fst;
		final int yDiff = direction.snd;

		// special case: vertical line
		if (xDiff == 0) {
			return yDiff > 0 ? 90f : 270f;
		}

		final float slope = yDiff / (float) xDiff;

		return (float) Math.toDegrees(Math.atan(slope));

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
	public static Point direction(final Point from, final Point to) {
		final int xStart = from.fst;
		final int yStart = from.snd;
		final int xEnd = to.fst;
		final int yEnd = to.snd;

		return new Point(xEnd - xStart, yEnd - yStart);
	}


}
