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
		super(new Integer(x), new Integer(y));
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
		this(new Integer(p.fst), new Integer(p.snd));
	}


	/**
	 * Computes the hashcode for the Point.
	 * <p>
	 * This hashCode method has been taken from http://stackoverflow
	 * .com/a/26981910
	 */
	@Override
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
	 * Non-static wrapper for static adjacency function.
	 *
	 * @param p2
	 * 		the other point to check for adjacency
	 * @return whether or not the points are adjacent
	 */
	public boolean adjacent(final Point p2) {
		return adjacent(this, p2);
	}

}
