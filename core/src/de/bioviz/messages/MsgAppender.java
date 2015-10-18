package de.bioviz.messages;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.bioviz.ui.BioViz;

/**
 * @author Oliver Keszöcze
 * @brief A wrapper that makes the MessageCenter a valid appender for the logback framework
 */
public class MsgAppender extends AppenderBase<ILoggingEvent> {

    MessageCenter mc = null;
    BioViz viz;
    
    public MsgAppender(BioViz viz) {
		this.viz = viz;
	}

    @Override
    /**
     * @brief Forwards a log message to the MessageCenter that will display it on the HUD
     */
    protected void append(ILoggingEvent eventObject) {
        if (isStarted() && eventObject.getLevel().isGreaterOrEqual(Level.INFO)) {
            mc.addMessage(eventObject.getFormattedMessage());
        }
    }

    @Override
    /**
     * @brief Checks whether this appender is ready to append messages
     *
     * The idea is to check whether a GUI is present. If so, messages can be appended.
     */
    public boolean isStarted() {
        if (mc == null) {
            if (viz != null && viz.mc != null) {
                mc = viz.mc;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }

    }
}
