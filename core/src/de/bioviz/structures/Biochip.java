package de.bioviz.structures;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.List;
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


	/**
	 * The internal logging device.
	 */
	private static Logger logger = LoggerFactory.getLogger(Biochip.class);


	public final ArrayList<Pair<Rectangle, Range>> blockages =
			new ArrayList<>();
	public final ArrayList<Detector> detectors = new ArrayList<>();
	public final HashMap<Integer, Pin> pins = new HashMap<>();
	public final HashMap<Integer, ActuationVector> pinActuations =
			new HashMap<>();
	public final HashMap<Point, ActuationVector> cellActuations =
			new HashMap<>();
	public final ArrayList<Mixer> mixers = new ArrayList<>();
	public ArrayList<String> errors = new ArrayList<>();
	public boolean recalculateAdjacency = false;

	private HashMap<Integer, Integer> dropletIDsToFluidTypes = new HashMap<>();

	private HashMap<Integer, String> fluidTypes = new HashMap<>();

	private ArrayList<String> annotations = new ArrayList<>();


	/**
	 * Caching data that does not need to be recalculated with each frame.
	 */
	private Set<FluidicConstraintViolation> adjacencyCache = null;


	/**
	 * The latest time step in the operation of this biochip.
	 * <p>
	 * The initial value of null indicates that the maximal amount of time
	 * steps
	 * has not been computed yet.
	 */
	private Integer maxT = null;

	/**
	 * The length of the longest route.
	 * <p>
	 * The initial value of null indicates that the length has not been
	 * computed
	 * yet.
	 */
	private Integer maxRouteLength = null;

	/**
	 * Caches the maximum usage of all fields to save computation time.
	 * <p>
	 * The initial value of null indicates that the max usage has not been
	 * computed yet.
	 */
	private Integer maxUsageCache = null;

	/**
	 * The fields of this chip.
	 */
	private HashMap<Point, BiochipField> field = new HashMap<>();

	/**
	 * All droplets of this chip.
	 * <p>
	 * Use the get-method to retrieve them from other classes.
	 */
	private HashSet<Droplet> droplets = new HashSet<>();

	/**
	 * The nets of this chip.
	 */
	private ArrayList<Net> nets = new ArrayList<>();


	/**
	 * Adds an amount of fluid types to the biochip.
	 *
	 * @param types
	 * 		the types to add.
	 */
	public void addFluidTypes(final Map<Integer, String> types) {
		fluidTypes.putAll(types);
	}


	/**
	 * Adds a collection of nets to this biochip.
	 *
	 * @param netCollection
	 * 		Collection of nets
	 */
	public void addNets(final Collection<Net> netCollection) {
		this.nets.addAll(netCollection);
		resetCaches();
	}

	/**
	 * Returns a set of all nets of this biochip.
	 *
	 * @return Set of all nets of this biochip field. Might be empty (but not
	 * NULL)
	 */
	public Set<Net> getNets() {
		return new HashSet<>(this.nets);
	}

	/**
	 * Returns all nets that a field belongs to.
	 *
	 * @param biochipField
	 * 		the field to be tested
	 * @return the nets that this field is a part of
	 */
	public Set<Net> getNetsOf(final BiochipField biochipField) {
		HashSet<Net> result = new HashSet<>();

		for (final Net net : nets) {
			if (net.containsField(biochipField)) {
				result.add(net);
			}
		}

		return result;
	}

	/**
	 * Assigns a fluid type to a droplet.
	 *
	 * @param dropletID
	 * 		The droplet that is now of the given fluid type.
	 * @param fluidID
	 * 		The fluid type
	 */
	public void addDropToFluid(final int dropletID, final int fluidID) {
		dropletIDsToFluidTypes.put(dropletID, fluidID);
	}

	/**
	 * Determines the fluid ID of a droplet.
	 * <p>
	 * The result might be NULL if the droplet has no assigned fluid.
	 *
	 * @param dropletID
	 * 		The droplet whose fluid ID is queried
	 * @return The fluid ID of the given droplet
	 */
	public Integer fluidID(final int dropletID) {
		return dropletIDsToFluidTypes.get(dropletID);
	}


	/**
	 * Determines the fluid type of the given fluid ID.
	 *
	 * @param fluidID
	 * 		The fluid ID of the droplet whose fluid type is to be determined.
	 * 	    May be null to support easy chaining.
	 * @return The fluid type of the given droplet. Might be NULL. If fluidID is
	 * NULL the return value is also NULL.
	 */
	public String fluidType(final Integer fluidID) {
		if (fluidID == null) {
			return null;
		}
		return fluidTypes.get(fluidID);
	}


	/**
	 * Adds a droplet to the chip.
	 *
	 * @param drop
	 * 		The droplet that is added.
	 */
	public void addDroplet(final Droplet drop) {
		this.droplets.add(drop);
		resetCaches();
	}

	/**
	 * Returns all droplets that are part of this chip.
	 *
	 * @return The set of all droplets of this chip, might be NULL.
	 */
	public Set<Droplet> getDroplets() {
		return this.droplets;
	}


	/**
	 * Resets all the caches and re-computes the corresponding values.
	 * <p>
	 * This means that after calling this method, the latest time step, the
	 * maximal route length and the maximal use of cells are not set anymore.
	 * You need to manually call the reCompute() method.
	 */
	public void resetCaches() {
		maxT = null;
		maxRouteLength = null;
		maxUsageCache = null;
	}


	/**
	 * Re-compute internal values.
	 * <p>
	 * The values are the maximal cell usage, the max route length and the
	 * latest time step.
	 */
	public void reCompute() {
		resetCaches();
		computeCellUsage();
		getMaxT();
		getMaxRouteLength();
		getMaxUsage();
	}

	/**
	 * Checks whether any droplet is present on a position in a given time
	 * step.
	 *
	 * @param pos
	 * 		Position to check for the presence of a droplet.
	 * @param t
	 * 		The time step to test for the presence.
	 * @return True iff there is a droplet present in the time step.
	 */
	boolean dropletOnPosition(final Point pos, final int t) {

		for (final Droplet d : droplets) {
			Point p = d.getPositionAt(t);
			if (p != null && p.equals(pos)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Checks whether two droplets are from the same net.
	 *
	 * @param d1
	 * 		First droplet to test.
	 * @param d2
	 * 		Second droplet to test.
	 * @return true iff the droplets are from the same net
	 */
	private boolean sameNet(final Droplet d1, final Droplet d2) {
		if (nets != null) {
			// first find the net of one of the droplets
			Net net = null;
			for (final Net n : nets) {
				if (n.containsDroplet(d1)) {
					net = n;
				}
			}
			return net != null && net.containsDroplet(d2);
		} else {
			// when there are no nets stored, they can't be from the same net
			return false;
		}
	}


	void addAdjacentPoint(final Point p1, final Droplet d1, final Point p2,
						  final Droplet d2, final Set s, final int timestep) {
		if (Point.adjacent(p1, p2)) {
			logger.trace("Points " + p1 + "(" + d1 + ") and " + p2 + "(" + d2 +
						 ") are adjacent in time step " + timestep);
			BiochipField f1 = field.get(p1);
			BiochipField f2 = field.get(p2);
			s.add(new FluidicConstraintViolation(d1, f1, d2, f2, timestep));
		}
	}

	/**
	 * Adds a single annotation to the chip.
	 * @param annotation the annotation
	 */
	public void addAnnotation(final String annotation){
		this.annotations.add(annotation);
	}

	/**
	 * Adds multiple annotations to the chip.
	 * @param annotations the annotations
	 */
	public void addAnnotations(final List<String> annotations){
		this.annotations.addAll(annotations);
	}

	/**
	 * Calculates all fields that are at some point activated with adjacently
	 * placed droplets.
	 *
	 * @return Set of fields with adjacent droplets (at any point in time)
	 */
	public Set<FluidicConstraintViolation> getAdjacentActivations() {

		if (adjacencyCache != null && !recalculateAdjacency) {
			return adjacencyCache;
		} else {
			logger.debug("Recalculating adjacency");
			recalculateAdjacency = false;
			HashSet<FluidicConstraintViolation> result = new HashSet<>();

			for (int timestep = 1; timestep <= getMaxT(); timestep++) {
				for (final Droplet d1 : droplets) {
					Point p1 = d1.getPositionAt(timestep);
					Point pp1 = d1.getPositionAt(timestep + 1);
					for (final Droplet d2 : droplets) {

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
							addAdjacentPoint(p1,d1,p2,d2,result,timestep);
							addAdjacentPoint(pp1,d1,p2,d2,result,timestep);
							addAdjacentPoint(p1,d1,pp2,d2,result,timestep);
						}
					}
				}
			}

			adjacencyCache = result;
			return result;
		}
	}

	/**
	 * Calculates the last timestamp at which a droplet is moved.
	 *
	 * @return the last timestamp of the currently loaded simulation
	 */
	public int getMaxT() {
		if (maxT != null) {
			return maxT;
		}

		maxT = 0;
		for (final Droplet d : droplets) {
			maxT = Math.max(maxT, d.getMaxTime());
		}
		for (final Mixer m : mixers) {
			maxT = Math.max(maxT, m.timing.end);
		}
		for (final Pair<Rectangle, Range> b : blockages) {
			maxT = Math.max(maxT, b.snd.end);
		}
		for (final ActuationVector a : pinActuations.values()) {
			maxT = Math.max(maxT, a.size());
		}
		for (final ActuationVector a : cellActuations.values()) {
			maxT = Math.max(maxT, a.size());
		}
		return maxT;

	}


	/**
	 * Determines the length of the longest route.
	 *
	 * @return Length of the longest route
	 */
	public int getMaxRouteLength() {
		if (maxRouteLength == null) {
			maxRouteLength = 0;
			for (final Droplet d : droplets) {
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
	public BiochipField getFieldAt(final Point coords) {
		if (hasFieldAt(coords)) {
			return this.field.get(coords);
		} else {
			throw new RuntimeException("Could not retrieve field at " +
									   coords);
		}
	}


	/**
	 * Checks whether there is a field ad the given position.
	 *
	 * @param coords
	 * 		The coordinates to check for a field
	 * @return true if there is a field at the specified positions, false
	 * otherwise
	 */
	public boolean hasFieldAt(final Point coords) {
		return this.field.containsKey(coords);
	}

	/**
	 * Retrieves the coordinates of all the fields that are currently set.
	 *
	 * @return all valid coordinates
	 */
	public Set<Point> getAllCoordinates() {
		return this.field.keySet();
	}

	/**
	 * Retrieves all fields of this chip.
	 *
	 * @return all field instances being used on this chip
	 */
	public Collection<BiochipField> getAllFields() {
		return this.field.values();
	}


	/**
	 * Returns the fields connected via a pin.
	 *
	 * @param pinID
	 * 		The ID of the pin
	 * @return The fields connected via the specified pin. Might be NULL.
	 */
	public List<BiochipField> getFieldsForPin(final Integer pinID) {

		List<BiochipField> fields;
		fields = field.values().
				parallelStream().
				filter(f -> f.pin != null && f.pin.pinID == pinID).
				collect(Collectors.toList());

		return fields;
	}

	/**
	 * Adds a field to the chip.
	 *
	 * @param coordinates
	 * 		The coordinates to which the field is to be added.
	 * @param biochipField
	 * 		The field to be added.
	 */
	// TODO why the heck do we provide the coordinates twice? --> check the
	// parsing procedure.
	public void addField(final Point coordinates, final BiochipField
			biochipField) {
		Point coords = coordinates;
		if (biochipField.x() != coords.fst ||
			biochipField.y() != coords.snd) {
			logger.error(
					"Field coordinates differ from those transmitted to the " +
					"chip for this instance");
			coords = new Point(biochipField.x(), biochipField.y());
		}
		if (this.field.containsKey(coords)) {
			logger.trace("Field added twice at " + coords +
						 ", removed older instance");
		}
		this.field.put(coords, biochipField);
	}


	/**
	 * Determines the maximal coordinate of the biochip.
	 * <p>
	 * This corresponds to the  upper right corner of the chip.
	 *
	 * @return The maximal (i.e. upper right) coordinate of the biochip.
	 */
	public Point getMaxCoord() {
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (final Point coord : this.field.keySet()) {
			if (maxX < coord.fst) {
				maxX = coord.fst;
			}
			if (maxY < coord.snd) {
				maxY = coord.snd;
			}
		}
		return new Point(maxX, maxY);
	}

	/**
	 * Determines the minimal coordinate of the biochip.
	 * <p>
	 * This corresponds to the lower left corner of the chip.
	 *
	 * @return The minimal (i.e. lower left) coordinate of the biochip.
	 */
	public Point getMinCoord() {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		for (final Point coord : this.field.keySet()) {
			if (minX > coord.fst) {
				minX = coord.fst;
			}
			if (minY > coord.snd) {
				minY = coord.snd;
			}
		}
		return new Point(minX, minY);
	}

	/**
	 * Determines the maximal amount of usages of the cells.
	 * <p>
	 * The result is cached.
	 *
	 * @return The maximal amount of times a cell is actuated.
	 */
	public int getMaxUsage() {
		if (maxUsageCache == null) {
			maxUsageCache = 0;
			for (final BiochipField f : this.field.values()) {
				if (f.getUsage() > this.maxUsageCache) {
					this.maxUsageCache = f.getUsage();
				}
			}
		}
		return maxUsageCache;
	}

	/**
	 * Computes the cell usage for every cell of this chip.
	 */
	public void computeCellUsage() {
		logger.debug("Computing cell usage");

		for (final BiochipField f : field.values()) {
			f.computeUsage(getMaxT());
		}
	}
}
