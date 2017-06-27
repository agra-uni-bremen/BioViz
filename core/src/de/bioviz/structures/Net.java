/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.structures;

import java.util.ArrayList;
import java.util.List;
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
	private final Rectangle target;


	/**
	 * The list of all starting points of this net's droplets.
	 */
	private final List<Source> sources = new ArrayList<>();


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
		this(sources, new Rectangle(target, 1, 1));
	}


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
	@SuppressWarnings("checkstyle:magicnumber")
	public Net(final List<Source> sources, final Rectangle target) {
		this.target = target;
		this.sources.addAll(sources);
		Random rnd = new Random();

		long upperRightX = (long) target.upperRight.fst;
		long upperRightY = (long) target.upperRight.snd;
		long lowerLeftX = (long) target.lowerLeft.fst;
		long lowerLeftY = (long) target.lowerLeft.snd;


		long seed = ((upperRightX % 65536) << 48) |
					((upperRightY % 65536) << 32) |
					((lowerLeftX % 65536) << 16) |
					(lowerLeftY % 65536);
		rnd.setSeed(seed);

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
			final Point ll = source.startPosition.lowerLeft;
			final Point ur = source.startPosition.upperRight;
			if (ll.fst < xMin) {
				xMin = ll.fst;
			}
			if (ll.snd < yMin) {
				yMin = ll.snd;
			}
			if (ur.fst > xMax) {
				xMax = ur.fst;
			}
			if (ur.snd > yMax) {
				yMax = ur.snd;
			}
		}

		final Point ll = target.lowerLeft;
		final Point ur = target.upperRight;
		if (ll.fst < xMin) {
			xMin = ll.fst;
		}
		if (ll.snd < yMin) {
			yMin = ll.snd;
		}
		if (ur.fst > xMax) {
			xMax = ur.fst;
		}
		if (ur.snd > yMax) {
			yMax = ur.snd;
		}

		final Rectangle netBoundingBox = new Rectangle(xMin, yMin, xMax, yMax);

		return netBoundingBox.contains(f.pos);
	}

	/**
	 * Returns the target of the net.
	 *
	 * @return the target this net's droplets are directed to.
	 */
	public Rectangle getTarget() {
		return target;
	}

	/**
	 * Returns the initial positions of the droplets of this net.
	 *
	 * @return the list of all starting points of this net's droplets.
	 */
	public List<Source> getSources() {
		return sources;
	}

}
