package de.bioviz.structures;

import java.util.ArrayList;
import java.util.Map;

import static de.bioviz.structures.Resource.ResourceType.detector;


/**
 * The abstraction of a field on a biochip.
 * <p>
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
	 * The pin that is assigned to this field.
	 * <p>
	 * Note that this variable might be NULL as there might be BioGram files
	 * that do not specify a pin assignment.
	 */
	public Pin pin;

	/**
	 * The actuation vector of this field.
	 * <p>
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
	 * List of annotations for this field.
	 */
	public ArrayList<AreaAnnotation> areaAnnotations = new ArrayList<>();


	/**
	 * The biochip this field belongs to.
	 */
	private Biochip parent;

	/**
	 * The time range in which the field is blocked.
	 * <p>
	 * Note that a field can only be blocked for a single range. This means
	 * that
	 * if a field has been unblocked it will never be blocked again later on.
	 */
	private Range blockage;

	/**
	 * How often the field was actuated.
	 */
	private Integer usage = null;

	/**
	 * Stores the resource of this field.
	 * <p>
	 * This variable might be null as not every field must have a rource.
	 */
	private Resource resource;

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
	 * Checks whether this field has any area annotations.
	 * @return true if the field as area annotations, false otherwise.
	 */
	public boolean hasAnnotations() {
		return !areaAnnotations.isEmpty();
	}


	/**
	 * Sets the detector of this field.
	 * <p>
	 * Note that we do not check whether a detector is added to a field that is
	 * blocked and vice versa. Also no check for other resources is performed.
	 *
	 * @param det
	 * 		The detector that is placed at this field.
	 */
	public void setDetector(final Detector det) {
		resource = det;
	}

	public void setHeater(final Heater heater) { resource = heater; }

	public void setMagnet(final Magnet magnet) { resource = magnet;}

	/**
	 * Checks whether there is a resources at this field.
	 *
	 * It does only check that any resource is present and not for the type of
	 * resource.
	 *
	 * @return true if there is a resource at this field.
	 */
	public boolean hasResource() { return resource != null; }

	/**
	 * Returns the detector of this field.
	 * <p>
	 * Note that the result may by NULL as not every field must have a
	 * detector.
	 *
	 * @return The detector, if present, NULL otherwise
	 */
	public Detector getDetector() {
		if (resource == null  ||
			resource.type != Resource.ResourceType.detector) {
			return null;
		}
		return (Detector)resource;
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

		Map<Integer,ActuationVector> actVecs = circ.pinActuations;
		if (pin != null && !actVecs.isEmpty()) {
			ActuationVector vec = actVecs.get(pin.pinID);
			if (vec != null) {
				act = vec.get(timeStep - 1);
			}
		} else if (actVec != null && !actVec.isEmpty()) {
			act = actVec.get(timeStep - 1);
		} else {
			if (circ.dropletOnPosition(pos, timeStep)) {
				act = Actuation.ON;
			}
		}

		for (final Mixer m : mixers) {
			if (m.position.contains(pos) && m.timing.inRange(timeStep)) {
				act = Actuation.ON;
			}
		}

		return act == Actuation.ON;
	}

	/**
	 * Returns the usage of this field.
	 * <p>
	 * Note that the value will be null if the usage has not been calculated
	 * before calling this method.
	 *
	 * @return The usage of this field or null if the usage wasn't computed.
	 */
	public int getUsage() {
		return usage;
	}

	/**
	 * Computes the usage of the field.
	 * <p>
	 * The usage is considered up to a specified position in time.
	 *
	 * @param maxT
	 * 		The upper bound for the time steps to consider when computing the
	 * 		usage.
	 * @return The usage of this field up to time step T.
	 */
	int computeUsage(int maxT) {
		usage = 0;
		for (int t = 1; t <= maxT; t++) {
			if (isActuated(t)) {
				usage++;
			}
		}
		return usage;
	}




}
