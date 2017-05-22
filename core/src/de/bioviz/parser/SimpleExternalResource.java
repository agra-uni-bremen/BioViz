package de.bioviz.parser;

import de.bioviz.structures.Direction;
import de.bioviz.structures.Point;

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
public class SimpleExternalResource {

		final Point gridPosition;
		final Direction dropletDirection;
		final Optional<Integer> id;


	/**
	 * @brief Constructor for creating a sink
	 * @param pos Position on the grid
	 * @param dir The direction in which the droplet is removed
	 */
		public SimpleExternalResource(Point pos, Direction dir) {
			gridPosition = pos;
			dropletDirection = dir;
			id = Optional.empty();
		}

	/**
	 * @brief Cosntructor for creating a dispenser
	 * @param pos Position on the grid
	 * @param dir The direction from which the droplet is dispensed
	 * @param id The type of fluid that is dispensed
	 */
		public SimpleExternalResource(Point pos, Direction dir, Integer id) {
			gridPosition = pos;
			dropletDirection = dir;
			this.id = Optional.of(id);
		}

}
