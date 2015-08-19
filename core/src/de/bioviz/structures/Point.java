package de.bioviz.structures;

import de.bioviz.util.Pair;

/**
 * Created by keszocze on 27.07.15.
 */
public class Point extends Pair<Integer, Integer> {
	public Point(int x, int y) {
		super(x, y);
	}

	public Point(Point p) {
		this(p.first, p.second);
	}

	public Point add(Point p) {
		return new Point(first + p.first, second + p.second);
	}

	public final static Point NORTH = new Point(0,1);
	public final static Point EAST = new Point(1,0);
	public final static Point SOUTH = new Point(0,-1);
	public final static Point WEST = new Point(-1,0);

	/*
	This hashCode method has been taken from http://stackoverflow.com/a/26981910
	 */
	@Override
	public int hashCode() {
		return (first << 16) + second;
	}

	public static Point pointFromDirection(Direction dir) {
		switch (dir) {
			case NORTH:
				return new Point(0, 1);
			case EAST:
				return new Point(1, 0);
			case SOUTH:
				return new Point(0, -1);
			case WEST:
				return new Point(-1, 0);
		}

		// note that this case should not be reachable
		return new Point(0, 0);
	}

	public static boolean adjacent(Point p1, Point p2) {
		if (p1 == null || p2 == null) {
			return false;
		}

		return (Math.abs(p1.second - p2.second) <= 1) && (Math.abs(p1.first - p2.first) <= 1);

	}

}
