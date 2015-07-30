package de.dfki.bioviz.structures;

import java.util.*;

import de.dfki.bioviz.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a biochip. It consists of a set of
 * droplets (the wobbly drops that travel around) and a set of
 * fields (i.e. the parts that they travel around on).
 * 
 * @author jannis
 *
 */
public class Biochip {


	static Logger logger = LoggerFactory.getLogger(Biochip.class);

	/**
	 * The fields that are part of this biochip.
	 * While this is currently public I'm not really sure
	 * if this is a good idea, so it may be set to private
	 * at any given time.
	 */
	public BiochipField[][] field;

	public ArrayList<Pair<Rectangle, Range>> blockages;
	public ArrayList<Detector> detectors;



	private HashMap<Integer,String> fluidTypes = new HashMap<Integer,String>();
	public void addFluidType(int fluidID, String fluidDescription) {
		fluidTypes.put(fluidID,fluidDescription);
	}
	public void addFluidTypes(HashMap<Integer,String> types) {
		fluidTypes.putAll(types);
	}


	private ArrayList<Net> nets = new ArrayList<Net>();
	public void addNet(Net n) {
		nets.add(n);
	}
	public void addNets(Collection<Net> nets) {
		this.nets.addAll(nets);
	}

	private HashMap<Integer,Integer> dropletIDsToFluidTypes = new HashMap<>();
	public void addDropToFluid(int dropletID, int fluidID) {
		dropletIDsToFluidTypes.put(dropletID,fluidID);
	}


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
				field[i][j] = new BiochipField(i, j);
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
	 * Enables all fields of the chip
	 */
	public void enableAll() {
		Arrays.stream(field).forEach(flds -> Arrays.stream(flds).forEach(f -> f.isEnabled=true));

	}

	/**
	 * Adds a new blob to the circuit.
	 * @return the newly created blob.
	 */
	public Droplet addDroplet() {
		Droplet b = new Droplet();
		this.droplets.add(b);
		return b;
	}

	public void addDroplet(Droplet drop) {
		this.droplets.add(drop);
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
			logger.debug("Recalculation adjacency");
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
						if (
								(x1 == x2 && Math.abs(y1 - y2) == 1) ||
								(y1 == y2 && Math.abs(x1 - x2) == 1)
							) {
							result.add(this.field[x1][y1]);
							result.add(this.field[x2][y2]);
							//BioViz.singleton.mc.addMessage("Found adjacency: " + x1 + "/" + y1 + " <-> " + x2 + "/" + y2 + " at " + currentTime, MessageCenter.SEVERITY_DEBUG);
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
	
	/**
	 * Calculates the last timestamp at which a droplet is moved
	 * @return the last timestamp of the currently loaded simulation
	 */
	public long getMaxTime() {
		long maxTime = 0;
		for (Droplet d : this.droplets) {
			long dTime = d.getMaxTime();
			if (dTime > maxTime)
				maxTime = dTime;
		}
		return maxTime;
	}

}
