package de.bioviz.structures;

import java.util.ArrayList;

/**
 * Created by keszocze on 27.07.15.
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
		this.getSources().addAll(sources);
	}

	/**
	 * Checks whether this net contains the given droplet.
	 * @param d the droplet that is supposedly part of this net
	 * @return true if it is part of this net, otherwise false
	 */
	public boolean containsDroplet(final Droplet d) {
		// TODO: Shouldn't this also check if the target matches?
		for (final Source s:getSources()) {
			if (s.dropletID == d.getID()) {
				return true;
			}
		}
		return false;
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
