package de.bioviz.parser;



import de.bioviz.structures.Biochip;
import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Pin;
import de.bioviz.structures.Actuation;
import de.bioviz.structures.ActuationVector;
import de.bioviz.structures.Detector;
import de.bioviz.structures.Direction;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Point;
import de.bioviz.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by keszocze on 25.08.15.
 * <p>
 * This class provides very basic validation of aspects of the chip.
 * <p>
 * The way this validator works is that every problem found will be added to an
 * error list that will be returned to the caller.
 *
 * @author Oliver Keszocze
 */
public final class Validator {
	/**
	 * Logger used for debugging purposes.
	 */
	static final Logger logger = LoggerFactory.getLogger(Validator.class);


	/**
	 * Private constructor to prevent instantiation of this class.
	 */
	private Validator() {

	}

	/**
	 * This method checks that all the positions a droplet moves along is
	 * actually a position of the chip.
	 *
	 * @param drops
	 * 		Droplets, whose positions should be validated
	 * @param points
	 * 		The possible positions of
	 * @return List of errors
	 */
	static ArrayList<String> checkPathsForPositions(
			final ArrayList<Droplet> drops, final Set<Point> points) {
		ArrayList<String> errors = new ArrayList<>();
		for (final Droplet drop : drops) {
			ArrayList<Point> ps = drop.getPositions();
			ps.forEach(p -> {
				if (!points.contains(p)) {
					errors.add("Droplet " + drop.getID() + ": position " + p +
							   " of route not on grid!");
				}
			});
		}

		return errors;
	}

	/**
	 * Checks whether paths move through blockages.
	 *
	 * @param chip
	 * 		Biochips that is checked for errors
	 * @return List of errors
	 */
	static ArrayList<String> checkPathForBlockages(final Biochip chip) {
		Set<Droplet> droplets = chip.getDroplets();
		ArrayList<String> errors = new ArrayList<>();

		for (final Droplet drop : droplets) {
			ArrayList<Point> positions = drop.getPositions();
			for (int i = 0; i < positions.size(); i++) {
				Point pos = positions.get(i);
				if (chip.hasFieldAt(pos)) {
					int timestep = i + drop.getSpawnTime();
					BiochipField field = chip.getFieldAt(pos);
					if (field.isBlocked(timestep)) {
						errors.add("Droplet " + drop.getID() +
								   " moves into blockage at " + pos +
								   " in time step " + timestep);
					}
				}
			}
		}

		return errors;
	}

	/**
	 * This method checks whether droplets only move a single cell in
	 * horizontal
	 * or vertical direction in one time step.
	 *
	 * @param drops
	 * 		Droplets whose positions on the grid will be checked for 'jumps'
	 * @return List of errors
	 */
	static ArrayList<String> checkPathsForJumps(
			final ArrayList<Droplet> drops) {
		ArrayList<String> errors = new ArrayList<String>();
		for (final Droplet drop : drops) {
			ArrayList<Point> points = drop.getPositions();

			if (points.isEmpty()) {
				errors.add("Droplet " + drop.getID() +
						   " has no route attached to it!");
			}
			else {
				if (points.size() >= 2) {
					Point prev = points.get(0);
					for (int i = 1; i < points.size(); i++) {
						Point curr = points.get(i);
						if (!Point.reachable(prev, curr)) {
							errors.add("Droplet " + drop.getID() +
									   ": Jump in route from " + prev + " to" +
									   " " +
									   curr + "!");
						}
						prev = curr;
					}
				}
			}

		}
		return errors;
	}

