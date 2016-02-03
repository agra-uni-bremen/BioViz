package de.bioviz.structures;

import java.util.ArrayList;

import java.util.Random;

/**
 * Created by keszocze on 27.07.15.
 *
 * @author Oliver Keszöcze
 */
public final class Net {

	/**
	 * The target this net's droplets are directed to.
	 */
	private final Point target;

	/**
	 * The list of all starting points of this net's droplets.
	 */
	private final ArrayList<Source> sources = new ArrayList<Source>();
	
	public final int color;

	/**
	 * <p>Creates a <i>net</i>. That is, a structure that describes for a set of
	 * droplets with a common target.</p>
	 * <p>This class is basically immutable. The sources/target combination you
	 * set in this constructor are final.</p>
	 * @param sources this net's sources
	 * @param target this net's droplets' common target
	 */
	public Net(final ArrayList<Source> sources, final Point target) {
		this.target = target;
		this.sources.addAll(sources);
		Random rnd = new Random();
		
		// TODO be more sophisticated here ^^
		rnd.setSeed(target.fst+target.snd);
		color=rnd.nextInt();
	}

	/**
	 * Checks whether this net contains the given droplet.
	 * @param d the droplet that is supposedly part of this net
	 * @return true if it is part of this net, otherwise false
	 */
	public boolean containsDroplet(Droplet d) {
		return sources.stream().anyMatch(o -> o.dropletID == d.getID());
	}
	
	/**
	 * Checks whether this net contains the given field.
	 * @param f the field that is supposedly part of this net
	 * @return true if it is part of this net, otherwise false
	 */
	public boolean containsField(BiochipField f) {
		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE,
			xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;

		for (Source source : sources) {
			if (source.startPosition.fst < xMin) {
				xMin = source.startPosition.fst;
			}
			if (source.startPosition.snd < yMin) {
				yMin = source.startPosition.snd;
			}
			if (source.startPosition.fst > xMax) {
				xMax = source.startPosition.fst;
			}
			if (source.startPosition.snd > yMax) {
				yMax = source.startPosition.snd;
			}
		}
		if (target.fst < xMin) {
			xMin = target.fst;
		}
		if (target.snd < yMin) {
			yMin = target.snd;
		}
		if (target.fst > xMax) {
			xMax = target.fst;
		}
		if (target.snd > yMax) {
			yMax = target.snd;
		}
		
		return (f.pos.fst >= xMin && f.pos.fst <= xMax &&
				f.pos.snd >= yMin && f.pos.snd <= yMax);
	}

	/**
	 * @return the target this net's droplets are directed to.
	 */
	public Point getTarget() {
		return target;
	}

	/**
	 * @return the list of all starting points of this net's droplets.
	 */
	public ArrayList<Source> getSources() {
		return sources;
	}

}
