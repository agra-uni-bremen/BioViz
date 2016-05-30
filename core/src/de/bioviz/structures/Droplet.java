package de.bioviz.structures;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class that contains everything that is necessary to describe a DMFB droplet.
 * This includes basic facts such as a unique ID as well as information about
 * the movement on the chip.
 *
 * @author Jannis Stoppe, Oliver Keszocze
 */
public class Droplet {

	/**
	 * The net the droplet belongs to. Note that not every droplet necessarily
	 * is part of a net as for a plain routing solution this information is not
	 * vital.
	 */
	private Net net = null;

	/**
	 * The positions the droplet occupies on the grid. The order within the
	 * vector corresponds to consecutive time steps.
	 * <p>
	 * Note that a droplet may be spawn at some point in time. Therefore, this
	 * vector has an offset which is stored in the {@link Droplet#spawnTime}
	 * variable.
	 * <p>
	 * Combined with the length of the vector, this also implicitly defines
	 * when
	 * the droplet vanishes from the chip.
	 */
	private ArrayList<Point> positions = new ArrayList<>();

	/**
	 * The unique ID of the droplet.
	 */
	private int id = 0;

	/**
	 * The time step the droplet appears on the chip.
	 * <p>
	 * This is an offset used with {@link Droplet#positions} to now when the
	 * droplet is where.
	 */
	private int spawnTime = 1;

	/**
	 * Creates an 'empty' droplet.
	 * <p>
	 * Empty means that there is no additional information besides the ID
	 * stored
	 * in this particular droplet. This means that it has no associated net or
	 * routes. Also it appears in the first time step.
	 *
	 * @param id
	 * 		The unique ID of the droplet
	 */
	public Droplet(final int id) {
		this.id = id;
	}

	/**
	 * Creates an almost 'empty' droplet.
	 * <p>
	 * Empty means that there is no additional information besides the ID
	 * stored
	 * in this particular droplet. This means that it has no associated net or
	 * routes. But the spawn time is specified.
	 *
	 * @param id
	 * 		The unique ID of the droplet
	 * @param spawnTime
	 * 		The first time step the droplet appears
	 */
	public Droplet(final int id, final int spawnTime) {
		this.id = id;
		this.spawnTime = spawnTime;
	}

	/**
	 * Checks whether this droplet belongs to a net.
	 *
	 * @return true if the droplet belongs to a net; false otherwise.
	 */
	public boolean hasNet() {
		return net != null;
	}

	/**
	 * @return The net the droplet belongs to.
	 */
	public Net getNet() {
		return net;
	}

	/**
	 * Tells the droplet which net it belongs to.
	 *
	 * @param net
	 * 		The net droplet should belong to
	 * @warning Calling this method does *not* add the droplet to any net. It
	 * merely sets a reference. The net itself has a list of IDs of droplets
	 * that belong to it. No checks for actual membership are performed. So be
	 * sure that you call this with the proper parameters!
	 */
	public void setNet(final Net net) {
		this.net = net;
	}

	/**
	 * @return Positions of the droplet
	 * @warning Do not modify this returned vector as it will modify the
	 * droplet. We could think about returning a copy but than again we believe
	 * in the non-evilness of our clients :)
	 */
	public ArrayList<Point> getPositions() {
		return positions;
	}


	/**
	 * Appends a position the the droplet's route (i.e. positions)
	 *
	 * @param p
	 * 		Position that is added
	 */
	public void addPosition(final Point p) {
		positions.add(p);
	}

	/**
	 * Tries to return the position of the droplet at specified time step.
	 * <p>
	 * If the time step is before the droplet is spawned or after the droplet
	 * has vanished, the first or last position, respectively, is returned in
	 * stead.
	 * <p>
	 * The main feature of this function is that it does not return null.
	 *
	 * @param t
	 * 		Time step for which the position is requested
	 * @return Position of droplet at time step t oder first/last position.
	 */
	public Point getSafePositionAt(final int t) {

		int index = t - spawnTime;

		if (positions.isEmpty()) {
			return null;
		}

		if (index < 0) {
			return getFirstPosition();
		}
		if (index >= positions.size()) {
			return getLastPosition();
		}
		return positions.get(index);
	}

	/**
	 * @param t
	 * 		Time step for which the position is requested
	 * @return Position at time step t or null if outside time range
	 */
	public Point getPositionAt(final int t) {

		int index = t - spawnTime;

		if (positions.isEmpty() || index < 0 || index >= positions.size()) {
			return null;
		}
		return positions.get(index);
	}


	/**
	 * @return First position of the droplet
	 * @throws IndexOutOfBoundsException
	 * 		if list of positions is empty
	 */
	public Point getFirstPosition() {
		return positions.get(0);
	}


	/**
	 * @return Last position of the droplet
	 * @throws IndexOutOfBoundsException
	 * 		if list of positions is empty
	 */
	public Point getLastPosition() {
		return positions.get(positions.size() - 1);
	}


	/**
	 * Calculates the time at which the <i>next</i> step is performed for this
	 * blob. If a blob moves at timestamps 0, 1, 5, 10, calling this method
	 * with
	 * current=4 would return 5. Calling it with current=5 would return 10. If
	 * current is at or after the last value, the last value is returned.
	 *
	 * @param current
	 * 		the current time
	 * @return the timestamp at which the next step is performed.
	 */
	public long getNextStep(final long current) {
		long result = 0;

		if (current > 0 && current < positions.size() - 1) {
			result = current + 1;
		}
		else {
			result = positions.size();
		}


		return result;
	}

	/**
	 * Computes and returns the hash code of this droplet.
	 * <p>
	 * The hash code simply is the droplets id. As this is unique, it serves as
	 * a hash code.
	 *
	 * @return hash code of the Droplet
	 */
	@Override
	public int hashCode() {
		return this.getID();
	}

	/**
	 * Compares an object to this Droplet.
	 *
	 * The object is equal to this Droplet instance if it is an instance of the
	 * droplet class and has the same ID.
	 *
	 * @param o
	 * 		The object to compare against
	 * @return true if both objects reference the same droplet
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Droplet) {
			return ((Droplet) o).getID() == this.getID();
		}
		else {
			return false;
		}
	}

	/**
	 * Retrieves the last timestamp at which this droplet moves.
	 *
	 * @return the time at which the droplet's last movement is performed
	 * @warning Returns 0 in case no positions (i.e. no path) is associated
	 * with
	 * this droplet
	 */
	public int getMaxTime() {
		return positions.size() + spawnTime - 1;
	}

	/**
	 * @return The time step the droplet appears first on the chip
	 */
	public int getSpawnTime() {
		return this.spawnTime;
	}

	/**
	 * @return The String representation in the form "D[<ID>@<spawnTime>]"
	 */
	@Override
	public String toString() {
		return "D[" + id + "@" + spawnTime + "]";
	}

	/**
	 * @return The ID of the droplet
	 */
	public int getID() {
		return this.id;
	}


	/**
	 * @return Length of the route of this droplet.
	 */
	int getRouteLength() {
		return positions.size();
	}
}
