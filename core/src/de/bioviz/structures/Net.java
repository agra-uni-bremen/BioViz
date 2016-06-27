package de.bioviz.structures;

import java.util.ArrayList;
import java.util.Random;

import de.bioviz.ui.Color;

/**
 * A net as used for routing.
 * <p>
 * A net defines how droplets are to be routed. It consists of a target
 * destination and at least one droplet that is to be transported to that
 * destination. If multiple droplets are routed to that destination (by the same
 * net!), they are allowed to violate the fluidic constraints between each other
 * and have to be at the destination simultaneously.
 *
 * @author Oliver Keszocze
 */
public final class Net {


	/**
	 * The color for this net.
	 * <p>
	 * It is used when the option to color all droplets within a net with the
	 * same color is chosen.
	 */
	private Color color = new Color();


	/**
	 * The target this net's droplets are directed to.
	 */
	private final Point target;


	/**
	 * The list of all starting points of this net's droplets.
	 */
	private final ArrayList<Source> sources = new ArrayList<Source>();

	/**
	 * <p>Creates a <i>net</i>. That is, a structure that describes for a
	 * set of
	 * droplets with a common target.</p> <p>This class is basically immutable.
	 * The sources/target combination you set in this constructor are
	 * final.</p>
	 *
	 * @param sources
	 * 		this net's sources
	 * @param target
	 * 		this net's droplets' common target
	 */
	public Net(final ArrayList<Source> sources, final Point target) {
		this.target = target;
		this.sources.addAll(sources);
		Random rnd = new Random();

		// TODO be more sophisticated here ^^
		rnd.setSeed((long)target.fst + target.snd); // cast to long to avoid losing precision
		color = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(),
						  1f);
	}


	/**
	 * Returns the net's color.
	 *
	 * @return The net's color
	 */
	public Color getColor() {
		return color.cpy();
	}

	/**
	 * Sets the color of the net.
	 *
	 * @param c
	 * 		The new color of the net.
	 */
	public void setColor(final Color c) {
		this.color = c;
	}

	/**
	 * Checks whether this net contains the given droplet.
	 *
	 * @param d
	 * 		the droplet that is supposedly part of this net
	 * @return true if it is part of this net, otherwise false
	 */
	public boolean containsDroplet(final Droplet d) {
		return sources.stream().anyMatch(o -> o.dropletID == d.getID());
	}

	/**
	 * Checks whether this net contains the given field.
	 *
	 * @param f
	 * 		the field that is supposedly part of this net
	 * @return true if it is part of this net, otherwise false
	 */
	public boolean containsField(final BiochipField f) {
		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE;
		int yMax = Integer.MIN_VALUE;

		for (final Source source : sources) {
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

		return f.pos.fst >= xMin && f.pos.fst <= xMax &&
			   f.pos.snd >= yMin && f.pos.snd <= yMax;
	}

	/**
	 * Returns the target of the net.
	 *
	 * @return the target this net's droplets are directed to.
	 */
	public Point getTarget() {
		return target;
	}

	/**
	 * Returns the initial positions of the droplets of this net.
	 *
	 * @return the list of all starting points of this net's droplets.
	 */
	public ArrayList<Source> getSources() {
		return sources;
	}

}
