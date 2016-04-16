package de.bioviz.svg;

import de.bioviz.structures.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 4/6/16.
 */
public enum GradDir {
	TOPLEFT(Point.NORTH, Point.WEST),
	TOPRIGHT(Point.NORTH, Point.EAST),
	BOTTOMLEFT(Point.SOUTH, Point.WEST),
	BOTTOMRIGHT(Point.SOUTH, Point.EAST),
	LEFTRIGHT(Point.EAST),
	RIGHTLEFT(Point.WEST),
	TOPBOTTOM(Point.SOUTH),
	BOTTOMTOP(Point.NORTH);

	private final List<Point> dir = new ArrayList<>();

	GradDir(Point... dirs) {
		for(Point p : dirs){
			dir.add(p);
		}
	}

	public List<Point> getDirs(){
		return dir;
	}
}
