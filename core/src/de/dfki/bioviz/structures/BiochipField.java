package de.dfki.bioviz.structures;

public class BiochipField {
	public boolean isEnabled = false;
	public boolean isSink = false;
	public boolean isDispenser = false;
	public int x, y;
	private Range blockage;
	public int usage;



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

	public void setSink(Direction removeFrom) {
		isDispenser=false;
		isEnabled=true;
		isSink=true;
		fluidID=0;
		direction = removeFrom;
	}

	public void setDispenser(int fluidID, Direction dispenseTo) {
		isSink=false;
		isEnabled=true;
		isDispenser=true;
		this.fluidID=fluidID;
		direction=dispenseTo;
	}

	public BiochipField(int x, int y, int fluidID, Direction dispenseTo) {
		this.x=x;
		this.y=y;
		setDispenser(fluidID,dispenseTo);
	}

	public BiochipField(int x, int y, Direction removeFrom) {
		this.x=x;
		this.y=y;
		setSink(removeFrom);

	}

	public BiochipField(int x, int y) {
		this.x = x;
		this.y = y;
	}


}
