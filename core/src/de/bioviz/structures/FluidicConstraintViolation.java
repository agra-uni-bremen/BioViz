package de.bioviz.structures;

/**
 * Class that stores violations of fluidic constraints.
 *
 * @author Oliver Keszocze
 */
public class FluidicConstraintViolation {


	/**
	 * The first droplet that is involved in the violation.
	 */
	public final Droplet d1;

	/**
	 * The second droplet that is involved in the violation.
	 */
	public final Droplet d2;

	/**
	 * The field the first droplet is on when violating the constraints.
	 */
	public final BiochipField f1;

	/**
	 * The field the first droplet is on when violating the constraints.
	 */
	public final BiochipField f2;

	/**
	 * The time step the violation takes place.
	 */
	public final int timestep;

	/**
	 * Creates a constraint violation by directly setting all internal
	 * variables.
	 *
	 * @param d1
	 * 		The first droplet of the violation.
	 * @param f1
	 * 		The position of the first droplet.
	 * @param d2
	 * 		The second droplet of the violation.
	 * @param f2
	 * 		The position of the second droplet.
	 * @param timestep
	 * 		The timestep the violation occurs.
	 */
	public FluidicConstraintViolation(final Droplet d1,
									  final BiochipField f1,
									  final Droplet d2,
									  final BiochipField f2,
									  final int timestep) {
		this.d1 = d1;
		this.d2 = d2;
		this.f1 = f1;
		this.f2 = f2;
		this.timestep = timestep;

	}

	/**
	 * Checks whether a given field is involved in this violation of the
	 * fluidic
	 * constraints.
	 *
	 * @param field
	 * 		the field to test.
	 * @return true if the field is involved in this violation, false
	 * otherwise.
	 */
	public boolean containsField(final BiochipField field) {
		return f1.equals(field) || f2.equals(field);
	}

	/**
	 * @return String representation of the violation.
	 */
	public String toString() {
		return "At timestep " + timestep + " droplet " + d1.getID() +
			   " is too close to droplet " + d2.getID() + " : " + f1.pos +
			   " <-> " + f2.pos;
	}

	/**
	 * Checks whether some object (ideally a violation) is equal to the given
	 * violation.
	 *
	 * @param o
	 * 		The other violation.
	 * @return true if the violations are equal, false otherwise.
	 */
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FluidicConstraintViolation other = (FluidicConstraintViolation) o;

		boolean direct1 = f1.equals(other.f1) && d1.equals(other.d1);
		boolean direct2 = f2.equals(other.f2) && d2.equals(other.d2);
		boolean direct = direct1 && direct2;

		boolean switched1 = f1.equals(other.f2) && d1.equals(other.d2);
		boolean switched2 = f2.equals(other.f1) && d2.equals(other.d1);
		boolean switched = switched1 && switched2;

		return (timestep == other.timestep) && (direct || switched);

	}

	@Override
	/**
	 * Pretty simple and bad hashCode implementation.
	 */
	public int hashCode() {
		return timestep + f1.x() + f1.y() + f2.x() + f2.y();
	}
}
