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

package de.bioviz.ui;

/**
 * <p>The BioVizEvent interface serves as a way to exchange information between
 * the visualization and any project built around it (such as a native UI).</p>
 * <p>The core idea is that the visualization can add various instances of
 * classes that implement this interface to certain sets of things that
 * require some kind of notification or interaction beyond the libgdx
 * visualization. If e.g. a file is supposed to be loaded, the visualization
 * checks for any BioVizEvent instances that have been added via the
 * addLoadFileListener method and <i>calls their bioVizEvent() function</i>. The
 * surrounding structure (e.g. the native desktop UI) can then use this call to
 * do stuff.</p>
 * <p>Usually this would be done by implementing something like a
 * loadFileListener class that inherits this event and fills the bioVizEvent()
 * method with the required content - such as opening the according dialogue and
 * calling the function inside the visualization that expects the according
 * data as its input.</p>
 *
 * @author Jannis Stoppe
 *
 */
public interface BioVizEvent {

	/**
	 * Implement this method in your structures, it is called when the according
	 * event occurs and your instance has been added to the according sets of the
	 * BioViz instance.
	 */
	void bioVizEvent();
}
