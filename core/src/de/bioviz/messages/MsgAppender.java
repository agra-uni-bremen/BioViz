/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU
 * General Public License along with BioViz. If not, see <http://www.gnu.org/licenses/>.
 */

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
	private MessageCenter mc = null;

	@Override
	/**
	 * Forwards a log message to the MessageCenter that will display it on the
	 * HUD.
	 */
	protected void append(final ILoggingEvent eventObject) {
		if (isStarted() &&
			eventObject.getLevel().isGreaterOrEqual(Level.INFO)) {
			mc.addMessage(eventObject.getFormattedMessage());
		}
	}

	@Override
	/**
	 * Checks whether this appender is ready to append messages.
	 *
	 * The idea is to check whether a GUI is present. If so, messages can be
	 * appended.
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

	/**
	 * Sets the reference to the {@link BioViz} that is supposed to show the +
	 * messages.
	 *
	 * @param bioViz
	 * 		The {@link BioViz} that is supposed to show the messages.
	 */
	public static void setMessageViz(final BioViz bioViz) {
		MsgAppender.viz = bioViz;
	}

}
