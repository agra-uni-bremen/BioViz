package de.bioviz.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author Oliver Keszöcze
 *         <p>
 *         Class storing the current display options/settings.
 *         <p>
 *         It loads all options as defined in {@link BDisplayOptions} and
 *         initially sets them to false. It provides method for
 *         getting/setting/toggling these options.
 */
public class DisplayOptions {

	/**
	 * The class-wide logging thingie.
	 */
	static Logger logger = LoggerFactory.getLogger(DisplayOptions.class);

	/**
	 * Stores all options as defined in {@link BDisplayOptions}.
	 */
	private HashMap<BDisplayOptions, Boolean> options;


	/**
	 * The construct loads all options as defined in the
	 * {@link BDisplayOptions}
	 * enum and sets some of them to true.
	 */
	DisplayOptions() {
		options = new HashMap<>();
		for (final BDisplayOptions o : BDisplayOptions.values()) {
			setOption(o, false);
		}
		setOption(BDisplayOptions.SinkIcon, true);
		setOption(BDisplayOptions.SourceTargetIcons, true);
		setOption(BDisplayOptions.SourceTargetIDs, true);
		setOption(BDisplayOptions.Droplets, true);
		//setOption(BDisplayOptions.Coordinates,true);
		setOption(BDisplayOptions.DispenserIcon, true);
		setOption(BDisplayOptions.DispenserID, true);
		setOption(BDisplayOptions.DetectorIcon, true);
		setOption(BDisplayOptions.FluidNames, true);
		setOption(BDisplayOptions.InterferenceRegion, true);
	}


	/**
	 * @param opt
	 * 		Option to check
	 * @return true if the option opt is turned on, false otherwise
	 */
	public boolean getOption(final BDisplayOptions opt) {
		return options.get(opt);
	}

	/**
	 * Sets the value of an option.
	 * @param opt Option that is to be set
	 * @param val The new value of that option
	 */
	public void setOption(final BDisplayOptions opt, final boolean val) {

		logger.debug("Setting option \"{}\" to {}", opt, val);
		options.put(opt, val);
	}

	/**
	 * Toggles the value of an option and returns the new value.
	 * @param opt The option to toggle.
	 * @return The new value of the option, that is !old_value.
	 */
	public boolean toggleOption(final BDisplayOptions opt) {
		boolean val = !getOption(opt);
		setOption(opt, val);
		return val;
	}

}
