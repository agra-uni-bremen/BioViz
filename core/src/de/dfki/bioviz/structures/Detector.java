package de.dfki.bioviz.structures;

/**
 * Created by keszocze on 30.07.15.
 */
public class Detector {
	private int fluidType;
	private int duration;
	private Point position;

	public Point position() {
		return new Point(position);
	}

	public void setFluidType(int fluidType) {
		this.fluidType = fluidType;
	}

	public int getDuration() {

		return duration;
	}

	public Detector(Point position, int duration, int fluidType) {
		this.position = position;
		this.duration = duration;
		this.fluidType = fluidType;
	}
}
