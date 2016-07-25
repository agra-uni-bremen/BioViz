package de.bioviz.desktop;

import de.bioviz.ui.BioViz;

/**
 * This class implements an errorViewer for parser errors.
 *
 */
public class ErrorViewer extends TextViewer{

	/** The error type. */
	ERROR_TYPE type;
	/** The bioViz instance. */
	private BioViz currentViz;

	/**
	 * Constructs a new ErrorViewer.
	 *
	 * @param bioViz the bioviz instance
	 * @param title the title for the frame
	 * @param type the type of errors to show
	 * @param iconPath the path to the icon
	 */
	public ErrorViewer(final BioViz bioViz, final String title, final
	ERROR_TYPE type) {
		super();
		frame.setTitle(title);
		currentViz = bioViz;
		this.type = type;
	}

	/**
	 * Adds hard errors to the textArea.
	 */
	private void addErrors(){
		if (currentViz.currentCircuit != null) {
			addLines(currentViz.currentCircuit.getData().hardErrors);
		}
	}

	/**
	 * Adds soft errors to the textArea.
	 */
	private void addWarnings(){
		if (currentViz.currentCircuit != null) {
			addLines(currentViz.currentCircuit.getData().errors);
		}
	}

	/**
	 * Reloads the content for the current bioviz.
	 */
	public void reload(){
		textArea.setText("");
		if (type.equals(ERROR_TYPE.HARD)) {
			addErrors();
		} else if (type.equals(ERROR_TYPE.SOFT)) {
			addWarnings();
		} else if (type.equals(ERROR_TYPE.BOTH)) {
			addErrors();
			addWarnings();
		}
	}

	/**
	 * Enum for the errorTypes.
	 */
	public enum ERROR_TYPE {
		HARD,
		SOFT,
		BOTH
	}
}
