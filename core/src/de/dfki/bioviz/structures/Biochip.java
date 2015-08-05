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

	private HashMap<Point,BiochipField> field = new HashMap<Point,BiochipField>();

	public final ArrayList<Pair<Rectangle, Range>> blockages= new ArrayList<Pair<Rectangle,Range>>();
	public final ArrayList<Detector> detectors=new ArrayList<Detector>();
	public final HashMap<Integer,Pin> pins = new HashMap<>();
	public final HashMap<Integer,ActuationVector> pinActuations = new HashMap<>();
	public final  HashMap<Point,ActuationVector> cellActuations = new HashMap<>();



	private HashMap<Integer,String> fluidTypes = new HashMap<Integer,String>();
	public void addFluidType(int fluidID, String fluidDescription) {
		fluidTypes.put(fluidID, fluidDescription);
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
	private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
	
	/**
	 * Creates a new 2D-Biochip with a certain field size.
	 * @param dimensionX The size of the chip along the x axis
	 * @param dimensionY The size of the chip along the y axis
	 * @deprecated The chip no longer relies on a fixed field size.
	 * Use {@link #Biochip()} instead.
	 */
	@Deprecated
	public Biochip(int dimensionX, int dimensionY) {

	}

	public Biochip() {
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
		if (this.droplets.contains(drop)) {
			this.droplets.remove(drop);
		}
	}
	
	/**
	 * Returns all droplets that are part of this chip.
	 */
	public Set<Droplet> getDroplets() {
		return this.droplets;
	}


	public void computeCellUsage() {
		logger.debug("Computing cell usage");

		// first we set the usage of each field to zero
		field.values().forEach(f -> f.usage=0);


		for (Droplet drop: droplets) {
			for(TimedPosition pos: drop.getPositions()) {
				field.get(pos.getPos()).usage++;
			}
		}
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
							result.add(this.field.get(new Point(x1,y1)));
							result.add(this.field.get(new Point(x2,y2)));
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
			if (dTime > maxTime) {
				maxTime = dTime;
			}
		}
		return maxTime;
	}

	/**
	 * Retrieves field that is located at given coordinates.
	 * @param coords the coordinates at which the field is located
	 * @return the field
	 * @throws RuntimeException if there is no field at given coordinates
	 */
	public BiochipField getFieldAt(Point coords) {
		if (this.field.containsKey(coords)) {
			return this.field.get(coords);
		} else {
			throw new RuntimeException("Could not retrieve field at " + coords);
		}
	}

	/**
	 * Retrieves the coordinates of all the fields that are currently set
	 * @return all valid coordinates
	 */
	public Set<Point> getAllCoordinates() {
		return this.field.keySet();
	}

	/**
	 * Retrieves all fields of this chip
	 * @return all field instances being used on this chip
	 */
	public Collection<BiochipField> getAllFields() {
		return this.field.values();
	}

	public void addField(Point coordinates, BiochipField field) {
		if (field.x() != coordinates.first || field.y() != coordinates.second) {
			logger.error("Field coordinates differ from those transmitted to the chip for this instance");
			coordinates = new Point(field.x(), field.y());
		}
		if (this.field.containsKey(coordinates)) {
			logger.debug("Field added twice at " + coordinates + ", removed older instance");
		}
		this.field.put(coordinates, field);
	}

	public Point getMaxCoord() {
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		for (Point coord: this.field.keySet()) {
			if (maxX < coord.first) {
				maxX = coord.first;
			}
			if (maxY < coord.second) {
				maxY = coord.second;
			}
		}
		return new Point(maxX, maxY);
	}

	public Point getMinCoord() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (Point coord: this.field.keySet()) {
			if (minX > coord.first) {
				minX = coord.first;
			}
			if (minY > coord.second) {
				minY = coord.second;
			}
		}
		return new Point(minX, minY);
	}

}
