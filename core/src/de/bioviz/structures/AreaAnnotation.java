package de.bioviz.structures;

/**
 * @author Maximilian Luenert
 */
public class AreaAnnotation {
	public final Rectangle position;
	public final String annotation;

	public AreaAnnotation(final Rectangle rectangle, final String annotation){
		this.position = rectangle;
		this.annotation = annotation;
	}

	public String toString(){
		return annotation + position;
	}
}
