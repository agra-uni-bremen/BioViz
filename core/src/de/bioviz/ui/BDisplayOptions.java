package de.bioviz.ui;

/**
 * @author Oliver Keszöcze
 *         <p>
 *         This enum stores all the options available when displaying a biochip.
 *         All options are on/off only.
 */
public enum BDisplayOptions {
	/**
	 * Option for displaying the amount of cell usage.
	 */
	CellUsage,

	/**
	 * Option for highlighting used cells that are too close to other used
	 * cells. See also {@link BDisplayOptions#InterferenceRegion}.
	 */
	Adjacency,
	/**
	 * Option for coloring the neighbouring cells of droplets (also known as
	 * the
	 * interference region).
	 */
	InterferenceRegion,
	/**
	 * Option for displaying the IDs of the droplets.
	 */
	DropletIDs,
	/**
	 * Option for displaying the fluid IDs of the droplets.
	 */
	FluidIDs,
	/**
	 * Option for displaying the names of the fluids (e.g. blood) on top of the
	 * droplets.
	 */
	FluidNames,
	/**
	 * Option for displaying the pins assigned to the cells.
	 */
	Pins,
	/**
	 * Option for displaying droplets in general.
	 */
	Droplets,
	/**
	 *
	 */
	Actuations,
	/**
	 * Option for displaying icons at the source(s) and target of a net.
	 */
	SourceTargetIcons,
	/**
	 * Option for displaying the droplet IDs at the source(s) and target of a
	 * net.
	 */
	SourceTargetIDs,
	/**
	 * Option for displaying coordinates at the upper and left part of the
	 * canvas.
	 */
	Coordinates,
	/**
	 * Option for coloring the routes' arrows in the color of the droplet
	 * instead of black.
	 */
	ColorfulRoutes,
	/**
	 * Option for displaying the fluid ID dispensed by the given dispenser.
	 * Please note that this is not the ID of the dispensing unit!
	 */
	DispenserID,
	/**
	 * Option for displaying the icon of a dispenser.
	 */
	DispenserIcon,
	/**
	 * Option for displaying the icon of a sink.
	 */
	SinkIcon,
	/**
	 * Option for displaying the icon of a detector.
	 */
	DetectorIcon,

	/**
	 * Option for displaying a net's colour on all its corresponding droplets 
	 */
	NetColorOnDroplets,

	/**
	 * Option for displaying a net's colour on all its corresponding fields 
	 */
	NetColorOnFields,

	/**
	 * Option for displaying a droplet's source and target via single, long
	 * indicators
	 */
	LongNetIndicatorsOnDroplets,

	/**
	 * Option for dispalying a net's sources and target via single, long
	 * indicators on its fields
	 */
	LongNetIndicatorsOnFields,
	
	/**
	 * Whether or not interference regions should "linger" behind, i.e.
	 * encompass all fields that are adjacent at t *and* t-1.
	 */
	LingeringInterferenceRegions
}
