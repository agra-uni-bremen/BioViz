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
