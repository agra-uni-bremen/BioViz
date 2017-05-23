package de.bioviz.structures;

/**
 * Models a detector on a biochip.
 * <p>
 * The attributes of the detector are its position on the grid, the fluid type
 * it can work on and the time it needs for performing the operation.
 * <p>
 * Created by keszocze on 30.07.15.
 *
 * @author Oliver Keszocze
 */
public class Detector extends Resource {
	/**
	 * The fluid type the detector works on.
	 */
	private int fluidType;

	/**
	 * The time the detector needs to perform its work.
	 */
	private int completionTime;


	/**
	 * @param position
	 * 		Position of the detector on the biochip
	 * @param completionTime
	 * 		The time the detector needs to perform its operation
	 * @param fluidType
	 * 		The fluid type the detector operates on
	 */
	public Detector(final Rectangle position,
					final int completionTime,
					final int fluidType) {
		super(position, ResourceType.detector);

		this.completionTime = completionTime;
		this.fluidType = fluidType;
	}


	/**
	 * @return The detector's position on the biochip
	 */
	public Rectangle position() {
		return new Rectangle(position);
	}

	/**
	 * Sets the fluid type the detector can work on.
	 *
	 * @param fluidType
	 * 		The fluid type the detector can work on
	 */
	public void setFluidType(final int fluidType) {
		this.fluidType = fluidType;
	}

	/**
	 * @return The fluid type the detector works on
	 */
	public int getFluidType() {
		return fluidType;
	}

	/**
	 * @return The time it takes for the operation to complete
	 */
	public int getCompletionTime() {

		return completionTime;
	}


}
