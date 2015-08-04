package de.dfki.bioviz.structures;

import java.util.Collections;
import java.util.Vector;

public class Droplet {
	
	private Vector<TimedPosition> positions = new Vector<>();

	private int id=0;

	public Vector<TimedPosition> getPositions() {
		return positions;
	}

	public float smoothX, smoothY, targetX, targetY;

	public Droplet() {
		// TODO Auto-generated constructor stub
	}

	public Droplet(int id) {
		this.id = id;
	}
	
	public void addPosition(long time, int x, int y) {
		positions.add(new TimedPosition(time, x, y));
		Collections.sort(positions);
	}
	
	public int getXAt(long t) {
		int result = positions.get(0).x;
		for (TimedPosition position : positions) {
			if (position.time <= t) {
				result = position.x;
			} else {
				return result;
			}
		}
		
		return result;
	}
	
	public int getYAt(long t) {
		int result = positions.get(0).y;
		for (TimedPosition position : positions) {
			if (position.time <= t) {
				result = position.y;
			} else {
				return result;
			}
		}
		
		return result;
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
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i).time <= current) {
				result = positions.get(i).time;
			} else {
				if (i < positions.size()) {
					result = positions.get(i).time;
				}
				return result;
			}
		}
		return result;
	}
	
	public void update() {
		float movementDelay = 4f;
		smoothX += (targetX - smoothX) / movementDelay;
		smoothY += (targetY - smoothY) / movementDelay;
	}



	
	/**
	 * Retrieves the last timestamp at which this droplet moves.
	 * @return the time at which the droplet's last movement is performed
	 */
	public long getMaxTime() {
		return this.positions.lastElement().getTime();
	}
	
	public int getID() {
		return this.id;
	}

}
