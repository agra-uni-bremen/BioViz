package de.dfki.bioviz.structures;

/**
 * Created by keszocze on 28.07.15.
 */
public class Range {
	public static final int DONTCARE = 0;

	private int begin;
	private int end;

	public Range(final int begin,final int end) {

		this.begin = (begin<1) ? DONTCARE:begin;
		this.end = (end<1) ? DONTCARE: begin;
	}

	public boolean inRange(final int i) {
		if (i <1) {
			return false;
		}
		else {
			return (i >= begin && (end == DONTCARE || i <=end));
		}
	}
}
