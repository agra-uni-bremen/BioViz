package de.bioviz.desktop;

import de.bioviz.ui.BioViz;

/**
 * This class implements an errorViewer for parser errors.
 *
 */
public class ErrorViewer extends TextViewer {

	/** The error type. */
	ERRORTYPE type;
	/** The bioViz instance. */
	private BioViz currentViz;

	/**
	 * Constructs a new ErrorViewer.
	 *
	 * @param bioViz the bioviz instance
	 * @param title the title for the frame
	 * @param type the type of errors to show
	 */
	public ErrorViewer(final BioViz bioViz, final String title, final
	ERRORTYPE type) {
		super();
		frame.setTitle(title);
		currentViz = bioViz;
		this.type = type;
	}

	/**
	 * Adds hard errors to the textArea.
	 */
	private void addErrors() {
		if (currentViz.currentAssay != null) {
			addLines(currentViz.currentAssay.getData().hardErrors);
		}
	}

	/**
	 * Adds soft errors to the textArea.
	 */
	private void addWarnings() {
		if (currentViz.currentAssay != null) {
			addLines(currentViz.currentAssay.getData().errors);
		}
	}

	/**
	 * Reloads the content for the current bioviz.
	 */
	public void reload() {
		textArea.setText("");
		if (type.equals(ERRORTYPE.HARD)) {
			addErrors();
		} else if (type.equals(ERRORTYPE.SOFT)) {
			addWarnings();
		} else if (type.equals(ERRORTYPE.BOTH)) {
			addErrors();
			addWarnings();
		}
	}

	/**
	 * Enum for the errorTypes.
	 */
	public enum ERRORTYPE {
		HARD, /** Error where the parsing failed completely. */
		SOFT, /** Warnings. */
		BOTH /** Both types together. */
	}
}
