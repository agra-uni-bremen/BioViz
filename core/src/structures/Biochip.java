package structures;

import java.util.HashSet;
import java.util.Set;

public class Biochip {

	public BiochipField[][] field;
	private HashSet<Blob> blobs = new HashSet<Blob>();
	
	public Biochip(int dimensionX, int dimensionY) {
		field = new BiochipField[dimensionX][dimensionY];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = new BiochipField();
			}
		}
	}
	
	public void disableFieldAt(int x, int y) {
		field[x][y].isEnabled = false;
	}
	
	public void enableFieldAt(int x, int y) {
		field[x][y].isEnabled = true;
	}
	
	public Blob addBlob() {
		Blob b = new Blob();
		this.blobs.add(b);
		return b;
	}
	
	public void removeBlob(Blob b) {
		if (this.blobs.contains(b))
			this.blobs.remove(b);
	}
	
	public Set<Blob> getBlobs() {
		return this.blobs;
	}

}
