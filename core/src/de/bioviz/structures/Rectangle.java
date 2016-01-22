package de.bioviz.structures;

import java.util.ArrayList;

/**
 * Created by keszocze on 22.07.15.
 */
public class Rectangle {

	int x1, x2, y1, y2;

	Point bottomLeft;
	Point topRight;

	public Point fstCorner() {
		return new Point(x1, y1);
	}

	public Point sndCorner() {
		return new Point(x2, y2);
	}

	public Rectangle(Point p1, Point p2) {
		this(p1.fst, p1.snd, p2.fst, p2.snd);
	}

	public Rectangle(int x1, int y1, int x2, int y2) {


		int minX = Math.min(x1, x2);
		int minY = Math.min(y1, y2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);

		bottomLeft = new Point(minX, minY);
		topRight = new Point(maxX, maxY);

	}

	public boolean contains(Point p) {
		return contains(p.fst, p.snd);
	}

	public boolean contains(int x, int y) {

		return bottomLeft.fst <= x && x <= topRight.fst && bottomLeft.snd <= y && y <= topRight.snd;
	}

	public ArrayList<Point> positions() {

		ArrayList<Point> result = new ArrayList<Point>();

		for (int x = bottomLeft.fst; x <= topRight.fst; x++) {
			for (int y = bottomLeft.fst; y <= topRight.snd; y++) {
				result.add(new Point(x, y));
			}
		}

		return result;
	}

	public String toString() {
		return "Rect[" + bottomLeft + " " + topRight + "]";
	}


}
