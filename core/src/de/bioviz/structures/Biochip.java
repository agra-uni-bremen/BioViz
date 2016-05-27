package de.bioviz.structures;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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


	static Logger logger = LoggerFactory.getLogger(Biochip.class);


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

	private HashMap<Integer, Integer> dropletIDsToFluidTypes = new HashMap<>();

	public HashMap<Integer, String> fluidTypes = new HashMap<>();

	public boolean recalculateAdjacency = false;

	/**
	 * Caching data that does not need to be recalculated with each frame.
	 */
	private Set<FluidicConstraintViolation> adjacencyCache = null;


	private int maxT = -1;
	private int maxRouteLength = -1;

	/**
	 * Caches the maximum usage of all fields to save computation time.
	 */
	private int maxUsageCache = -1;

	private HashMap<Point, BiochipField> field = new HashMap<>();

	/**
	 * All droplets of this chip. Use the get-method to retrieve them from
	 * other
	 * classes.
	 */
	private HashSet<Droplet> droplets = new HashSet<>();
	private ArrayList<Net> nets = new ArrayList<>();


	/**
	 * Adds an amount of fluid types to the biochip.
	 *
	 * @param types
	 * 		the types to add.
	 */
	public void addFluidTypes(final HashMap<Integer, String> types) {
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

		for (Net net : nets) {
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

	public String fluidType(final Integer fluidID) {
		return fluidTypes.get(fluidID);
	}


	public void addDroplet(final Droplet drop) {
		this.droplets.add(drop);
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
			for (final BiochipField f : field.values()) {
				if (f.isActuated(t)) {
					f.usage++;
				}
			}
		}
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
	public boolean dropletOnPosition(final Point pos, final int t) {

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
	public Set<FluidicConstraintViolation> getAdjacentActivations() {

		if (adjacencyCache != null && !recalculateAdjacency) {
			return adjacencyCache;
		}
		else {
			logger.debug("Recalculating adjacency");
			recalculateAdjacency = false;
			HashSet<FluidicConstraintViolation> result = new HashSet<>();

			for (int timestep = 1; timestep <= getMaxT(); timestep++) {
				for (final Droplet d1 : droplets) {
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
								BiochipField f1 = field.get(p1);
								BiochipField f2 = field.get(p2);
								result.add(
										new FluidicConstraintViolation(d1, f1,
																	   d2, f2,
																	   timestep));

							}
							if (Point.adjacent(pp1, p2)) {
								logger.trace(
										"Points " + pp1 + "(" + d1 + ") and " +
										p2 + "(" + d2 +
										") are adjacent in time step " +
										(timestep + 1) + "/" + timestep);
								BiochipField f1 = field.get(pp1);
								BiochipField f2 = field.get(p2);
								result.add(
										new FluidicConstraintViolation(d1, f1,
																	   d2, f2,
																	   timestep));
							}
							if (Point.adjacent(p1, pp2)) {
								logger.trace(
										"Points " + p1 + "(" + d1 + ") and " +
										pp2 + "(" + d2 +
										") are adjacent in time step " +
										timestep + "/" + (timestep + 1));

								BiochipField f1 = field.get(p1);
								BiochipField f2 = field.get(pp2);
								result.add(
										new FluidicConstraintViolation(d1, f1,
																	   d2, f2,
																	   timestep));
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
	 * @author Oliver Keszocze
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
	 * @author Oliver Keszocze
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
	public BiochipField getFieldAt(final Point coords) {
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
	public boolean hasFieldAt(final Point coords) {
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

	public List<BiochipField> getFieldsForPin(final Integer pinID) {

		List<BiochipField> fields;
		fields = field.values().
				parallelStream().
				filter(f -> f.pin != null && f.pin.pinID == pinID).
				collect(Collectors.toList());

		return fields;
	}

	public void addField(Point coordinates, final BiochipField field) {
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

	public int getMaxUsage() {
		if (this.maxUsageCache <= 0) {
			for (final BiochipField f : this.field.values()) {
				if (f.usage > this.maxUsageCache) {
					this.maxUsageCache = f.usage;
				}
			}
		}
		return maxUsageCache;
	}
}
