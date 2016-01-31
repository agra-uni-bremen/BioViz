package de.bioviz.structures;

import java.util.ArrayList;

/**
 * @brief
 *
 * Created by keszocze on 22.07.15.
 *
 * @author keszocze
 */
public class Rectangle {

	Point lowerLeft;
	Point upperRight;


	/**
	 * @param p1
	 * 		First corner
	 * @param p2
	 * 		Second corner
	 * @brief Creates a rectangle defined by two corners.
	 * <p>
	 * Internally, the class stores the coordinates of the rectangle by
	 * computing the lower left and upper right corners. The provided corners
	 * need not be the lower left or upper right corner. This also means that
	 * the toString() method may produce unexpected output.
	 * <p>
	 * The two corners of the rectangle may be identical.
	 */
	public Rectangle(Point p1, Point p2) {
		this(p1.fst, p1.snd, p2.fst, p2.snd);
	}

	/**
	 * @param x1
	 * 		x-coordinate of the first corner
	 * @param y1
	 * 		y-coordinate of the first corner
	 * @param x2
	 * 		x-coordinate of the second corner
	 * @param y2
	 * 		y-coordinate of the second corner
	 * @brief Creates a rectangle defined by two corners (provided by their
	 * coordinates).
	 * <p>
	 * Internally, the class stores the coordinates of the rectangle by
	 * computing the lower left and upper right corners. The provided corners
	 * need not be the lower left or upper right corner. This also means that
	 * the toString() method may produce unexpected output.
	 * <p>
	 * The two corners of the rectangle may be identical.
	 */
	public Rectangle(int x1, int y1, int x2, int y2) {


		int minX = Math.min(x1, x2);
		int minY = Math.min(y1, y2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);

		lowerLeft = new Point(minX, minY);
		upperRight = new Point(maxX, maxY);

	}


	/**
	 * @param p
	 * 		Point to check for
	 * @return true if p is within the rectangle, false otherwise
	 * @brief Checks whether a given point is within the boundaries of the
	 * rectangle
	 */
	public boolean contains(Point p) {
		return contains(p.fst, p.snd);
	}


	/**
	 * @param x
	 * 		x-coordinate of Point to check for
	 * @param y
	 * 		y-coordinate of Point to check for
	 * @return true if (x,y) is within the rectangle, false otherwise
	 * @brief Checks whether a given point is within the boundaries of the
	 * rectangle
	 */
	public boolean contains(int x, int y) {

		return lowerLeft.fst <= x && x <= upperRight.fst &&
			   lowerLeft.snd <= y && y <= upperRight.snd;
	}

	/**
	 *
	 * @return ArrayList of the points of this rectangle.
	 */
	public ArrayList<Point> positions() {

		ArrayList<Point> result = new ArrayList<Point>();

		for (int x = lowerLeft.fst; x <= upperRight.fst; x++) {
			for (int y = lowerLeft.fst; y <= upperRight.snd; y++) {
				result.add(new Point(x, y));
			}
		}

		return result;
	}


	/**
	 *
	 * @return  String of the form "Rect[(lowerLeft.x,lowerLeft.y) (upperRight.x,upperRight.y)]"
	 */
	public String toString() {
		return "Rect[" + lowerLeft + " " + upperRight + "]";
	}


}
