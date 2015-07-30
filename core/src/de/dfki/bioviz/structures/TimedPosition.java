package de.dfki.bioviz.structures;

/**
 * Created by keszocze on 30.07.15.
 */
/**
 *
 * @author jannis
 *
 * @note This class has a natural ordering that is inconsistent with equals.
 */
class TimedPosition implements Comparable<TimedPosition> {
	long time;
	int x, y;

	public long getTime() {
		return time;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public TimedPosition(long t, int x, int y) {
		this.time = t;
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(TimedPosition t) {
		return Long.compare(this.time, t.time);
	}
}