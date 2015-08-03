package de.dfki.bioviz.structures;

public class BiochipField {
	public boolean isSink = false;
	public boolean isDispenser = false;
	public final Point pos;
	private Range blockage;
	private Detector detector;
	public Pin pin;
	public ActuationVector actVec;


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



	
	public boolean isPotentiallyBlocked() {
		return !(blockage == null);
	}

	public void setSink(Direction removeFrom) {
		isDispenser=false;
		isSink=true;
		fluidID=0;
		direction = removeFrom;
	}

	public void setDispenser(int fluidID, Direction dispenseTo) {
		isSink=false;
		isDispenser=true;
		this.fluidID=fluidID;
		direction=dispenseTo;
	}

	public BiochipField(Point pos, int fluidID, Direction dispenseTo) {
		this.pos=pos;
		setDispenser(fluidID,dispenseTo);
	}

	public BiochipField(Point pos, Direction removeFrom) {
		this.pos=pos;
		setSink(removeFrom);

	}
	// end of TODO
	// ############################################################################################################

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

	public BiochipField(int x, int y) {
		this.pos = new Point(x,y);
	}

	public BiochipField(Point p) {
		this.pos = p;
	}


}
