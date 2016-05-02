package de.bioviz.structures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumSet;

public class BiochipField {
	public final Point pos;
	private Range blockage;
	private Detector detector;
	public int usage;
	public Pin pin;
	public ActuationVector actVec;

	public ArrayList<Integer> source_ids = new ArrayList<Integer>();
	public ArrayList<Integer> target_ids = new ArrayList<Integer>();
	public ArrayList<Mixer> mixers = new ArrayList<Mixer>();
	static private Logger logger = LoggerFactory.getLogger(BiochipField.class);

	Biochip parent;

	public int x() {
		return pos.fst;
	}

	public int y() {
		return pos.snd;
	}


	public void setDetector(Detector det) {
		detector = det;
	}

	public Detector getDetector() {
		return detector;
	}

	/**
	 * Checks whether this field is the destination of a net.
	 *
	 * @return true if the field is the destination of a net.
	 */
	public boolean isTarget() {
		return !target_ids.isEmpty();
	}

	/**
	 * Checks whether this field is the source of a net.
	 *
	 * @return true if the field is the source of a net.
	 */
	public boolean isSource() {
		return !source_ids.isEmpty();
	}

	/**
	 * Checks whether this field has any mixers.
	 *
	 * @return true if the field has mixers.
	 */
	public boolean hasMixers() {
		return !mixers.isEmpty();
	}


	// ############################################################################################################
	/**
	 * Sets the time, this field is blocked.
	 *
	 * @param blockage
	 * 		The {@link Range} describing the time the field is blocked.
	 */
	public void attachBlockage(Range blockage) {
		this.blockage = blockage;
	}

	/**
	 * Checks whether the field is blocked at the specified time step.
	 *
	 * @param timeStep The time step to check.
	 * @return true if the field is blocked at timeStep, false otherwise.
	 */
	public boolean isBlocked(int timeStep) {

		if (isPotentiallyBlocked()) {
			return blockage.inRange(timeStep);
		}
		else {
			return false;
		}
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
	public boolean isActuated(int timeStep) {

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

		for (Mixer m : mixers) {
			if (m.positions.contains(pos) && m.timing.inRange(timeStep)) {
				act = Actuation.ON;
			}
		}

		return act == Actuation.ON;
	}


	// end of TODO
	// ############################################################################################################
	public BiochipField(int x, int y, Biochip parent) {
		this.pos = new Point(x, y);
		this.parent = parent;
	}

	public BiochipField(Point p, Biochip parent) {
		this.pos = p;
		this.parent = parent;
	}

}
