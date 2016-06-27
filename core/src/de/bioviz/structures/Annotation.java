package de.bioviz.structures;

/**
 * Created by max on 6/20/16.
 */
public class Annotation {
	public final Rectangle position;
	public final String annotation;

	public Annotation(final Rectangle rectangle, final String annotation){
		this.position = rectangle;
		this.annotation = annotation;
	}

	public String toString(){
		return annotation + position;
	}
}
