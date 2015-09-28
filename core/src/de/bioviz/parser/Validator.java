package de.bioviz.parser;

import de.bioviz.structures.*;
import de.bioviz.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
public class Validator {
	static final Logger logger = LoggerFactory.getLogger(Validator.class);


	/**
	 * This method checks that all the positions a droplet moves along is
	 * actually a position of the chip
	 *
	 * @param drops
	 * 		Droplets, whose positions should be valided
	 * @param points
	 * 		The possible positions of
	 * @return List of errors
	 */
	static ArrayList<String> checkPathsForPositions(ArrayList<Droplet> drops, Set<Point> points) {
		ArrayList<String> errors = new ArrayList<String>();
		for (Droplet drop : drops) {
			Vector<Point> ps = drop.getPositions();
			ps.forEach(p -> {
				if (!points.contains(p)) {
					errors.add("Droplet " + drop.getID() + ": position " + p + " of route not on grid!");
				}
			});
		}

		return errors;
	}

	/**
	 * This method checks whether droplets only move a single cell in horizontal
	 * or vertical direction in one time step
	 *
	 * @param drops
	 * 		Droplets whose positions on the grid will be checked for 'jumps'
	 * @return List of errors
	 */
	static ArrayList<String> checkPathsForJumps(ArrayList<Droplet> drops) {
		ArrayList<String> errors = new ArrayList<String>();
		for (Droplet drop : drops) {
			Vector<Point> points = drop.getPositions();
//			logger.debug("Evaluating router of length {} for droplet {}",points.size(),drop.getID());

			if (points.isEmpty()) {
				errors.add("Droplet " + drop.getID() + " has no route attached to it!");
			} else {
				if (points.size() >= 2) {
					Point prev = points.get(0);
					for (int i = 1; i < points.size(); i++) {
						Point curr = points.get(i);
//						logger.debug("looking at points {} and {}",prev,curr);
//						logger.debug("reachable: {}",Point.reachable(prev,curr));
						if (!Point.reachable(prev, curr)) {
							errors.add("Droplet " + drop.getID() + ": Jump in route from " + prev + " to " + curr + "!");
						}
						prev = curr;
					}
				}
			}

		}
		return errors;
	}


	// TODO Somehow also generate the list of assigned pins to the cells

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
	static ArrayList<String> checkMultiplePinAssignments(Collection<Pin> pins) {
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<String> errors = new ArrayList<String>();

		for (Pin pin : pins) {
			points.addAll(pin.cells);
		}

		Map<Point, Long> counts = points.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

		for (Map.Entry<Point, Long> e : counts.entrySet()) {
			long count = e.getValue();
			if (count > 1) {
				errors.add("Cell " + e.getKey() + " has multiple pins assigned: " + count);
			}
		}

		return errors;
	}

	/**
	 * This method checks that all actuations sequences provided have the same
	 * length
	 *
	 * @param cellActuations
	 * 		List of actuations given on the cell level (might be null)
	 * @param pinActuations
	 * 		List of actuations given on the pin level (might be nulll)
	 * @return List of errors
	 */
	static ArrayList<String> checkActuationVectorLenghts(HashMap<Point, ActuationVector> cellActuations, HashMap<Integer, ActuationVector> pinActuations) {
		ArrayList<String> errors = new ArrayList<String>();

		Integer cellActs = null;
		Integer pinActs = null;
		boolean addedCellError = false;

		if (cellActuations != null && !cellActuations.isEmpty()) {
			for (Map.Entry<Point, ActuationVector> pair : cellActuations.entrySet()) {
				int len = pair.getValue().size();
				if (cellActs == null) {
					cellActs = len;
				} else {
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
			for (Map.Entry<Integer, ActuationVector> pair : pinActuations.entrySet()) {
				int len = pair.getValue().size();
				if (pinActs == null) {
					pinActs = len;
				} else {
					if (len != cellActs && !addedPinError) {
						errors.add("Different lengths in pin actuations");
						addedPinError = true;
					}
					if (pinActs != -1 && pinActs != len && !diffError) {
						errors.add("Different lengths between cell and pin actuations");
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
	public static ArrayList<String> checkForDetectorPositions(Biochip chip, ArrayList<Detector> detectors, boolean removeWrongDetectors) {
		ArrayList<String> errors = new ArrayList<String>();

		if (detectors != null && !detectors.isEmpty()) {
			ArrayList<Detector> removeList = new ArrayList<Detector>();
			for (Detector det : detectors) {
				Point pos = det.position();
				try {
					chip.getFieldAt(pos);
				} catch (RuntimeException e) {
					String msg = "Can not place detectors at position " + pos + ". Position does not exist on chip.";
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

	public static String checkOutsidePosition(Biochip chip, String type, Pair<Point, Direction> dir) {
		String msg = "";
		boolean targetExists = true;
		boolean sourceExists = true;
		Point source = dir.first.add(Point.pointFromDirection(dir.second));


		try {
			chip.getFieldAt(dir.first);
		} catch (RuntimeException e) {
			targetExists = false;
		}

		try {

			chip.getFieldAt(source);
		} catch (RuntimeException e) {
			sourceExists = false;
		}


		if (!targetExists) {
			msg = msg + type + " target " + dir.first + " does not exist! [" + dir + "] ";
		}
		if (sourceExists) {
			msg = msg + type + " source " + source + " is within the chip! [" + dir + "]";
		}

		return msg;
	}

	public static ArrayList<String> checkSinkPositions(Biochip chip, ArrayList<Pair<Point, Direction>> sinks, boolean removeWrongDirs) {
		ArrayList<String> errors = new ArrayList<String>();

		if (sinks != null && !sinks.isEmpty()) {
			ArrayList<Pair<Point, Direction>> removeList = new ArrayList<Pair<Point, Direction>>();
			for (Pair<Point, Direction> sink : sinks) {
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


	public static ArrayList<String> checkDispenserPositions(
			Biochip chip,
			ArrayList<Pair<Integer, Pair<Point, Direction>>> disps,
			boolean removeWrongDirs) {

		ArrayList<String> errors = new ArrayList<String>();

		if (disps != null && !disps.isEmpty()) {
			ArrayList<Pair<Integer, Pair<Point, Direction>>> removeList
					= new ArrayList<Pair<Integer, Pair<Point, Direction>>>();
			for (Pair<Integer, Pair<Point, Direction>> dir : disps) {
				String msg = checkOutsidePosition(chip, "Dispenser", dir.second);
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
}
