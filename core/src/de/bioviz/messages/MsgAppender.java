package de.bioviz.messages;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.bioviz.ui.BioViz;

/**
 * A wrapper that makes the MessageCenter a valid appender for the logback
 * framework.
 *
 * @author Oliver Keszocze
 */
public class MsgAppender extends AppenderBase<ILoggingEvent> {

	/**
	 * The "parent" visualization.
	 */
	private static BioViz viz;


	/**
	 * The "parent" MessageCenter.
	 */
	MessageCenter mc = null;

	@Override
	/**
	 * @brief Forwards a log message to the MessageCenter that will display it
	 * on the HUD
	 */
	protected void append(final ILoggingEvent eventObject) {
		if (isStarted() &&
			eventObject.getLevel().isGreaterOrEqual(Level.INFO)) {
			mc.addMessage(eventObject.getFormattedMessage());
		}
	}

	@Override
	/**
	 * @brief Checks whether this appender is ready to append messages
	 *
	 * The idea is to check whether a GUI is present. If so, messages can be
	 * appended.
	 */
	public boolean isStarted() {
		if (mc == null) {
			if (viz != null && viz.messageCenter != null) {
				mc = viz.messageCenter;
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}

	/**
	 * Sets the reference to the {@link BioViz} that is supposed to show the
	 *+ messages.
	 *
	 * @param bioViz
	 * 		The {@link BioViz} that is supposed to show the messages.
	 */
	public static void setMessageViz(final BioViz bioViz) {
		MsgAppender.viz = bioViz;
	}
}
