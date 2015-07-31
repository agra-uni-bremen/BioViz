package de.dfki.bioviz.structures;

import de.dfki.bioviz.util.Pair;

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

}
