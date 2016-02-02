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
 * @author jannis
 *
 */
public interface BioVizEvent {

	/**
	 * Implement this method in your structures, it is called when the according
	 * even occurs and your instance has been added to the according sets of the
	 * BioViz instance.
	 */
	void bioVizEvent();
}
