package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import de.bioviz.ui.DrawableAssay;

import java.util.List;

/**
 * Created by max on 7/25/16.
 */
public class AnnotationViewer extends TextViewer {

	/** The bioviz instance. */
	private BioViz currentViz;

	/**
	 * Constructs a new AnnotationViewer.
	 * @param bioViz the bioviz instance.
	 */
	public AnnotationViewer(final BioViz bioViz) {
		frame.setTitle("Annotations");
		currentViz = bioViz;
	}

	/**
	 * Loads the annotations.
	 */
	private void loadAnnotations() {
		DrawableAssay currentAssay = currentViz.currentAssay;
		if (currentAssay != null) {
			List<String> annotations = currentAssay.getData().getAnnotations();
			for (final String annotation : annotations) {
				addLine(annotation.substring(2));
			}
		}
	}

	/**
	 * Reloads the contents.
	 */
	public void reload() {
		textArea.setText("");
		loadAnnotations();
	}
}
