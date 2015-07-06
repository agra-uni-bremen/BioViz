package structures;

import java.util.Collections;
import java.util.Vector;

public class Blob {
	
	private Vector<Blob.TimedPosition> positions = new Vector<Blob.TimedPosition>();
	
	public float smoothX, smoothY, targetX, targetY;

	private float movementDelay = 4f;
	
	public Blob() {
		// TODO Auto-generated constructor stub
	}
	
	public void addPosition(long time, int x, int y) {
		positions.add(new TimedPosition(time, x, y));
		Collections.sort(positions);
	}
	
	public int getXAt(long t) {
		int result = 0;
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i).time <= t) {
				result = positions.get(i).x;
			} else {
				return result;
			}
		}
		return result;
	}
	
	public int getYAt(long t) {
		int result = 0;
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i).time <= t) {
				result = positions.get(i).y;
			} else {
				return result;
			}
		}
		return result;
	}
	
	public void update() {
		smoothX += (targetX - smoothX) / movementDelay;
		smoothY += (targetY - smoothY) / movementDelay;
	}
	
	
	
	/**
	 * 
	 * @author jannis
	 *
	 * @note This class has a natural ordering that is inconsistent with equals.
	 */
	class TimedPosition implements Comparable<TimedPosition> {
		long time;
		int x, y;
		
		public TimedPosition(long t, int x, int y) {
			this.time = t;
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int compareTo(TimedPosition t) {
			return Long.compare(this.time, t.time);
		}
	}

}
