package de.dfki.bioviz.structures;

import de.dfki.bioviz.util.Pair;

import java.util.ArrayList;

/**
 * Created by keszocze on 27.07.15.
 */
public class Net {
	private Point target;
	private ArrayList<Pair<Integer,Point>> sources;

	Net(ArrayList<Pair<Integer,Point>> sources, Point target) {
		this.target = target;
		this.sources.addAll(sources);
	}

}
