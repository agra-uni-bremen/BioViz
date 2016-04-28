package de.bioviz.structures;

/**
 * Class that stores violations of fluidic constraints
 *
 * @author Oliver Keszocze
 */
public class FluidicConstraintViolation {


	public final Droplet d1;
	public final Droplet d2;
	public final BiochipField f1;
	public final BiochipField f2;
	public final int timestep;

	public FluidicConstraintViolation(Droplet d1, BiochipField f1, Droplet d2,
									  BiochipField f2, int timestep) {
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
	public boolean containsField(BiochipField field) {
		return f1.equals(field) || f2.equals(field);
	}

	public String toString() {
		return "At timestep " + timestep + " droplet " + d1.getID() +
			   " is too close to droplet " + d2.getID() + " : " + f1.pos +
		" <-> " + f2.pos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FluidicConstraintViolation other = (FluidicConstraintViolation)o;

		boolean direct1 = f1.equals(other.f1) && d1.equals(other.d1);
		boolean direct2 = f2.equals(other.f2) && d2.equals(other.d2);
		boolean direct = direct1 && direct2;

		boolean switched1 = f1.equals(other.f2) && d1.equals(other.d2);
		boolean switched2 = f2.equals(other.f1) && d2.equals(other.d1);
		boolean switched = switched1 && switched2;

		System.out.println("direct1: " + direct1 + " direct2 " + direct2);
		System.out.println("switched1: " + switched1 + " switched " + switched2);

		return (timestep == other.timestep) && (direct || switched);

	}

	@Override
	/**
	 * Pretty simple and bad hashCode implementation.
	 */
	public int hashCode() {
		return timestep+f1.x()+f1.y()+f2.x()+f2.y();
	}
}
