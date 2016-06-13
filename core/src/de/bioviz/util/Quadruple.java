package de.bioviz.util;

/**
 * A very simple quadruple class
 * <p>
 * This class is necessary as, for no good reason at all, Java does not come
 * with any means of creating tuples.
 *
 * @param <F>
 * 		Type for the first entry of the quadruple
 * @param <S>
 * 		Type for the second entry of the quadruple
 * @param <T>
 * 		Type for the third entry of the quadruple
 * @param <L>
 * 		Type for the fourth entry of the quadruple
 * @author Oliver Keszocze
 */
public class Quadruple<F, S, T, L> {
	public final F fst;
	public final S snd;
	public final T thd;
	public final L fth;

	/**
	 * @param first
	 * 		First entry of the quadruple
	 * @param second
	 * 		Second entry of the quadruple
	 * @param third
	 * 		Third entry of the quadruple
	 * @param fourth
	 * 		Fourth entry of the quadruple
	 */
	public Quadruple(final F first, final S second, final T third, final L
			fourth) {
		fst = first;
		snd = second;
		thd = third;
		fth = fourth;
	}

	/**
	 * @param that
	 * 		The object to compare against
	 * @return True iff o is logically equivalent to the object
	 * @brief Checks equality of the Quadruple with another object.
	 * <p>
	 * An object is treated as equals if it also is a quadruple and the values
	 * in the positions are equal as determined by their equals() method.
	 */
	@Override
	public boolean equals(final Object that) {

		if (this == that) {
			return true;
		}

		if (that == null || getClass() != that.getClass()) {
			return false;
		}


		Quadruple thatQ = (Quadruple) that;

		return fst.equals(thatQ.fst) && snd.equals(thatQ.snd) &&
			   thd.equals(thatQ.thd) && fth.equals(thatQ.fth);
	}

	@Override
	/**
	 * @return The sum of the hashcodes
	 */
	public int hashCode() {
		return fst.hashCode() + snd.hashCode() + thd.hashCode() +
			   fth.hashCode();
	}


	@Override
	/**
	 * @return String representing the Pair of the form '(' + first + ',' +
	 * second
	 * + ',' + third + ',' fourth ')'
	 */
	public String toString() {
		return "(" + fst + "," + snd + "," + thd + "," + fth + ")";
	}

}
