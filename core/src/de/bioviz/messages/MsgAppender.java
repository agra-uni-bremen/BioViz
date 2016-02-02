package de.bioviz.messages;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.bioviz.ui.BioViz;

/**
 * A wrapper that makes the MessageCenter a valid appender for the logback
 * framework.
 * @author keszocze
 */
public class MsgAppender extends AppenderBase<ILoggingEvent> {
	/**
	 * The "parent" MessageCenter.
	 */
	MessageCenter mc = null;

	/**
	 * The "parent" visualization.
	 */
	private static BioViz viz;

	/**
	 * Creates a new MsgAppender for a given visualization.
	 * @param viz the parent visualization
	 */
	public MsgAppender(final BioViz viz) {
		this.viz = viz;
	}

	@Override
	/**
	 * @brief Forwards a log message to the MessageCenter that will display it
	 * on the HUD.
	 */
	protected void append(final ILoggingEvent eventObject) {
		if (isStarted()
				&& eventObject.getLevel().isGreaterOrEqual(Level.INFO)) {
			mc.addMessage(eventObject.getFormattedMessage());
		}
	}

	@Override
	/**
	 * Checks whether this appender is ready to append messages
	 *
	 * The idea is to check whether a GUI is present.
	 * If so, messages can be appended.
	 */
	public boolean isStarted() {
		if (mc == null) {
			if (viz != null && viz.messageCenter != null) {
				mc = viz.messageCenter;
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

    public static void setMessageViz(BioViz vis) {
    	viz = vis;
    }
}
