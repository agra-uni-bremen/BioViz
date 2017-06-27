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

package de.bioviz.parser;

import de.bioviz.structures.Direction;
import de.bioviz.structures.Point;
import de.bioviz.util.Pair;

import java.util.Optional;

/**
 * Convenience class to store commonly used data for external resources.
 * <p>
 * The idea is to store the position on the grid and the direction, from which
 * droplets are dispensed/to which droplets are removed. This class is intended
 * to be used within the parsing process only.
 * <p>
 * This class is only necessary as Java does not have type aliases.
 * There is no real inherent semantics to this class
 */
class SimpleExternalResource {

	/**
	 * The position of the resource that sits on the grid.
	 */
	final Point gridPosition;

	/**
	 * The position towards/from the external resource.
	 *
	 * The position of the actual sink or dispenser is not on the actual grid
	 * but next to it. This is indicated by this direction (starting from
	 * gridPosition).
	 *
	 */
	final Direction dropletDirection;

	/**
	 * ID of the dispensed fluid.
	 *
	 * If the id is empty then the resource is a sink.
	 */
	final Optional<Integer> id;


	/**
	 * @param posWithDir
	 * 		Pair of a) position to which the droplet is dispensed and b)
	 * 		direction
	 * 		from which the droplet is dispensed
	 * @param id
	 * 		The type of fluid that is dispensed
	 * @brief Constructor for creating a dispenser
	 */
	SimpleExternalResource(final Pair<Point, Direction> posWithDir, final
	Integer id) {
		gridPosition = posWithDir.fst;
		dropletDirection = posWithDir.snd;
		this.id = Optional.of(id);
	}

	/**
	 * @param posWithDir
	 * 		Pair of a) position from which the droplet is removed and b)
	 * 		direction in which the droplet is removed
	 * @brief Constructor for creating a sink
	 */
	SimpleExternalResource(final Pair<Point, Direction> posWithDir) {
		gridPosition = posWithDir.fst;
		dropletDirection = posWithDir.snd;
		this.id = Optional.empty();
	}

	/**
	 * @return The position of the actual resource in grid coordinates
	 */
	public Point resourcePosition() {
		Point directionAsPoint = Point.pointFromDirection(dropletDirection);
		Point resourcePosition = gridPosition.add(directionAsPoint);
		return resourcePosition;
	}

}
