package structures;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a biochip. It consists of a set of
 * droplets (the wobbly drops that travel around) and a set of
 * fields (i.e. the parts that they travel around on).
 * 
 * @author jannis
 *
 */
public class Biochip {

	/**
	 * The fields that are part of this biochip.
	 * While this is currently public I'm not really sure
	 * if this is a good idea, so it may be set to private
	 * at any given time.
	 */
	public BiochipField[][] field;
	
	/**
	 * All droplets of this chip. Use the get-method to retrieve
	 * them from other classes.
	 */
	private HashSet<Droplet> droplets = new HashSet<>();
	
	/**
	 * Caching data that does not need to be recalculated with each frame.
	 */
	private Set<BiochipField> adjacencyCache = null;
	
	public boolean recalculateAdjacency = false;
	
	/**
	 * Creates a new 2D-Biochip with a certain field size.
	 * @param dimensionX The size of the chip along the x axis
	 * @param dimensionY The size of the chip along the y axis
	 */
	public Biochip(int dimensionX, int dimensionY) {
		field = new BiochipField[dimensionX][dimensionY];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = new BiochipField();
			}
		}
	}
	
	/**
	 * Sets a field's disabled flag so it is no longer
	 * drawn as part of this circuit. As the fields are
	 * currently stored using a 2-dimensional array, this
	 * does not actually remove anything. It really just
	 * sets a flag.
	 */
	public void disableFieldAt(int x, int y) {
		field[x][y].isEnabled = false;
	}
	
	/**
	 * (Re-)Enables a field at certain coordinates after
	 * it has been disabled (e.g. using disableFieldAt(x,y)).
	 */
	public void enableFieldAt(int x, int y) {
		field[x][y].isEnabled = true;
	}
	
	/**
	 * Adds a new blob to the circuit.
	 * @return the newly created blob.
	 */
	public Droplet addBlob() {
		Droplet b = new Droplet();
		this.droplets.add(b);
		return b;
	}
	
	/**
	 * <p>Removes a blob from this chip.</p>
	 * <p>This does <i>not</i> mean that the blob is removed
	 * at a certain time or something. Instead, this blob and its
	 * according data is removed from the circuit altogether.</p>
	 * @param drop The droplet that will be removed
	 */
	public void removeBlob(Droplet drop) {
		if (this.droplets.contains(drop))
			this.droplets.remove(drop);
	}
	
	/**
	 * Returns all droplets that are part of this chip.
	 */
	public Set<Droplet> getDroplets() {
		return this.droplets;
	}
	
	/**
	 * Calculates all fields that are at some point activated
	 * with adjacently placed droplets.
	 *
	 * @return Set of fields with adjacent droplets (at any point in time)
	 */
	public Set<BiochipField> getAdjacentActivations() {
		if (adjacencyCache != null && !recalculateAdjacency) {
			return adjacencyCache;
		} else {
			System.out.println("Recalculating adjacency...");
			recalculateAdjacency = false;
			HashSet<BiochipField> result = new HashSet<>();
			
			boolean timeProceeds = true;
			long currentTime = 0;
			
			while(timeProceeds) {
				long minimumTimestep = Long.MAX_VALUE;
				timeProceeds = false;
				for (Droplet b : this.getDroplets()) {
					
					int x1, y1;
					x1 = b.getXAt(currentTime);
					y1 = b.getYAt(currentTime);
					for (Droplet partner : this.getDroplets()) {
						int x2, y2;
						x2 = partner.getXAt(currentTime);
						y2 = partner.getYAt(currentTime);

						// TODO When everything works, remove this stupid outputs

						System.out.println("Checking " + x1 + "/" + y1 + " <-> " + x2 + "/" + y2 + " at " + currentTime);
						if (
								(x1 == x2 && Math.abs(y1 - y2) == 1) ||
								(y1 == y2 && Math.abs(x1 - x2) == 1)
							) {
							result.add(this.field[x1][y1]);
							result.add(this.field[x2][y2]);
							System.out.println("Found adjacency: " + x1 + "/" + y1 + " <-> " + x2 + "/" + y2);
						}
					}
					
					long nextStep = b.getNextStep(currentTime);
					if (nextStep > currentTime) {
						timeProceeds = true;
						if (nextStep - currentTime < minimumTimestep) {
							minimumTimestep = nextStep - currentTime;
						}
					}
				}
				
				currentTime += minimumTimestep;
			}
			adjacencyCache = result;
			return result;
		}
	}

}
