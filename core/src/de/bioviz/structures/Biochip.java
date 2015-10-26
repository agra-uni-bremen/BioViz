package de.bioviz.structures;

import java.util.*;
import java.util.stream.Collectors;

import de.bioviz.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a biochip. It consists of a set of droplets (the wobbly
 * drops that travel around) and a set of fields (i.e. the parts that they
 * travel around on).
 *
 * @author jannis
 */
public class Biochip {


	static Logger logger = LoggerFactory.getLogger(Biochip.class);

	private HashMap<Point, BiochipField> field =
			new HashMap<Point, BiochipField>();

	public final ArrayList<Pair<Rectangle, Range>> blockages =
			new ArrayList<Pair<Rectangle, Range>>();
	public final ArrayList<Detector> detectors = new ArrayList<Detector>();
	public final HashMap<Integer, Pin> pins = new HashMap<>();
	public final HashMap<Integer, ActuationVector> pinActuations =
			new HashMap<>();
	public final HashMap<Point, ActuationVector> cellActuations =
			new HashMap<>();
	public final ArrayList<Mixer> mixers = new ArrayList<Mixer>();


	private HashMap<Integer, String> fluidTypes =
			new HashMap<Integer, String>();

	public void addFluidType(int fluidID, String fluidDescription) {
		fluidTypes.put(fluidID, fluidDescription);
	}

	public void addFluidTypes(HashMap<Integer, String> types) {
		fluidTypes.putAll(types);
	}


	private ArrayList<Net> nets = new ArrayList<Net>();

	public void addNet(Net n) {
		nets.add(n);
	}

	public void addNets(Collection<Net> nets) {
		this.nets.addAll(nets);
	}

	private HashMap<Integer, Integer> dropletIDsToFluidTypes = new HashMap<>();

	public void addDropToFluid(int dropletID, int fluidID) {
		dropletIDsToFluidTypes.put(dropletID, fluidID);
	}

	public Integer fluidID(int dropletID) {
		return dropletIDsToFluidTypes.get(dropletID);
	}

	public String fluidType(Integer fluidID) {
		return fluidTypes.get(fluidID);
	}


	private int maxT = -1;
	private int maxRouteLength = -1;


	/**
	 * All droplets of this chip. Use the get-method to retrieve them from
	 * other
	 * classes.
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
	 *
	 * @param dimensionX
	 * 		The size of the chip along the x axis
	 * @param dimensionY
	 * 		The size of the chip along the y axis
	 * @deprecated The chip no longer relies on a fixed field size. Use {@link
	 * #Biochip()} instead.
	 */
	@Deprecated
	public Biochip(int dimensionX, int dimensionY) {

	}

	public Biochip() {
	}


	public void addDroplet(Droplet drop) {
		this.droplets.add(drop);
	}

	/**
	 * <p>Removes a blob from this chip.</p> <p>This does <i>not</i> mean that
	 * the blob is removed at a certain time or something. Instead, this blob
	 * and its according data is removed from the circuit altogether.</p>
	 *
	 * @param drop
	 * 		The droplet that will be removed
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
		field.values().forEach(f -> f.usage = 0);


		for (int t = 1; t <= getMaxT(); t++) {
			for (BiochipField f : field.values()) {
				if (f.isActuated(t)) {
					f.usage++;
				}
			}
		}
	}

	public boolean dropletOnPosition(Point pos, int t) {

		for (Droplet d : droplets) {
			Point p = d.getPositionAt(t);
			if (p != null && p.equals(pos)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @param d1
	 * @param d2
	 * @return true iff the droplets are from the same net
	 * @brief Checks whether two droplets are from the same nat
	 */
	private boolean sameNet(Droplet d1, Droplet d2) {
		if (nets != null) {
			// first find the net of one of the droplets
			Net net = null;
			for (Net n : nets) {
				if (n.containsDroplet(d1)) {
					net = n;
				}
			}
			return net == null ? false : net.containsDroplet(d2);
		}
		// when there are no nets stored, they can't be from the same net
		else {
			return false;
		}
	}

