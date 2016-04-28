package de.bioviz.structures;

/**
 * Created by keszocze on 28.04.16.
 */
public class FluidicConstraintViolation {


	public final Droplet d1;
	public final Droplet d2;
	public final BiochipField f1;
	public final BiochipField f2;
	public final int timestep;

	public FluidicConstraintViolation(Droplet d1, BiochipField f1, Droplet d2,
									  BiochipField f2, int timestep) {
		this.d1=d1;
		this.d2=d2;
		this.f1=f1;
		this.f2=f2;
		this.timestep=timestep;

	}

	public boolean containsField(BiochipField field) {
		return f1.equals(field) || f2.equals(field);
	}
}
