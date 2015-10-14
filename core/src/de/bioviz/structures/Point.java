package de.bioviz.structures;

import de.bioviz.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by keszocze on 27.07.15.
 * @author Oliver Keszocze
 */
public class Point extends Pair<Integer, Integer> {
	static final Logger logger = LoggerFactory.getLogger(Point.class);
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

	public final static Point[] DIRECTIONS  = {NORTH, EAST, SOUTH, WEST};

	/*
	This hashCode method has been taken from http://stackoverflow.com/a/26981910
	 */
	@Override
	public int hashCode() {
		return (first << 16) + second;
	}



	// Bascially casts from enum to static points
	public static Point pointFromDirection(Direction dir) {
		switch (dir) {
			case NORTH:
				return NORTH;
			case EAST:
				return EAST;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
		}

		// note that this case should not be reachable
		return new Point(0, 0);
	}


	public static boolean reachable(Point p1, Point p2) {
		for (Point direction : DIRECTIONS) {
//			logger.debug("p1.add("+direction+")="+p1.add(direction)+" == "+p2);

			if (p1.add(direction).equals(p2)) {
				return true;
			}
		}
		return false;
	}

	public static boolean adjacent(Point p1, Point p2) {
		if (p1 == null || p2 == null) {
			return false;
		}

		return (Math.abs(p1.second - p2.second) <= 1) && (Math.abs(p1.first - p2.first) <= 1);

	}

}
