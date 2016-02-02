package de.bioviz.structures;

import java.util.ArrayList;

import java.util.Random;

/**
 * Created by keszocze on 27.07.15.
 *
 * @author Oliver Keszöcze
 */
public class Net {
	public final Point target;
	public final ArrayList<Source> sources = new ArrayList<Source>();


	public final int color;

	public Net(ArrayList<Source> sources, Point target) {
		this.target = target;
		this.sources.addAll(sources);
		Random rnd = new Random();

		// TODO be more sophisticated here ^^
		rnd.setSeed(target.fst+target.snd);
		color=rnd.nextInt();
	}

	public boolean containsDroplet(Droplet d) {
		return sources.stream().anyMatch(o -> o.dropletID == d.getID());
	}

}