	/**
	 * Method that checks whether there are cells that have multiple pin
	 * assigned to them.
	 *
	 * @param pins
	 * 		List of pins that will be scanned for multiple aissgnments
	 * @return List of errors
	 * @note This method will generate error messages even if one cell gets
	 * assigned the same pin multiple times.
	 */
	static ArrayList<String> checkMultiplePinAssignments(
			final Collection<Pin> pins) {
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<String> errors = new ArrayList<>();

		for (final Pin pin : pins) {
			points.addAll(pin.cells);
		}

		Map<Point, Long> counts = points.stream().collect(
				Collectors.groupingBy(e -> e, Collectors.counting()));

		for (final Map.Entry<Point, Long> e : counts.entrySet()) {
			long count = e.getValue();
			if (count > 1) {
				errors.add(
						"Cell " + e.getKey() + " has multiple pins assigned:" +
						" " +
						count);
			}
		}

		return errors;
	}

	/**
	 * This method checks that all actuations sequences provided have the same
	 * length.
	 *
	 * @param cellActuations
	 * 		List of actuations given on the cell level (might be null)
	 * @param pinActuations
	 * 		List of actuations given on the pin level (might be nulll)
	 * @return List of errors
	 */
	static ArrayList<String> checkActuationVectorLengths(
			final HashMap<Point, ActuationVector> cellActuations,
			final HashMap<Integer, ActuationVector> pinActuations) {
		ArrayList<String> errors = new ArrayList<>();

		Integer cellActs = null;
		Integer pinActs = null;
		boolean addedCellError = false;

		if (cellActuations != null && !cellActuations.isEmpty()) {
			for (final Map.Entry<Point, ActuationVector> pair : cellActuations
					.entrySet()) {
				int len = pair.getValue().size();
				if (cellActs == null) {
					cellActs = len;
				}
				else {
					if (len != cellActs && !addedCellError) {
						errors.add("Different lengths in cell actuations");
						addedCellError = true;
					}
				}
			}
		}

		boolean addedPinError = false;
		boolean diffError = false;
		if (pinActuations != null && !pinActuations.isEmpty()) {
			for (final Map.Entry<Integer, ActuationVector> pair : pinActuations
					.entrySet()) {
				int len = pair.getValue().size();
				if (pinActs == null) {
					pinActs = len;
				}
				else {
					if (cellActs != null && len != cellActs &&
						!addedPinError) {
						errors.add("Different lengths in pin actuations");
						addedPinError = true;
					}
					if (pinActs != -1 && pinActs != len && !diffError) {
						errors.add(
								"Different lengths between cell and pin " +
								"actuations");
						diffError = true;
					}
				}
			}
		}
		return errors;
	}


	/**
	 * This method checks whether provided detectors can actually be placed on
	 * the chip. It can further remove conflicting detectors from the detectors
	 * list.
	 *
	 * @param chip
	 * 		The biochip that is supposed to contain the detectors
	 * @param detectors
	 * 		List of detectors to be placed on the chip (might be nulll)
	 * @param removeWrongDetectors
	 * 		if true, erroneous detectors will be removed from the list of
	 * 		detectors
	 * @return List of errors
	 */
	public static ArrayList<String>
	checkForDetectorPositions(final Biochip chip,
							  final ArrayList<Detector> detectors,
							  final boolean removeWrongDetectors) {
		ArrayList<String> errors = new ArrayList<>();

		if (detectors != null && !detectors.isEmpty()) {
			ArrayList<Detector> removeList = new ArrayList<>();
			for (final Detector det : detectors) {
				Point pos = det.position();
				try {
					chip.getFieldAt(pos);
				} catch (final RuntimeException e) {
					String msg = "Can not place detectors at position " + pos +
								 ". Position does not exist on chip.";
					if (removeWrongDetectors) {
						removeList.add(det);
						msg = msg + " Removed erroneous detector from list.";
					}
					errors.add(msg);
				}
			}
			detectors.removeAll(removeList);
		}
		return errors;
	}

