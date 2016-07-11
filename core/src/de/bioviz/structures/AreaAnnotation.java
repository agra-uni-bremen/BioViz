package de.bioviz.structures;

/**
 * This class stores the informations for an areaAnnotation.
 *
 * @author Maximilian Luenert
 */
public class AreaAnnotation {

	/** The annotated fields. */
	private Rectangle position;
	/** The annotation text. */
	private String annotation;

	/**
	 * Constructs a new AreaAnnotation with the given rectangle an annotation.
	 *
	 * @param rectangle the position of the new AreaAnnotation
	 * @param annotation the text of the new AreaAnnotation
	 */
	public AreaAnnotation(final Rectangle rectangle, final String annotation) {
		this.position = rectangle;
		this.annotation = annotation;
	}

	/**
	 * Returns a String representation of the AreaAnnotation.
	 *
	 * @return string representation
	 */
	@Override
	public String toString() {
		return annotation + position;
	}

	/**
	 * Gets the annotation position.
	 *
	 * @return Rectangle
	 */
	public Rectangle getPosition() {
		return position;
	}

	/**
	 * Sets the annotation position.
	 *
	 * @param position the position
	 */
	public void setPosition(final Rectangle position) {
		this.position = position;
	}

	/**
	 * Gets the annotation text.
	 *
	 * @return String
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * Sets the annotation text.
	 *
	 * @param annotation the annotation text
	 */
	public void setAnnotation(final String annotation) {
		this.annotation = annotation;
	}

}
