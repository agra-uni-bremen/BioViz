package de.bioviz.ui;

import com.badlogic.gdx.Input;

import java.util.Arrays;
import java.util.Optional;

/**
 * This enum stores all the options available when displaying a biochip.
 * <p>
 * All options are on/off only.
 *
 * @author Oliver Keszocze
 */
public enum BDisplayOptions {
	/**
	 * Option for displaying the amount of cell usage.
	 */
	CellUsage("Show cell usage colored", Input.Keys.U),

	/**
	 * Option for displaying the amount of actuations of a given cell
	 */
	CellUsageCount("Cell usage count"),

	/**
	 * Option for highlighting used cells that are too close to other used
	 * cells. See also {@link BDisplayOptions#InterferenceRegion}.
	 */
	Adjacency("Show fluidic constraint violations", Input.Keys.A, true, false,
			  false),
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
	Coordinates("Show coordinates", Input.Keys.C, true, false, false),
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
<<<<<<< HEAD
	 * Option for displaying a net's colour on all its corresponding droplets
=======
	 * Will draw solid paths for the droplets.
	 * <p>
	 * This basically disables the fancy fading.
>>>>>>> master
	 */
	SolidPaths("Draw solid paths"),

	/**
<<<<<<< HEAD
	 * Option for displaying a net's colour on all its corresponding fields
=======
	 * Option for displaying a net's colour on all its corresponding droplets.
>>>>>>> master
	 */
	NetColorOnDroplets("Color droplets within net"),

	/**
	 * Option for displaying a net's colour on all its corresponding fields.
	 */
	NetColorOnFields("Show bounding boxes for nets"),

	/**
	 * Option for displaying a droplet's source and target via single, long
	 * indicators.
	 */
	LongNetIndicatorsOnDroplets("Draw lines on droplets"),

	/**
	 * Option for dispalying a net's sources and target via single, long
	 * indicators on its fields.
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

	/**
	 * The description that is used in the menus to display this option.
	 */
	private final String description;

	//
	// th

	/**
	 * Stores the hotkey used to toggle the option.
	 * <p>
	 * ANY_KEY is the smallest value used by libgdx so this should make sure at
	 * by default no hotkey matches. Let's hope that they never change this.
	 * <p>
	 * I know that this is kinda hacky @keszocze
	 */
	private int keycode = Input.Keys.ANY_KEY - 1;

	/**
	 * Whether the Ctrl-Key is needed to to toggle this option.
	 */
	private boolean ctrl = false;

	/**
	 * Whether the Shift-Key is needed to toggle this option.
	 */
	private boolean shift = false;


	/**
	 * Whether the Alt-Key is needed to toggle this option.
	 */
	private boolean alt = false;

	/**
	 * Creates an Option without hotkeys.
	 *
	 * @param desc
	 * 		The text used in the menu to describe this option.
	 */
	BDisplayOptions(final String desc) {
		description = desc;
	}

	/**
	 * Creates an option with an unmodified hotkey.
	 * <p>
	 * 'Unmodified' in this case means that neither shift, key nor alt is
	 * pressed.
	 *
	 * @param desc
	 * 		The text used in the menu to describe this option.
	 * @param keycode
	 * 		The hotkey that toggles this option.
	 */
	BDisplayOptions(final String desc, final int keycode) {
		this(desc);
		this.keycode = keycode;
	}

	/**
	 * Creates an option with a hotkey.
	 * <p>
	 *
	 * @param desc
	 * 		The text used in the menu to describe this option.
	 * @param keycode
	 * 		The hotkey that toggles this option.
	 * @param ctrl
	 * 		if 'true', the Ctrl-key needs to be pressed to toggle this option
	 * @param shift
	 * 		if 'true', the Shift-key needs to be pressed to toggle this option
	 * @param alt
	 * 		if 'true', the Alt-key needs to be pressed to toggle this option
	 */
	BDisplayOptions(final String desc, final int keycode, final boolean ctrl,
					final boolean shift, final boolean alt) {
		this(desc, keycode);
		this.ctrl = ctrl;
		this.shift = shift;
		this.alt = alt;
	}

	/**
	 * Searches an option that is bound to the given hotkey.
	 * <p>
	 * Note that if more than one option is bound to the specified hotkey only
	 * the first ocurrence ist returned. There is garuantee regarding the order
	 * of the options. So make sure to know what you are doing.
	 *
	 * @param keycode
	 * 		The key that was typed
	 * @param ctrl
	 * 		Whether the Ctrl-key was hold down
	 * @param shift
	 * 		Whether the Shift-key was hold down
	 * @param alt
	 * 		Whether the Alt-key was hold down
	 * @return An optional that might contain an option that matches the given
	 * hotkey.
	 */
	public static Optional<BDisplayOptions> findOption(final int keycode,
													   final boolean ctrl,
													   final boolean shift,
													   final boolean alt) {

		return Arrays.stream(BDisplayOptions.values()).filter(
				it -> it.keycode == keycode && it.ctrl == ctrl &&
					  it.shift == shift && it.alt == alt).findFirst();
	}


	/**
	 * The textual description of the Option as used in the menus.
	 *
	 * @return The textual description of the option
	 */
	public String description() {
		return description;
	}
}
