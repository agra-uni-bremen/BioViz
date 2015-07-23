package de.dfki.bioviz.messages;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.dfki.bioviz.BioViz;

/**
 * Created by keszocze on 23.07.15.
 */
public class MsgAppender extends AppenderBase<ILoggingEvent> {

    MessageCenter mc = null;


    @Override
    protected void append(ILoggingEvent eventObject) {
        if (isStarted()) {
            mc.addMessage(eventObject.getFormattedMessage());
        }
    }

    @Override
    public boolean isStarted() {
        if (mc == null) {
            if (BioViz.singleton != null && BioViz.singleton.mc != null) {
                mc = BioViz.singleton.mc;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }

    }
}