	/**
	 * Check the validity of dispenser/sink positions.
	 * <p>
	 * A dispenser/sink itself must sit outside of the regular chip positions.
	 * Its target/source, on the other hand, must be a valid chip position.
	 *
	 * @param chip
	 * 		Biochip to check for errors
	 * @param type
	 * 		What is tested (dispensers or sinks)
	 * @param dir
	 * 		Position and direction of the dispenser
	 * @return Error message
	 */
	public static String checkOutsidePosition(
			final Biochip chip,
			final String type,
			final Pair<Point, Direction> dir) {
		String msg = "";
		boolean targetExists = true;
		boolean sourceExists = true;
		Point source = dir.fst.add(Point.pointFromDirection(dir.snd));


		try {
			chip.getFieldAt(dir.fst);
		} catch (final RuntimeException e) {
			targetExists = false;
		}

		try {
			chip.getFieldAt(source);
		} catch (final RuntimeException e) {
			sourceExists = false;
		}


		if (!targetExists) {
			msg = msg + type + " target " + dir.fst + " does not exist! [" +
				  dir + "] ";
		}
		if (sourceExists) {
			msg = msg + type + " source " + source + " is within the chip! [" +
				  dir + "]";
		}

		return msg;
	}


	/**
	 * Check the validity of sink positions.
	 * <p>
	 * A sink itself must sit outside of the regular chip positions. Its
	 * source,
	 * on the other hand, must be a valid chip position.
	 *
	 * @param chip
	 * 		The biochip to check
	 * @param sinks
	 * 		List of sinks to check
	 * @param removeWrongDirs
	 * 		If true, erroneous sinkgs will be removed
	 * @return List of errors
	 */
	public static ArrayList<String> checkSinkPositions(
			final Biochip chip,
			final ArrayList<Pair<Point, Direction>> sinks,
			final boolean removeWrongDirs) {
		ArrayList<String> errors = new ArrayList<>();

		if (sinks != null && !sinks.isEmpty()) {
			ArrayList<Pair<Point, Direction>> removeList =
					new ArrayList<>();
			for (final Pair<Point, Direction> sink : sinks) {
				String msg = checkOutsidePosition(chip, "Sink", sink);
				if (!msg.isEmpty()) {
					if (removeWrongDirs) {
						removeList.add(sink);
						msg = msg + " Removed invalid sink.";
					}

					errors.add(msg);
				}
			}
			sinks.removeAll(removeList);
		}
		return errors;
	}

	/**
	 * Check the validity of dispenser positions.
	 * <p>
	 * A dispenser itself must sit outside of the regular chip positions. Its
	 * target, on the other hand, must be a valid chip position.
	 *
	 * @param chip
	 * 		The biochip to check
	 * @param disps
	 * 		List of dispensers to check
	 * @param removeWrongDirs
	 * 		If true, erroneous sinkgs will be removed
	 * @return List of errors
	 */
	public static ArrayList<String> checkDispenserPositions(
			final Biochip chip,
			final ArrayList<Pair<Integer, Pair<Point, Direction>>> disps,
			final boolean removeWrongDirs) {

		ArrayList<String> errors = new ArrayList<>();

		if (disps != null && !disps.isEmpty()) {
			ArrayList<Pair<Integer, Pair<Point, Direction>>> removeList
					= new ArrayList<>();
			for (final Pair<Integer, Pair<Point, Direction>> dir : disps) {
				String msg = checkOutsidePosition(chip, "Dispenser", dir.snd);
				if (!msg.isEmpty()) {
					if (removeWrongDirs) {
						removeList.add(dir);
						msg = msg + " Removed invalid dispenser.";
					}

					errors.add(msg);
				}

			}
			disps.removeAll(removeList);
		}
		return errors;
	}

