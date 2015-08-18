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

	public boolean containsDroplet(Droplet d) {
		for (Source s:sources) {
			if (s.dropletID == d.getID()) {
				return true;
			}
		}
		return false;
	}

}
