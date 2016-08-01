package de.bioviz.structures;

import de.bioviz.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Keszocze
 */
public class Rectangle {

	/**
	 * The lower left corner of the rectangle.
	 */
	public Point lowerLeft;

	/**
	 * The upper right corner of the rectangle.
	 */
	public Point upperRight;


	/**
	 * Creates a rectangle defined by two corners.
	 *
	 * @param p1
	 * 		First corner
	 * @param p2
	 * 		Second corner
	 * 		<p>
	 * 		Internally, the class stores the coordinates of the rectangle by
	 * 		computing the lower left and upper right corners. The provided
	 * 		corners
	 * 		need not be the lower left or upper right corner. This also means
	 * 		that
	 * 		the toString() method may produce unexpected output.
	 * 		<p>
	 * 		The two corners of the rectangle may be identical.
	 */
	public Rectangle(final Point p1, final Point p2) {
		this(p1.fst, p1.snd, p2.fst, p2.snd);
	}

	/**
	 * Copy constructur for Rectangles.
	 * @param rec The rectangle that is to be copied.
	 */
	public Rectangle(final Rectangle rec) {
		this(new Integer(rec.lowerLeft.fst),
			 new Integer(rec.lowerLeft.snd),
			 new Integer(rec.upperRight.fst),
			 new Integer(rec.upperRight.snd));
	}


	/**
	 * Creates a Rectangle defined by the upper left corner and a size.
	 *
	 * @param pos
	 * 		Top left corner of the rectangle
	 * @param xSize
	 * 		The width of the rectangle
	 * @param ySize
	 * 		The height of the rectangle
	 */
	public Rectangle(final Point pos, final int xSize, final int ySize) {
		this(pos.fst, pos.snd, pos.fst - (xSize - 1), pos.snd - (ySize - 1));
	}

	/**
	 *
	 * Creates a rectangle defined by two corners (provided by their
	 * coordinates).
	 *
	 * @param x1
	 * 		x-coordinate of the first corner
	 * @param y1
	 * 		y-coordinate of the first corner
	 * @param x2
	 * 		x-coordinate of the second corner
	 * @param y2
	 * 		y-coordinate of the second corner .
	 * 		<p>
	 * 		Internally, the class stores the coordinates of the rectangle by
	 * 		computing the lower left and upper right corners. The provided
	 * 		corners
	 * 		need not be the lower left or upper right corner. This also means
	 * 		that
	 * 		the toString() method may produce unexpected output.
	 * 		<p>
	 * 		The two corners of the rectangle may be identical.
	 */
	public Rectangle(final int x1, final int y1, final int x2, final int y2) {


		int minX = Math.min(x1, x2);
		int minY = Math.min(y1, y2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);

		lowerLeft = new Point(minX, minY);
		upperRight = new Point(maxX, maxY);

	}

	/**
	 * Checks whether two rectangles are overlapping.
	 * @param rec1 The first rectangle
	 * @param rec2 The second rectangle
	 * @return true if and only if the two rectangles overlap.
	 */
	public static boolean overlapping(
			final Rectangle rec1,
			final Rectangle rec2) {

		boolean beside1 = rec1.lowerLeft.fst > rec2.upperRight.fst;
		boolean beside2 = rec2.lowerLeft.fst > rec1.lowerLeft.fst;

		boolean above1 = rec1.upperRight.snd < rec2.lowerLeft.snd;
		boolean above2 = rec1.upperRight.snd < rec1.lowerLeft.snd;

		return !(beside1 && beside2 && above1 && above2);
	}

	/**
	 * Checks whether a given point is within the boundaries of the rectangle.
	 *
	 * @param p
	 * 		Point to check for
	 * @return true if p is within the rectangle, false otherwise
	 *
	 */
	public boolean contains(final Point p) {
		return contains(p.fst, p.snd);
	}


	/**
	 * Checks whether a given point is within the boundaries of the rectangle.
	 *
	 * @param x
	 * 		x-coordinate of Point to check for
	 * @param y
	 * 		y-coordinate of Point to check for
	 * @return true if (x,y) is within the rectangle, false otherwise
	 *
	 */
	public boolean contains(final int x, final int y) {

		return lowerLeft.fst <= x && x <= upperRight.fst &&
			   lowerLeft.snd <= y && y <= upperRight.snd;
	}


	/**
	 * Computes the center of the rectangle.
	 * <p>
	 * The computation is done in coordinates using integer arithmetic. This
	 * means that the usual rounding rules apply. The result will be a point
	 * within the Rectangle.
	 *
	 * @return Point which is in the center of the rectangle.
	 */
	public Point center() {
		int centerX = (lowerLeft.fst + upperRight.fst) / 2;
		int centerY = (lowerLeft.snd + upperRight.snd) / 2;
		return new Point(centerX, centerY);
	}

    /**
	 * Computes the center of the rectangle using floating point arithmetic.
	 *
	 * The result is a pair of floats. While the result lies within the rectangle
	 * it is not necessarily a valid integer coordinate of it.
	 *
	 * @return Point in the center of the rectangle using floating point arithmetic.
	 */
	public Pair<Float, Float> centerFloat() {
		float centerX =
				(lowerLeft.fst.floatValue() + upperRight.fst.floatValue()) /
				2f;
		float centerY =
				(lowerLeft.snd.floatValue() + upperRight.snd.floatValue()) /
				2f;

		return new Pair<>(centerX, centerY);
	}


