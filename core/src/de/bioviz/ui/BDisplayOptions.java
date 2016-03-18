package de.bioviz.ui;

import com.badlogic.gdx.Input;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author keszocze
 *         <p>
 *         This enum stores all the options available when displaying a biochip.
 *         All options are on/off only.
 */
public enum BDisplayOptions {
	/**
	 * Option for displaying the amount of cell usage.
	 */
	CellUsage("Show cell usage colored", Input.Keys.U),

	/**
	 * Option for highlighting used cells that are too close to other used
	 * cells. See also {@link BDisplayOptions#InterferenceRegion}.
	 */
	Adjacency("Show fluidic constraint violations"),
	/**
	 * Option for coloring the neighbouring cells of droplets (also known as
	 * the
	 * interference region).
	 */
	InterferenceRegion("Show the interference region"),
	/**
	 * Option for displaying the IDs of the droplets.
	 */
	DropletIDs("Show droplet IDs"),
	/**
	 * Option for displaying the fluid IDs of the droplets.
	 */
	FluidIDs("Show fluid IDs"),
	/**
	 * Option for displaying the names of the fluids (e.g. blood) on top of the
	 * droplets.
	 */
	FluidNames("Show fluid names"),
	/**
	 * Option for displaying the pins assigned to the cells.
	 */
	Pins("Show pin assignment"),
	/**
	 * Option for displaying droplets in general.
	 */
	Droplets("Show droplets"),
	/**
	 * Option for displaying actuations.
	 * <p>
	 * An cell is actuated when the underlying electrode is turned on. This
	 * makes a droplet move to that cell.
	 */
	Actuations("Show actuations"),
	/**
	 * Option for displaying icons at the source(s) and target of a net.
	 */
	SourceTargetIcons("Show net icons"),
	/**
	 * Option for displaying the droplet IDs at the source(s) and target of a
	 * net.
	 */
	SourceTargetIDs("Show net IDs"),
	/**
	 * Option for displaying coordinates at the upper and left part of the
	 * canvas.
	 */
	Coordinates("Show coordinates"),
	/**
	 * Option for coloring the routes' arrows in the color of the droplet
	 * instead of black.
	 */
	ColorfulRoutes("Color routes", Input.Keys.R),
	/**
	 * Option for displaying the fluid ID dispensed by the given dispenser.
	 * Please note that this is not the ID of the dispensing unit!
	 */
	DispenserID("Show dispenser IDs", Input.Keys.D, true, false, false),
	/**
	 * Option for displaying the icon of a dispenser.
	 */
	DispenserIcon("Show dispenser icons", Input.Keys.D, false, true, false),
	/**
	 * Option for displaying the icon of a sink.
	 */
	SinkIcon("Show sink icons"),
	/**
	 * Option for displaying the icon of a detector.
	 */
	DetectorIcon("Show detector icons"),

	/**
	 * Will draw solid paths for the droplets.
	 * <p>
	 * This basically disables the fancy fading.
	 */
	SolidPaths("Draw solid paths"),

	/**
	 * Option for displaying a net's colour on all its corresponding droplets
	 */
	NetColorOnDroplets("Color droplets within net"),

	/**
	 * Option for displaying a net's colour on all its corresponding fields
	 */
	NetColorOnFields("Show bounding boxes for nets"),

	/**
	 * Option for displaying a droplet's source and target via single, long
	 * indicators
	 */
	LongNetIndicatorsOnDroplets("Draw lines on droplets"),

	/**
	 * Option for dispalying a net's sources and target via single, long
	 * indicators on its fields
	 */
	LongNetIndicatorsOnFields("Draw source -> traget lines"),

	/**
	 * Whether or not interference regions should "linger" behind, i.e.
	 * encompass all fields that are adjacent at t *and* t-1.
	 */
	LingeringInterferenceRegions("Show dynamic interference region"),

	/**
	 * When using autoplay, restart it after it has finished.
	 */
	LoopAutoplay("Loop the autoplayed animation");

	private final String description;

	// ANY_KEY is the smallest value used by libgdx so this should make sure
	// that by default no hotkey matches.
	private int keycode = Input.Keys.ANY_KEY - 1;
	private boolean ctrl = false;
	private boolean shift = false;
	private boolean alt = false;

	BDisplayOptions(String desc) {
		description = desc;
	}

	BDisplayOptions(String desc, int keycode) {
		this(desc);
		this.keycode = keycode;
	}

	BDisplayOptions(String desc, int keycode, boolean ctrl, boolean shift,
					boolean alt) {
		this(desc, keycode);
		this.ctrl = ctrl;
		this.shift = shift;
		this.alt = alt;
	}

	public static Optional<BDisplayOptions> findOption(int keycode, boolean
			ctrl, boolean shift, boolean alt) {

		return Arrays.stream(BDisplayOptions.values()).filter(
				it -> it.keycode == keycode && it.ctrl == ctrl &&
					  it.shift == shift && it.alt == alt).findFirst();
	}


	public String description() {
		return description;
	}
}
