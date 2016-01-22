package de.bioviz.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author Oliver Keszöcze
 */
public class DisplayOptions {
	private HashMap<BDisplayOptions,Boolean> options;
	static Logger logger = LoggerFactory.getLogger(DisplayOptions.class);

	DisplayOptions() {
		options = new HashMap<>();
		for (BDisplayOptions o :BDisplayOptions.values()) {
			setOption(o,false);
		}
		setOption(BDisplayOptions.SinkIcon,true);
		setOption(BDisplayOptions.SourceTargetIcons,true);
		setOption(BDisplayOptions.SourceTargetIDs,true);
		setOption(BDisplayOptions.Droplets,true);
		//setOption(BDisplayOptions.Coordinates,true);
		setOption(BDisplayOptions.DispenserIcon,true);
		setOption(BDisplayOptions.DispenserID,true);
		setOption(BDisplayOptions.DetectorIcon,true);
		setOption(BDisplayOptions.FluidNames,true);
		setOption(BDisplayOptions.InterferenceRegion,true);
	}

	public boolean getOption(BDisplayOptions opt) {
		return options.get(opt);
	}

	public void setOption(BDisplayOptions opt, boolean val) {

		logger.debug("Setting option \"{}\" to {}",opt,val);
		options.put(opt,val);
	}

	public boolean toggleOption(BDisplayOptions opt) {
		boolean val = !getOption(opt);
		setOption(opt,val);
		return val;
	}

}
