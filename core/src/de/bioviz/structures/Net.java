package de.bioviz.structures;

import java.util.ArrayList;

/**
 * Created by keszocze on 27.07.15.
 */
public class Net {
	public final Point target;
	public final ArrayList<Source> sources=new ArrayList<Source>();

	public Net(ArrayList<Source> sources, Point target) {
		this.target = target;
		this.sources.addAll(sources);
	}

}
