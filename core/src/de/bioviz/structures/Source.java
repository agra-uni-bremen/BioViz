package de.bioviz.structures;

/**
 * <p>
 * Class representing a source of a net.
 * <p>
 * A source is the starting point of a droplet in a routing problem. Therefore,
 * this class stores the initial position of that droplet, the point in time the
 * droplet appears at that position and its id. We do not store an actual {@link
 * Droplet} object!
 * <p>
 * Please note that this 'source' has nothing to do with releasing a droplet
 * from a dispenser onto a chip!
 *
 * @author Oliver Keszocze
 */
public class Source {

	/**
	 * The initial position of the droplet.
	 */
	public final Point startPosition;

	/**
	 * The droplet's unique ID.
	 */
	public final int dropletID;

	/**
	 * The time step the droplet first appears at {@link Source#startPosition}.
	 */
	public final int spawnTime;

	/**
	 * Creates a source that uses the first time step as the starting time.
	 * @param dropletID Unique ID of the droplet that is to be routed
	 * @param pos The initial position of the droplet
	 */
	public Source(final int dropletID, final Point pos) {
		this(dropletID, pos, 1);
	}

	/**
	 * Creates a source with a defined starting time.
	 * @param dropletID Unique ID of the droplet that is to be routed
	 * @param pos The initial position of the droplet
	 * @param spawnTime The time step the droplet appears on the chip
	 */
	public Source(final int dropletID, final Point pos, final int spawnTime) {
		this.dropletID = dropletID;
		this.startPosition = pos;
		this.spawnTime = spawnTime;
	}
}
