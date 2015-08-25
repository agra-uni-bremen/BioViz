package de.bioviz.parser;

import de.bioviz.structures.Droplet;
import de.bioviz.structures.Point;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by keszocze on 25.08.15.
 *
 * @author Oliver Keszocze
 */
public class Validator {

	// TODO add check for positions that are outside of the grid
	ArrayList<String> checkPaths(ArrayList<Droplet> drops) {
		ArrayList<String> errors = new ArrayList<String>();
		for (Droplet drop : drops) {
			Vector<Point> points = drop.getPositions();

			if (points.isEmpty()) {
				errors.add("Droplet " + drop.getID() + " has no route attached to it");
			} else {
				if (points.size() >= 2) {
					Point prev = points.get(0);
					for (int i = 1; i < points.size(); i++) {
						Point curr = points.get(i);
						if (!Point.reachable(prev,curr)) {
							errors.add("Droplets "+ prev + " and "+curr +" are too close to each other");
						}
					}
				}
			}

		}
		return errors;
	}
}
