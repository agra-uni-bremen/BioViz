package de.bioviz.parser;

import de.bioviz.structures.Droplet;
import de.bioviz.structures.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

/**
 * Created by keszocze on 25.08.15.
 *
 * @author Oliver Keszocze
 */
public class Validator {
	static final Logger logger = LoggerFactory.getLogger(Validator.class);

	// TODO add check for positions that are outside of the grid
	static ArrayList<String> checkPaths(Set<Droplet> drops) {
		ArrayList<String> errors = new ArrayList<String>();
		for (Droplet drop: drops) {
			Vector<Point> points = drop.getPositions();
//			logger.debug("Evaluating router of length {} for droplet {}",points.size(),drop.getID());

			if (points.isEmpty()) {
				errors.add("Droplet " + drop.getID() + " has no route attached to it");
			} else {
				if (points.size() >= 2) {
					Point prev = points.get(0);
					for (int i = 1; i < points.size(); i++) {
						Point curr = points.get(i);
//						logger.debug("looking at points {} and {}",prev,curr);
//						logger.debug("reachable: {}",Point.reachable(prev,curr));
						if (!Point.reachable(prev,curr)) {
							errors.add("Droplets "+ prev + " and "+curr +" are too far away of each other");
						}
						prev=curr;
					}
				}
			}

		}
		return errors;
	}
}
