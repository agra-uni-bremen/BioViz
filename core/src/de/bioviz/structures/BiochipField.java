package de.bioviz.structures;

import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class BiochipField {
	public boolean isSink = false;
	public boolean isDispenser = false;
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

	BioViz parent;

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


	// ############################################################################################################
	// TODO put Information about sink/dispenser in
	// if the field is either a sink or a dispenser this field stores the
	// information from which
	// field the fluid is removed from or dispensed to
	Direction direction = null;

	// if the field is a dispenser, this variable stores the fluid type that
	// is dispensed
	public int fluidID = 0;


	public void attachBlockage(Range blockage) {
		this.blockage = blockage;
	}

	public boolean isBlocked(int timeStep) {
		if (blockage == null) {
			return false;
		}
		else {
			return blockage.inRange(timeStep);
		}
	}

	public boolean isActuated(int timeStep) {

		Biochip circ = parent.currentCircuit.data;
		Actuation act = Actuation.OFF;

		// TODO document that pin actuations win over cell actuations

		if (pin != null && !circ.pinActuations.isEmpty()) {
			logger.trace("circ.pinActuations.isEmpty: {}",
						 circ.pinActuations.isEmpty());
			logger.trace("timeStep: {} actuationVector {}", timeStep,
						 circ.pinActuations.get(pin.pinID));
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

		for (Mixer m: mixers) {
			if (m.positions.contains(pos) && m.timing.inRange(timeStep)) {
				act = Actuation.ON;
			}
		}

		return act == Actuation.ON;
	}

	public boolean isPotentiallyBlocked() {
		return !(blockage == null);
	}

	public void setSink(Direction removeTo) {
		isDispenser = false;
		isSink = true;
		fluidID = 0;
		direction = removeTo;
	}

	public void setDispenser(int fluidID, Direction dispenseFrom) {
		isSink = false;
		isDispenser = true;
		this.fluidID = fluidID;
		direction = dispenseFrom;
	}

	public BiochipField(Point pos, int fluidID, Direction dispenseFrom, BioViz
			parent) {
		this.pos = pos;
		setDispenser(fluidID, dispenseFrom);
		this.parent = parent;
	}

	public BiochipField(Point pos, Direction removeTo, BioViz parent) {
		this.pos = pos;
		setSink(removeTo);
		this.parent = parent;
	}

	// end of TODO
	// ############################################################################################################
	public BiochipField(int x, int y, BioViz parent) {
		this.pos = new Point(x, y);
		this.parent = parent;
	}

	public BiochipField(Point p, BioViz parent) {
		this.pos = p;
		this.parent = parent;
	}


}
