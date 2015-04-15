package structures;

import java.util.Collections;
import java.util.Vector;

public class Blob {
	
	private Vector<Blob.timedPosition> positions = new Vector<Blob.timedPosition>();
	
	public Blob() {
		// TODO Auto-generated constructor stub
	}
	
	public void addPosition(long time, int x, int y) {
		positions.add(new timedPosition(time, x, y));
		Collections.sort(positions);
	}
	
	public int getXAt(long t) {
		int result = 0;
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i).time < t) {
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
			if (positions.get(i).time < t) {
				result = positions.get(i).y;
			} else {
				return result;
			}
		}
		return result;
	}
	
	class timedPosition implements Comparable<timedPosition> {
		long time;
		int x, y;
		
		public timedPosition(long t, int x, int y) {
			this.time = t;
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int compareTo(timedPosition t) {
			return Long.compare(this.time, t.time);
		}
	}

}