	/**
	 * @return List of the points of this rectangle.
	 */
	public List<Point> positions() {

		ArrayList<Point> result = new ArrayList<>();

		for (int x = lowerLeft.fst; x <= upperRight.fst; x++) {
			for (int y = lowerLeft.snd; y <= upperRight.snd; y++) {
				result.add(new Point(x, y));
			}
		}

		return result;
	}

	/**
	 * Checks whether the rectangle contains a single point only.
	 *
	 * @return true if the Rectangle is a single point only
	 */
	public boolean isPoint() {
		return lowerLeft.equals(upperRight);
	}

	/**
	 * @return the Point of the rectangle if the rectangle contains one point
	 * only, null otherwise.
	 */
	public Point getPoint() {
		if (isPoint()) {
			return lowerLeft;
		} else {
			return null;
		}
	}

	/**
	 * Checks two rectangles for adjacency.
	 *
	 * @param r1
	 * 		The first rectangle
	 * @param r2
	 * 		The second rectangle
	 * @return true if the rectangles are adjacent, false otherwise.
	 * @warn Currently only single point rectangles are supported
	 */
	public static boolean adjacent(final Rectangle r1, final Rectangle r2) {

		if (r1 == null || r2 == null) {
			return false;
		}

		boolean isPoint1 = r1.isPoint();
		boolean isPoint2 = r2.isPoint();

		if (isPoint1 && isPoint2) {
			return Point.adjacent(r1.getPoint(), r2.getPoint());
		}

		/*
		Both rectangles are "real" rectangles. The algorithm now is to create
		a rectangle that is bigger than the other by one cell and then see
		whether these rectangles overlap.
		 */
		if (!isPoint1 && !isPoint2) {
			// use new Point to create a deep copy
			Point ll = new Point(r1.lowerLeft.fst - 1, r1.lowerLeft.snd - 1);
			Point ur = new Point(r1.upperRight.fst + 1, r1.upperRight.snd + 1);
			Rectangle biggerRect = new Rectangle(ll, ur);


			return Rectangle.overlapping(biggerRect, r2);
		}

		/*
		Now we know that at least Rectangle is a point and can act accordingly
		 */
		Point p = r1.getPoint();
		Rectangle rec = r2;
		if (r2.isPoint()) {
			p = r2.getPoint();
			rec = r1;
		}

		return rec.contains(p);
	}


	/**
	 * Computes the size of the rectangle.
	 *
	 * @return Point of type (width,height).
	 */
	public Point size() {
		return new Point(
				(upperRight.fst - lowerLeft.fst) + 1,
				(upperRight.snd - lowerLeft.snd) + 1
		);
	}

	/**
	 * Returns the list of corners of the rectangle.
	 * <p>
	 * Note that the list contains either four points (i.e. a "real"
	 * rectangle")
	 * or only one (the rectangle actually is a point). No extra test for
	 * 1xn or
	 * nx1 rectangles is performed.
	 *
	 * @return The corners of the rectangle.
	 */
	public List<Point> corners() {
		ArrayList<Point> cns = new ArrayList<>();
		cns.add(lowerLeft);
		if (!isPoint()) {
			cns.add(upperLeft());
			cns.add(upperRight);
			cns.add(lowerRight());
		}
		return cns;
	}

	/**
	 * Computes the upper left corner of the rectangle.
	 *
	 * @return The upper left corner of the rectangle.
	 */
	public Point upperLeft() {
		return new Point(lowerLeft.fst,upperRight.snd);
	}

	/**
	 * Computes the lower right corner of the rectangle.
	 *
	 * @return The lower right corner of the rectangle.
	 */
	public Point lowerRight() {
		return new Point(upperRight.fst, lowerLeft.snd);
	}

	/**
	 * Checks if a point is adjacent to this rectangle.
	 * <p>
	 * The check is easily done be extending the rectangle and checking whether
	 * the point is within that rectangle.
	 *
	 * The parameter size determins how much the rectangle gets extended. This
	 * is of interest when working with meda chips.
	 *
	 * @param p
	 * 		The point to check for adjacency.
	 * 	@param size The size of the adjacency to be considered.
	 * @return True if the point is adjacent to the rectangle.
	 */
	public boolean adjacent(final Point p, final int size) {
		Rectangle biggerRect =
				new Rectangle(lowerLeft.fst - size, lowerLeft.snd - size,
							  upperRight.fst + size, upperRight.snd + size);

		return biggerRect.contains(p);
	}


	/**
	 * Checks if a point is adjacent to this rectangle using a distance of 1.
	 *
	 * @param p The point to check for adjacency.
	 * @return True if the point is adjacent to the rectangle, false otherwise.
	 */
	public boolean adjacent(final Point p) {
		return adjacent(p, 1);
	}

	/**
	 * @return String of the form "Rect[(lowerLeft.x,lowerLeft.y)
	 * (upperRight.x,upperRight.y)]"
	 */
	@Override
	public String toString() {
		return "Rect[" + lowerLeft + " " + upperRight + "]";
	}


}
