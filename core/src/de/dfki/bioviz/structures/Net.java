package de.dfki.bioviz.structures;

import de.dfki.bioviz.util.Pair;

import java.util.ArrayList;

/**
 * Created by keszocze on 27.07.15.
 */
public class Net {
	private Point target;
	private ArrayList<Source> sources;

	public Net(ArrayList<Source> sources, Point target) {
		this.target = target;
		this.sources.addAll(sources);
	}

}
