package de.bioviz.structures;

/**
 * Base class for a resource on the biochip.
 * <p>
 * These resources are: mixer, detector, heater, magnet.
 * <p>
 * They can span multiple cells or just a single one.
 */
public class Resource {
	/**
	 * The position of the resource on the chip.
	 */
	public final Rectangle position;

	/**
	 * The type of the resource.
	 *
	 * See the enum below to see what kind of resources are available.
	 */
	public final ResourceType type;

	/**
	 * The supported resources.
	 */
	public enum ResourceType {
		mixer, detector, heater, magnet
	}

	/**
	 * Creates a resource of given type at specified position.
	 * @param position
	 * @param type
	 */
	public Resource(Rectangle position, ResourceType type) {
		this.position = position;
		this.type = type;
	}


}