	/**
	 * Checks the compatibility of two actuation vectors.
	 * <p>
	 * There are two modes of operation: strong and non-strong. In the strong
	 * mode, every entry has to be identical, i.e. 'X' and '0'/'1' is not
	 * counted as compatible.
	 *
	 * @param v1
	 * 		First actuation vector
	 * @param v2
	 * 		Second actuation vector
	 * @param errors
	 * 		List of errors that is filled by this method
	 * @param strong
	 * 		Whether strong matching should be performed
	 * @param what
	 * 		Free text that is added to the error message
	 */
	private static void compatibility(final ActuationVector v1,
									  final ActuationVector v2,
									  final ArrayList<String> errors,
									  final boolean strong,
									  final String what) {


		if (v1 != null && v1.size() == v2.size()) {
			logger.debug("Comparing actuation vectors {} <-> {}", v1, v2);
			for (int i = 0; i < v1.size(); i++) {
				Actuation a = v1.get(i);
				Actuation b = v2.get(i);
				if (a != b) {
					if (strong) {
						errors.add("Cell and pin actuations for " + what +
								   " are not strongly compatible.");
						break;
					}
					else {
						if (a != Actuation.DONTCARE &&
							b != Actuation.DONTCARE) {
							errors.add("Cell and pin actuations for " + what +
									   " are not weakly compatible.");
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Checks that the actuations specified pin-wise and cell-wise match.
	 * <p>
	 * There are two modes of operation: strong and non-strong. In the strong
	 * mode, every entry has to be identical, i.e. 'X' and '0'/'1' is not
	 * counted as compatible.
	 *
	 * @param chip
	 * 		Biochip to test
	 * @param cellActuations
	 * 		Actuations defined by cells
	 * @param pinActuations
	 * 		Actuations defined by pins
	 * @param strongCompatibility
	 * 		Whether strong matching should be performed
	 * @return List of errors
	 */
	public static ArrayList<String> checkCellPinActuationCompatibility(
			final Biochip chip,
			final HashMap<Point, ActuationVector> cellActuations,
			final HashMap<Integer, ActuationVector> pinActuations,
			final boolean strongCompatibility) {

		ArrayList<String> errors = new ArrayList<>();

		/*
		Check that every cell actuation matches with the pin actuation that is
		attached to the cell. Note that not necessarily both  (in this case the
		pin actuation) actuations need to be specified
		 */
		//logger.debug("cellActuations.entrySet()={}", cellActuations.entrySet
		// ());
		for (final Map.Entry<Point, ActuationVector> e : cellActuations
				.entrySet()) {
			Point p = e.getKey();
			ActuationVector cellActVec = e.getValue();
			//logger.debug("Working on cell actuation {}", e);

			Pin pin = chip.getFieldAt(p).pin;
			//logger.debug("Pin for position {}: {}", p, pin);
			if (pin != null) {
				ActuationVector pinActVec = chip.pinActuations.get(pin.pinID);
				//logger.debug("pin={} pinActVec={}", pin, pinActVec);
				compatibility(pinActVec, cellActVec, errors,
							  strongCompatibility,
							  "field at " + p + " and pin " + pin.pinID);
			}
		}


		/*
		Check that every pin actuation matches with the cell actuations
		associated
		with this pin. Note that not necessarily both  (in this case the
		cell actuations) actuations need to be specified. It is necessary to
		check the compatibility in both directions (cell -> pin & pin -> cells)
		as it is possible that both inputs in the file specify different sets
		 */

		//logger.debug("pinActuations.entrySet().size()={}",pinActuations
		// .entrySet().size());
		for (
				final Map.Entry<Integer, ActuationVector> e
				: pinActuations.entrySet())

		{
			Integer pinID = e.getKey();
			ActuationVector pinActVec = e.getValue();
			List<BiochipField> fields = chip.getFieldsForPin(pinID);

			//logger.debug("Entry: {}",e);
			//logger.debug("Working on pin actuation vector for pin {}:{}",
			// pinID,pinActVec);

			if (!fields.isEmpty()) {
				for (final BiochipField f : fields) {
					ActuationVector cellActVec = f.actVec;
					//logger.debug("pin -> cell: comparing {} and {}",
					// pinActVec,cellActVec);
					compatibility(cellActVec, pinActVec, errors,
								  strongCompatibility,
								  "pin with ID " + pinID + " and cell at " +
								  f.pos);
				}

			}
		}

		return errors;
	}
}
