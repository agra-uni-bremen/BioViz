package de.bioviz.parser;

import de.bioviz.structures.ActuationVector;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by keszocze on 25.08.15.
 *
 * @author Oliver Keszocze
 */
public class Validator {
	static final Logger logger = LoggerFactory.getLogger(Validator.class);


	static ArrayList<String> checkPathsForPositions(ArrayList<Droplet> drops, Set<Point> points) {
		ArrayList<String> errors = new ArrayList<String>();
		for (Droplet drop: drops) {
			Vector<Point> ps = drop.getPositions();
			ps.forEach(p -> {
				if (!points.contains(p)) {
					errors.add("Droplet "+drop.getID()+": position " + p + " of route not on grid!");
				}
			});
		}

		return errors;
	}

	static ArrayList<String> checkPathsForJumps(ArrayList<Droplet> drops) {
		ArrayList<String> errors = new ArrayList<String>();
		for (Droplet drop: drops) {
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
						if (!Point.reachable(prev,curr)) {
							errors.add("Droplet " + drop.getID() + ": Jump in route from " + prev + " to "+curr+"!");
						}
						prev=curr;
					}
				}
			}

		}
		return errors;
	}

	static ArrayList<String> checkActuationVectorLenghts(HashMap<Point, ActuationVector> cellActuations, HashMap<Integer, ActuationVector> pinActuations) {
		ArrayList<String> errors = new ArrayList<String>();

		int cellActs = -1;
		int pinActs = -1;
		boolean addedCellError=false;

		if (cellActuations != null && !cellActuations.isEmpty()) {
			for (Map.Entry<Point, ActuationVector> pair : cellActuations.entrySet()) {
				int len = pair.getValue().size();
				if (cellActs == -1) {
					cellActs = len;
				} else {
					if (len != cellActs && !addedCellError) {
						errors.add("Different lengths in cell actuations");
						addedCellError=true;
					}
				}
			}
		}

		boolean addedPinError=false;
		boolean diffError=false;
		if (pinActuations != null && !pinActuations.isEmpty()) {
			for (Map.Entry<Integer, ActuationVector> pair : pinActuations.entrySet()) {
				int len = pair.getValue().size();
				if (pinActs == -1) {
					pinActs = len;
				} else {
					if (len != cellActs && !addedPinError) {
						errors.add("Different lengths in pin actuations");
						addedPinError=true;
					}
					if (pinActs != -1 && pinActs != len && !diffError) {
						errors.add("Different lengths between cell and pin actuations");
						diffError=true;
					}
				}
			}
		}
		return errors;
	}
}
