package de.bioviz.structures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Vector;

/**
 * @author Oliver Keszöcze
 *         <p>
 *         Class that contains everything that is necessary to describe a DMFB
 *         droplet. This includes basic facts such as a unique ID as well as
 *         information about the movement on the chip.
 */
public class Droplet {

	public float smoothX;
	public float smoothY;


	private boolean firstUpdate = true;

	public static int movementTransitionDuration = 500;

	static Logger logger = LoggerFactory.getLogger(Droplet.class);

	private Net net = null;

	private Vector<Point> positions = new Vector<>();

	private int id = 0;
	private int spawnTime = 1;

	private float targetX;
	private float targetY;
	private float originX;
	private float originY;

	private float movementDelay = 4f;

	private long movementTransitionStartTime = 0, movementTransitionEndTime
			= 0;

	public Droplet(final int id) {
		this.id = id;
	}

	public Droplet(final int id, final int spawnTime) {
		this.id = id;
		this.spawnTime = spawnTime;
	}


	public Net getNet() {
		return net;
	}

	public void setNet(final Net net) {
		this.net = net;
	}


	public Vector<Point> getPositions() {
		return positions;
	}

	/**
	 * @return Length of the route of this droplet.
	 */
	public int getRouteLength() {
		return positions.size();
	}


	public void addPosition(final int x,final int y) {
		positions.add(new Point(x, y));
	}

	public void addPosition(final Point p) {
		positions.add(p);
	}

	public Point getSafePositionAt(final int t) {

		int index = t - spawnTime;

		if (positions.isEmpty()) {
			return null;
		}

		if (index < 0) {
			return positions.firstElement();
		}
		if (index >= positions.size()) {
			return positions.lastElement();
		}
		return positions.get(index);
	}

	public Point getPositionAt(final int t) {

		int index = t - spawnTime;

		if (positions.isEmpty() || index < 0 || index >= positions.size()) {
			return null;
		}
		return positions.get(index);
	}

	public Point getFirstPosition() {
		return positions.firstElement();
	}

	public Point getLastPosition() {
		return positions.lastElement();
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

	public void update() {
		if (firstUpdate) {
			smoothX = getTargetX();
			smoothY = getTargetY();
			originX = getTargetX();
			originY = getTargetY();
			firstUpdate = false;
		}

		float totalProgress = 1;
		if (movementTransitionStartTime != movementTransitionEndTime) {
			float transitionProgress = Math.max(0, Math.min(1, (float) (
					new Date().getTime() - movementTransitionStartTime) /
															   (float) (
																	   movementTransitionEndTime -
																	   movementTransitionStartTime)));
			totalProgress =
					(float) (-(Math.pow((transitionProgress - 1), 4)) + 1);
		}

		smoothX = this.originX * (1 - totalProgress) +
				  this.targetX * totalProgress;
		smoothY = this.originY * (1 - totalProgress) +
				  this.targetY * totalProgress;
	}

	@Override
	public int hashCode() {
		return this.getID();
	}

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

	public int getSpawnTime() {
		return this.spawnTime;
	}

	@Override
	public String toString() {
		return "D[" + id + "@" + spawnTime + "]";
	}

	public int getID() {
		return this.id;
	}

	public float getTargetX() {
		return targetX;
	}

	public float getTargetY() {
		return targetY;
	}

	public void setTargetPosition(final float targetX,final float targetY) {
		if (this.targetX != targetX || this.targetY != targetY) {
			originX = this.smoothX;
			originY = this.smoothY;
			this.targetX = targetX;
			this.targetY = targetY;
			Date d = new Date();
			this.movementTransitionStartTime = d.getTime();
			this.movementTransitionEndTime =
					d.getTime() + movementTransitionDuration;
		}
	}

}
