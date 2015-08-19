package de.bioviz.structures;

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


	public int x() { return pos.first;}
	public int y() { return pos.second;}


	public void setDetector(Detector det) {
		detector = det;
	}

	public Detector getDetector() {
		return detector;
	}


	// ############################################################################################################
	// TODO put Information about sink/dispenser in
	// if the field is either a sink or a dispenser this field stores the information from which
	// field the fluid is removed from or dispensed to
	Direction direction = null;

	// if the field is a dispenser, this variable stores the fluid type that is dispensed
	public int fluidID = 0;


	public void attachBlockage(Range blockage) {
		this.blockage=blockage;
	}

	public boolean isBlocked(int timeStep) {
		if (blockage == null) {
			return false;
		}
		else {
			return blockage.inRange(timeStep);
		}
	}
	
	public boolean isPotentiallyBlocked() {
		return !(blockage == null);
	}

	public void setSink(Direction removeTo) {
		isDispenser=false;
		isSink=true;
		fluidID=0;
		direction = removeTo;
	}

	public void setDispenser(int fluidID, Direction dispenseFrom) {
		isSink=false;
		isDispenser=true;
		this.fluidID=fluidID;
		direction=dispenseFrom;
	}

	public BiochipField(Point pos, int fluidID, Direction dispenseFrom) {
		this.pos=pos;
		setDispenser(fluidID,dispenseFrom);
	}

	public BiochipField(Point pos, Direction removeTo) {
		this.pos=pos;
		setSink(removeTo);

	}
	// end of TODO
	// ############################################################################################################
	public BiochipField(int x, int y) {
		this.pos = new Point(x,y);
	}

	public BiochipField(Point p) {
		this.pos = p;
	}


}
