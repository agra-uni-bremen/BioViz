package de.bioviz.structures;

import java.util.ArrayList;

/**
 * The abstraction of a field on a biochip.
 *
 * This class stores the position and whether a detector is present as well as
 * some meta information such as e.g. the actuation vector and the usage count.
 *
 * @author Oliver Keszocze
 */
public class BiochipField {


	/**
	 * The field's position.
	 */
	public final Point pos;

	/**
	 * How often the field was actuated.
	 */
	public int usage;

	/**
	 * The pin that is assigned to this field.
	 *
	 * Note that this variable might be NULL as there might be BioGram files
	 * that do not specify a pin assignment.
	 */
	public Pin pin;

	/**
	 * The actuation vector of this field.
	 *
	 * Note that this variable might be NULL.
	 */
	public ActuationVector actVec;

	/**
	 * List of droplet IDs that belong to a net starting at this field.
	 */
	public ArrayList<Integer> sourceIDs = new ArrayList<>();

	/**
	 * List of droplet IDs that belong to a net ending at this field.
	 */
	public ArrayList<Integer> targetIDs = new ArrayList<>();

	/**
	 * List of mixing operations performed on top of this field.
	 */
	public ArrayList<Mixer> mixers = new ArrayList<>();


	/**
	 * The biochip this field belongs to.
	 */
	private Biochip parent;

	/**
	 *
	 */
	private Range blockage;

	/**
	 * Stores the detector that is present at this field.
	 * <p>
	 * This variable might be null as not every field must have a detecting
	 * device.
	 */
	private Detector detector;

	/**
	 * Creates an empty field at given position on the specified biochip.
	 *
	 * @param p
	 * 		The position of the field.
	 * @param parent
	 * 		The biochip this field belongs to.
	 */
	public BiochipField(final Point p, final Biochip parent) {
		this.pos = p;
		this.parent = parent;
	}

	/**
	 * Returns the x position of this field.
	 *
	 * @return The x position of this field
	 */
	public int x() {
		return pos.fst;
	}

	/**
	 * Returns the y position of this field.
	 *
	 * @return The y position of this field
	 */
	public int y() {
		return pos.snd;
	}


	/**
	 * Sets the detector of this field.
	 * <p>
	 * Note that we do not check whether a detector is added to a field that is
	 * blocked and vice versa.
	 *
	 * @param det
	 * 		The detector that is placed at this field.
	 */
	public void setDetector(final Detector det) {
		detector = det;
	}

	/**
	 * Returns the detector of this field.
	 * <p>
	 * Note that the result may by NULL as not every field must have a
	 * detector.
	 *
	 * @return The detector, if present, NULL otherwise
	 */
	public Detector getDetector() {
		return detector;
	}

	/**
	 * Checks whether this field is the destination of a net.
	 *
	 * @return true if the field is the destination of a net.
	 */
	public boolean isTarget() {
		return !targetIDs.isEmpty();
	}

	/**
	 * Checks whether this field is the source of a net.
	 *
	 * @return true if the field is the source of a net.
	 */
	public boolean isSource() {
		return !sourceIDs.isEmpty();
	}

	/**
	 * Checks whether this field has any mixers.
	 *
	 * @return true if the field has mixers.
	 */
	public boolean hasMixers() {
		return !mixers.isEmpty();
	}


	/**
	 * Sets the time, this field is blocked.
	 *
	 * @param blockageRange
	 * 		The {@link Range} describing the time the field is blocked.
	 */
	public void attachBlockage(final Range blockageRange) {
		this.blockage = blockageRange;
	}

	/**
	 * Checks whether the field is blocked at the specified time step.
	 *
	 * @param timeStep
	 * 		The time step to check.
	 * @return true if the field is blocked at timeStep, false otherwise.
	 */
	public boolean isBlocked(final int timeStep) {

		return isPotentiallyBlocked() && blockage.inRange(timeStep);
	}

	/**
	 * Checks whether this field might be blocked at some point.
	 * <p>
	 * This method only checks whether there is a blockage associated with this
	 * field. As no time step is specified, this method only provides the
	 * information that this field may be blocked at some point in time.
	 *
	 * @return True if there is a blockage associated with this field.
	 */
	public boolean isPotentiallyBlocked() {
		return !(blockage == null);
	}


	/**
	 * Checks whether the field is actuated at the specified time step.
	 * <p>
	 * The order in which the field is checked for its actuation is as follows:
	 * 1. If there is a pin assignment, the actuation state of the pin is used.
	 * 2. If there is an actuation vector assigned to this field, its value is
	 * used. 3. If there is a droplet on top of this field, the field is
	 * regarded as being actuated.
	 * <p>
	 * Note that any of the above steps determined the actuation state of the
	 * field, the other checks are not performed. This means that if the field
	 * has an assigned pin that is never actuated, even the presence of a
	 * droplet on top of the field does not make this field actuated.
	 * <p>
	 * The exception from this rule is that the presence of a mixer always sets
	 * the status to actuated.
	 *
	 * @param timeStep
	 * 		The time step to check for actuation of this field.
	 * @return true if the field is actuated, false otherwise.
	 */
	public boolean isActuated(final int timeStep) {

		Biochip circ = parent;
		Actuation act = Actuation.OFF;

		if (pin != null && !circ.pinActuations.isEmpty()) {
			ActuationVector vec = circ.pinActuations.get(pin.pinID);
			if (vec != null) {
				act = vec.get(timeStep - 1);
			}
		}
		else if (actVec != null && !actVec.isEmpty()) {
			act = actVec.get(timeStep - 1);
		}
		else {
			if (circ.dropletOnPosition(pos, timeStep)) {
				act = Actuation.ON;
			}
		}

		for (final Mixer m : mixers) {
			if (m.positions.contains(pos) && m.timing.inRange(timeStep)) {
				act = Actuation.ON;
			}
		}

		return act == Actuation.ON;
	}

}
