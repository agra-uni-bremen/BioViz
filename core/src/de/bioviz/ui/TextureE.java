package de.bioviz.ui;

/**
 * @author Oliver Keszocze
 *
 * This class stores what kind of textues can be used
 */
public enum TextureE {
	// all of these comments are there to appease checkstyle only

	/**
	 * We have a texture for droplets
	 */
	Droplet,
	/**
	 * We have a texture for sinks
	 */
	Sink,
	/**
	 * We have a texture for dispensers
	 */
	Dispenser,
	/**
	 * We have a texture for detectors
	 */
	Detector,
	/**
	 * We have a texture for droplet route starts
	 */
	Start,
	/**
	 * We have a texture for droplet route targets
	 */
	Target,
	/**
	 * We have a texture for marking the adjacency
	 */
	AdjacencyMarker,
	/**
	 * We have a texture for a single black pixel
	 */
	BlackPixel,
	/**
	 * We have a texture for blockages
	 */
	Blockage,
	/**
	 * We have a texture for square shaped cells
	 */
	GridMarker,
	/**
	 * We have a texture for indicating the path of the droplet
	 */
	StepMarker,
	/**
	 * We have a texture for heaters
	 */
	Heater,
	/**
	 * We have a texture for magnets
	 */
	Magnet
}
