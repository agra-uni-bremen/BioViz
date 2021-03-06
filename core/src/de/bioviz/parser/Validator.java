/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.parser;


import de.bioviz.structures.Actuation;
import de.bioviz.structures.ActuationVector;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Pin;
import de.bioviz.structures.Point;
import de.bioviz.structures.Rectangle;
import de.bioviz.structures.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
final class Validator {
	/**
	 * Logger used for debugging purposes.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(Validator.class);


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
			ArrayList<Rectangle> ps = drop.getPositions();

			ps.forEach(rect ->
							   rect.positions().forEach(pos -> {
								   if (!points.contains(pos)) {
									   errors.add("Droplet " + drop.getID() +
												  ": position " + pos +
												  " of route not on grid!");
								   }

							   })

			);
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
			ArrayList<Rectangle> rectPositions = drop.getPositions();


			for (int timeStep = 0;
				 timeStep < rectPositions.size(); timeStep++) {
				int timestep = timeStep + drop.getSpawnTime();
				Rectangle rectPos = rectPositions.get(timeStep);
				ArrayList<Point> positions =
						rectPos.positions().stream().
								filter(chip::hasFieldAt).
								collect(Collectors.toCollection(
										ArrayList::new));

				positions.stream().
						filter(
								pos -> chip.getFieldAt(pos).isBlocked(
										timestep)).
						forEach(
								pos -> errors.add(
										"Droplet " + drop.getID() +
										" moves into blockage at " + pos +
										" in time step " + timestep)
						);

			}
		}
		return errors;
	}

	/**
	 * This method checks whether droplets only move a single cell in
	 * horizontal
	 * or vertical direction in one time step.
	 * <p>
	 * It also checks whether a route has been attached to the droplet at all.
	 *
	 * @param drops
	 * 		Droplets whose positions on the grid will be checked for 'jumps'
	 * @return List of errors
	 */
	static ArrayList<String> checkPathsForJumps(
			final ArrayList<Droplet> drops) {
		ArrayList<String> errors = new ArrayList<>();

		for (final Droplet drop : drops) {
			ArrayList<Rectangle> points = drop.getPositions();

			if (points.isEmpty()) {
				errors.add("Droplet " + drop.getID() +
						   " has no route attached to it!");
				return errors;
			}

			/**
			 * This additional check is just here to make the indentation level
			 * smaller.
			 */
			if (points.size() == 1) {
				return errors;
			}

			Rectangle prev = points.get(0);
			for (int i = 1; i < points.size(); i++) {
				Rectangle curr = points.get(i);
				boolean upperLeftBadMove =
						!Point.reachable(prev.upperLeft(), curr.upperLeft());
				boolean upperRightBadMove =
						!Point.reachable(prev.upperRight, curr.upperRight);
				boolean lowerLeftBadMove =
						!Point.reachable(prev.lowerLeft, curr.lowerLeft);
				boolean lowerRightBadMove =
						!Point.reachable(prev.lowerRight(), curr.lowerRight());


				if (upperLeftBadMove || upperRightBadMove ||
					lowerLeftBadMove || lowerRightBadMove) {
					errors.add("Droplet " + drop.getID() +
							   ": Jump in route from " + prev + " to " + curr +
							   "!");
				}
				prev = curr;
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
						"Cell " + e.getKey() +
						" has multiple pins assigned:" +
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
	 * 		List of actuations given on the pin level (might be null)
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
			for (final Map.Entry<Point, ActuationVector> pair :
					cellActuations.entrySet()) {
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
			for (final Map.Entry<Integer, ActuationVector> pair :
					pinActuations
							.entrySet()) {
				int len = pair.getValue().size();
				if (pinActs == null) {
					pinActs = len;
				} else {
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
	 * This method checks whether provided resources can actually be placed on
	 * the chip. Also, offending resources are removed.
	 *
	 * @param chip
	 * 		The biochip that is supposed to contain the resources
	 * @param resourceType
	 * 		The name of the resources that is checked
	 * @param resources
	 * 		List of resources to be placed on the chip (might be nulll)
	 * @return List of errors
	 */
	static List<String>
	checkForPositions(final Biochip chip,
					  final String resourceType,
					  final ArrayList<? extends Resource> resources) {
		ArrayList<String> errors = new ArrayList<>();
		ArrayList<Resource> toRemove = new ArrayList<>();

		// check this way to get rid of indentation issues
		if (resources == null) {
			return errors;
		}

		for (final Resource res : resources) {
			Rectangle pos = res.position;

			if (!chip.allPresent(pos)) {
				String msg = "Cannot place " + resourceType + " at position " +
							 pos + "! Some positions do not exit on the chip.";
				errors.add(msg);
				toRemove.add(res);
			}
		}
		resources.removeAll(toRemove);
		return errors;
	}

	/**
	 * Checks whether the given list of resources is going to be added to
	 * fields
	 * that already have an attached resource.
	 *
	 * @param chip
	 * 		The biochip that is supposed to contain the resources
	 * @param resourceType
	 * 		The name of the resources that is checked
	 * @param resources
	 * 		List of resources to be placed on the chip (might be nulll)
	 * @return List of errors
	 */
	static List<String>
	checkForResources(final Biochip chip,
					  final String resourceType,
					  final ArrayList<? extends Resource> resources) {
		ArrayList<String> errors = new ArrayList<>();

		// check this way to get rid of indentation issues
		if (resources == null) {
			return errors;
		}

		for (final Resource res : resources) {
			Rectangle pos = res.position;

			if (chip.hasResource(pos)) {
				String msg = "Cannot place " + resourceType + " at position " +
							 pos + "! Another resource is already present";
				errors.add(msg);
			}
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
	 * @param resource
	 * 		The simple resource description
	 * @return Error message
	 */

	private static String checkOutsidePosition(
			final Biochip chip,
			final SimpleExternalResource resource) {
		String msg = "";
		boolean targetExists = chip.hasFieldAt(resource.gridPosition);

		Point resourcePosition = resource.resourcePosition();
		boolean sourceExists = chip.hasFieldAt(resourcePosition);


		// If an ID was given that ID represents what kind of fluid is to be
		// dispensed => the resource is a dispenser
		String type = resource.id.isPresent() ? "Dispenser" : "Sink";


		if (!targetExists) {
			msg = msg + type + " target " + resource.gridPosition + " does not exist!";
		}
		if (sourceExists) {
			msg = msg + type + " source " + resourcePosition + " is within the chip!";
		}

		return msg;
	}

	/**
	 * Check the validity of dispenser/sink positions.
	 * <p>
	 * A dispenser itself must sit outside of the regular chip positions. Its
	 * target, on the other hand, must be a valid chip position.
	 *
	 * @param chip
	 * 		The biochip to check
	 * @param resources
	 * 		List of resources to check
	 * @param removeWrongDirs
	 * 		If true, erroneous resources will be removed
	 * @return List of errors
	 */
	public static List<String> checkExternalResourcePositions(
			final Biochip chip,
			final List<SimpleExternalResource> resources,
			final boolean removeWrongDirs) {
		ArrayList<String> errors = new ArrayList<>();

			ArrayList<SimpleExternalResource> removeList
					= new ArrayList<>();
			for (final SimpleExternalResource resource : resources) {
				String msg = checkOutsidePosition(chip, resource);
				if (!msg.isEmpty()) {
					if (removeWrongDirs) {
						removeList.add(resource);
						msg = msg + " Removed invalid resource.";
					}
					errors.add(msg);
				}
			}
		resources.removeAll(removeList);
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
					} else {
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
	static ArrayList<String> checkCellPinActuationCompatibility(
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
		for (final Map.Entry<Point, ActuationVector> e : cellActuations
				.entrySet()) {
			Point p = e.getKey();
			ActuationVector cellActVec = e.getValue();

			Pin pin = chip.getFieldAt(p).pin;
			if (pin != null) {
				ActuationVector pinActVec = chip.pinActuations.get(pin.pinID);
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
		for (
				final Map.Entry<Integer, ActuationVector> e
				: pinActuations.entrySet()) {
			Integer pinID = e.getKey();
			ActuationVector pinActVec = e.getValue();
			List<BiochipField> fields = chip.getFieldsForPin(pinID);

			if (!fields.isEmpty()) {
				for (final BiochipField f : fields) {
					ActuationVector cellActVec = f.actVec;

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