	/**
	 * Calculates all fields that are at some point activated with adjacently
	 * placed droplets.
	 *
	 * @return Set of fields with adjacent droplets (at any point in time)
	 */
	public Set<BiochipField> getAdjacentActivations() {

		if (adjacencyCache != null && !recalculateAdjacency) {
			return adjacencyCache;
		}
		else {
			logger.debug("Recalculating adjacency");
			recalculateAdjacency = false;
			HashSet<BiochipField> result = new HashSet<>();

			for (int timestep = 1; timestep <= getMaxT(); timestep++) {
				for (Droplet d1 : droplets) {
					Point p1 = d1.getPositionAt(timestep);
					Point pp1 = d1.getPositionAt(timestep + 1);
					for (Droplet d2 : droplets) {

						logger.trace("Comparing droplets {} and {}", d1, d2);


						if (!d1.equals(d2) && !sameNet(d1, d2)) {
							Point p2 = d2.getPositionAt(timestep);
							Point pp2 = d2.getPositionAt(timestep + 1);
							/*
							We actually need to differentiat the following
							three cases. The dynamic fluidic constraints
							should highlight the cell that in the upcoming
							time step violates one of the constraints.
							 */
							if (Point.adjacent(p1, p2)) {
								logger.trace(
										"Points " + p1 + "(" + d1 + ") and " +
										p2 + "(" + d2 +
										") are adjacent in time step " +
										timestep);
								result.add(this.field.get(p1));
								result.add(this.field.get(p2));
							}
							if (Point.adjacent(pp1, p2)) {
								logger.trace(
										"Points " + pp1 + "(" + d1 + ") and " +
										p2 + "(" + d2 +
										") are adjacent in time step " +
										(timestep + 1) + "/" + timestep);
								result.add(this.field.get(pp1));
								result.add(this.field.get(p2));
							}
							if (Point.adjacent(p1, pp2)) {
								logger.trace(
										"Points " + p1 + "(" + d1 + ") and " +
										pp2 + "(" + d2 +
										") are adjacent in time step " +
										timestep + "/" + (timestep + 1));
								result.add(this.field.get(p1));
								result.add(this.field.get(pp2));
							}
						}
					}
				}
				logger.trace("Advanced to timestep {}", timestep);
			}

			adjacencyCache = result;
			return result;
		}
	}

	/**
	 * Calculates the last timestamp at which a droplet is moved
	 *
	 * @return the last timestamp of the currently loaded simulation
	 * @author Oliver Keszöcze
	 */
	public int getMaxT() {
		if (maxT != -1) {
			return maxT;
		}

		for (Droplet d : droplets) {
			maxT = Math.max(maxT, d.getMaxTime());
		}
		for (Mixer m : mixers) {
			maxT = Math.max(maxT, m.timing.end);
		}
		for (Pair<Rectangle, Range> b : blockages) {
			maxT = Math.max(maxT, b.snd.end);
		}
		for (ActuationVector a : pinActuations.values()) {
			maxT = Math.max(maxT, a.size());
		}
		for (ActuationVector a : cellActuations.values()) {
			maxT = Math.max(maxT, a.size());
		}
		return maxT;

	}


	/**
	 * @return Length of the longest route
	 * @author Oliver Keszöcze
	 */
	public int getMaxRouteLength() {
		if (maxRouteLength == -1) {

			for (Droplet d : droplets) {
				maxRouteLength = Math.max(maxRouteLength, d.getRouteLength());
			}
		}
		return maxRouteLength;
	}


	/**
	 * Retrieves field that is located at given coordinates.
	 *
	 * @param coords
	 * 		the coordinates at which the field is located
	 * @return the field
	 * @throws RuntimeException
	 * 		if there is no field at given coordinates
	 */
	public BiochipField getFieldAt(Point coords) {
		if (hasFieldAt(coords)) {
			return this.field.get(coords);
		}
		else {
			throw new RuntimeException("Could not retrieve field at " +
									   coords);
		}
	}


	/**
	 * @param coords
	 * 		The coordinates to check for a field
	 * @return true if there is a field at the specified positions, false
	 * otherwise
	 * @brief Checks whether there is a field ad the given position.
	 */
	public boolean hasFieldAt(Point coords) {
		return this.field.containsKey(coords);
	}

	/**
	 * Retrieves the coordinates of all the fields that are currently set
	 *
	 * @return all valid coordinates
	 */
	public Set<Point> getAllCoordinates() {
		return this.field.keySet();
	}

	/**
	 * Retrieves all fields of this chip
	 *
	 * @return all field instances being used on this chip
	 */
	public Collection<BiochipField> getAllFields() {
		return this.field.values();
	}

	public List<BiochipField> getFieldsForPin(Integer pinID) {

		List<BiochipField> fields = new ArrayList<BiochipField>();
		fields = field.values().
				parallelStream().
				filter(f -> f.pin != null && f.pin.pinID == pinID).
				collect(Collectors.toList());

		return fields;
	}

	public void addField(Point coordinates, BiochipField field) {
		if (field.x() != coordinates.fst || field.y() != coordinates.snd) {
			logger.error(
					"Field coordinates differ from those transmitted to the " +
					"chip for this instance");
			coordinates = new Point(field.x(), field.y());
		}
		if (this.field.containsKey(coordinates)) {
			logger.trace("Field added twice at " + coordinates +
						 ", removed older instance");
		}
		this.field.put(coordinates, field);
	}

	public Point getMaxCoord() {
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		for (Point coord : this.field.keySet()) {
			if (maxX < coord.fst) {
				maxX = coord.fst;
			}
			if (maxY < coord.snd) {
				maxY = coord.snd;
			}
		}
		return new Point(maxX, maxY);
	}

	public Point getMinCoord() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (Point coord : this.field.keySet()) {
			if (minX > coord.fst) {
				minX = coord.fst;
			}
			if (minY > coord.snd) {
				minY = coord.snd;
			}
		}
		return new Point(minX, minY);
	}

}
