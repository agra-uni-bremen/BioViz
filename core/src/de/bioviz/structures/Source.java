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

/**
 * <p>
 * Class representing a source of a net.
 * <p>
 * A source is the starting point of a droplet in a routing problem. Therefore,
 * this class stores the initial position of that droplet, the point in time the
 * droplet appears at that position and its id. We do not store an actual {@link
 * Droplet} object!
 * <p>
 * Please note that this 'source' has nothing to do with releasing a droplet
 * from a dispenser onto a chip!
 *
 * @author Oliver Keszocze
 */
public class Source {

	/**
	 * The initial position of the droplet.
	 */
	public final Rectangle startPosition;

	/**
	 * The droplet's unique ID.
	 */
	public final int dropletID;

	/**
	 * The time step the droplet first appears at {@link Source#startPosition}.
	 */
	public final int spawnTime;

	/**
	 * Creates a source that uses the first time step as the starting time.
	 * <p>
	 * This constructor is used to create a conventional (i.e. non-meda)
	 * source.
	 *
	 * @param dropletID
	 * 		Unique ID of the droplet that is to be routed
	 * @param pos
	 * 		The initial position of the droplet
	 */
	public Source(final int dropletID, final Point pos) {
		this(dropletID, pos, 1, new Point(1, 1));
	}

	/**
	 * Creates a source with the default starting time of one for meda sources.
	 *
	 * @param dropletID
	 * 		Unique ID of the droplet that is to be routed
	 * @param pos
	 * 		The initial position of the droplet
	 * @param size
	 * 		The size of the source.
	 */
	public Source(final int dropletID, final Point pos, final Point size) {
		this(dropletID, pos, 1, size);
	}

	/**
	 * Creates a source with a defined starting time.
	 *
	 * @param dropletID
	 * 		Unique ID of the droplet that is to be routed
	 * @param pos
	 * 		The initial upper left corner position of the droplet
	 * @param spawnTime
	 * 		The time step the droplet appears on the chip
	 * @param size
	 * 		The size of the source. (1,1) for non-meda sources.
	 */
	public Source(final int dropletID,
				  final Point pos,
				  final int spawnTime,
				  final Point size) {
		this(dropletID, new Rectangle(pos, size.fst, size.snd), spawnTime);
	}

	/**
	 * @param dropletID
	 * 		Unique ID of the droplet that is to be routed.
	 * @param pos
	 * 		The initial position of the droplet.
	 * @param spawnTime
	 * 		The time step the droplet appears on the chip.
	 */
	public Source(final int dropletID, final Rectangle pos, final int
			spawnTime) {
		this.dropletID = dropletID;
		this.startPosition = pos;
		this.spawnTime = spawnTime;
	}

	@Override
	/**
	 * Returns the string representation of a Source.
	 *
	 * @return "<ID>:<startPosition>"
	 */
	public String toString() {
		return Integer.toString(dropletID) + ": " + startPosition;
	}
}
