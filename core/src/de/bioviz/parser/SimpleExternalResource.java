package de.bioviz.parser;

import de.bioviz.structures.Direction;
import de.bioviz.structures.Point;
import de.bioviz.util.Pair;

import java.util.Optional;

/**
 * Convenience class to store commonly used data for external resources.
 *
 * The idea is to store the position on the grid and the direction, from which
 * droplets are dispensed/to which droplets are removed. This class is intended
 * to be used within the parsing process only.
 *
 * This class is only necessary as Java does not have type aliases.
 * There is no real inherent semantics to this class
 */
class SimpleExternalResource {

		final Point gridPosition;
		final Direction dropletDirection;
		final Optional<Integer> id;



	/**
	 * @brief Constructor for creating a dispenser
	 * @param posWithDir Pair of
	 *                  a) position to which the droplet is dispensed and
	 *                  b) direction from which the droplet is dispensed
	 * @param id The type of fluid that is dispensed
	 */
	public SimpleExternalResource(Pair<Point, Direction> posWithDir, Integer id) {
		gridPosition = posWithDir.fst;
		dropletDirection = posWithDir.snd;
		this.id = Optional.of(id);
	}

	/**
	 * @brief Constructor for creating a sink
	 * @param posWithDir Pair of
	 *                  a) position from which the droplet is removed and
	 *                  b) direction in which the droplet is removed
	 */
	public SimpleExternalResource(Pair<Point, Direction> posWithDir) {
		gridPosition = posWithDir.fst;
		dropletDirection = posWithDir.snd;
		this.id = Optional.empty();
	}

	/**
	 *
	 * @return The position of the actual resource in grid coordinates
	 */
	public Point resourcePosition() {
		Point directionAsPoint = Point.pointFromDirection(dropletDirection);
		Point resourcePosition = gridPosition.add(directionAsPoint);
		return resourcePosition;
	}

}
