package de.bioviz.structures;

import java.util.Collections;
import java.util.Vector;

public class Droplet {
	
	private Vector<Point> positions = new Vector<>();

	private int id=0;
	private int spawnTime = 1;

	public Vector<Point> getPositions() {
		return positions;
	}

	public float smoothX, smoothY, targetX, targetY;

	public Droplet(int id) {
		this.id = id;
	}
	public Droplet(int id, int spawnTime) {
		this.id = id;
		this.spawnTime=spawnTime;
	}
	
	public void addPosition(int x, int y) {
		positions.add(new Point(x,y));
	}

	public void addPosition(Point p) {
		positions.add(p);
	}
	
	public Point getPositionAt(int t) {

		int index = t-spawnTime;

		if (positions.isEmpty() || index < 0 || index >= positions.size()) {
			return null;
		}

		return positions.get(index);
	}

	
	/**
	 * Calculates the time at which the <i>next</i> step is performed
	 * for this blob. If a blob moves at timestamps 0, 1, 5, 10, calling
	 * this method with current=4 would return 5. Calling it with current=5
	 * would return 10. If current is at or after the last value, the last
	 * value is returned.
	 * @param current the current time
	 * @return the timestamp at which the next step is performed.
	 */
	public long getNextStep(long current) {
		long result = 0;

		if (current > 0 && current < positions.size()-1) {
			result = current+1;
		}
		else {
			result = positions.size();
		}


		return result;
	}
	
	public void update() {
		float movementDelay = 4f;
		smoothX += (targetX - smoothX) / movementDelay;
		smoothY += (targetY - smoothY) / movementDelay;
	}

	@Override
	public int hashCode() {
		return this.getID();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Droplet) {
			return ((Droplet) o).getID() == this.getID();
		}
		else {
			return false;
		}
	}

	/**
	 * Retrieves the last timestamp at which this droplet moves.
	 * @return the time at which the droplet's last movement is performed
	 * @warning Returns 0 in case no positions (i.e. no path) is associated with this droplet
	 */
	public int getMaxTime() {

		return positions.size()+spawnTime-1;
	}

	@Override
	public String toString() {
		return "D["+id+"@"+spawnTime+"]";
	}

	public int getID() {
		return this.id;
	}

}
