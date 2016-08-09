package de.bioviz.structures;

/**
 * Range of integers with membership tests.
 *
 * @author Oliver Keszocze
 */
public class Range {

	/**
	 * Don't care value for the range.
	 */
	public static final int DONTCARE = 0;

	/**
	 * The begin of the range.
	 */
	private final int begin;

	/**
	 * The end of the range.
	 */
	public final int end;

	/**
	 * Creates a new range given the start and end.
	 * <p>
	 * Integer values < 1 are treated as don't care.
	 *
	 * @param begin
	 * 		The beginning of the range
	 * @param end
	 * 		The end of the range
	 */
	public Range(final int begin, final int end) {

		this.begin = (begin < 1) ? DONTCARE : begin;
		this.end = (end < 1) ? DONTCARE : end;
	}

	/**
	 * Check whether an integer is in the range.
	 *
	 * @param i
	 * 		The integer to test
	 * @return true if th the integer is within the range, false otherwise
	 */
	public boolean inRange(final int i) {
		return i >= 0 && i >= begin && (end == DONTCARE || i <= end);
	}


	/**
	 * Creates a String representation of the range.
	 * <p>
	 * Don't care values are represented by '*'.
	 *
	 * @return "(" [begin] "," [end] ")"
	 */
	@Override
	public String toString() {
		String fst = (begin == DONTCARE) ? "*" : String.valueOf(begin);
		String snd = (end == DONTCARE) ? "*" : String.valueOf(end);

		return "(" + fst + "," + snd + ")";
	}
}
