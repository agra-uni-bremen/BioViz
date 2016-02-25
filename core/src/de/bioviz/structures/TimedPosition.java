package de.bioviz.structures;

/**
 * @author jannis
 * @note This class has a natural ordering that is inconsistent with equals.
 */
class TimedPosition implements Comparable<TimedPosition> {
	/** The timing of this position. */
	long time;

	/** x-coordinate of the position. */
	int x;

	/** y-cordinate of the position. */
	int y;

	/**
	 * @param t Timing
	 * @param x x-coordinate of the position
	 * @param y y-coorinate of the position
	 */
	public TimedPosition(final long t, final int x, final int y) {
		this.time = t;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return The time of this position
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return The x-coordinate of this position
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return The y-coordinate of this position
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return The position as a Point
	 */
	public Point getPos() {
		return new Point(x, y);
	}

	@Override
	/**
	 * Compares two timed positions by their time.
	 *
	 * The actual position of the timed position is *not* considered!
	 *
	 * @return Long.compar(this.time, t.time)
	 */
	public int compareTo(final TimedPosition t) {
		return Long.compare(this.time, t.time);
	}
}
