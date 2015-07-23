package de.dfki.bioviz;

import java.util.Date;
import java.util.Set;
import java.util.Vector;

import de.dfki.bioviz.structures.Biochip;
import de.dfki.bioviz.structures.BiochipField;
import de.dfki.bioviz.structures.Droplet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The DrawableCircuit class provides methods to draw a given ReversibleCircuit.
 * Create a ReversibleCircuit first (e.g. by loading a given .real file), then
 * create a DrawableCircuit instance for the ReversibleCircuit to draw the latter.
 * @author jannis
 *
 */
public class DrawableCircuit implements Drawable {
	Biochip data;

	private float scaleX = 1;
	public float offsetX = 0;
	private float scaleY = 1;
	public float offsetY = 0;
	
	protected float smoothScaleX = 1;
	protected float smoothScaleY = 1;
	protected float smoothOffsetX = 0;
	protected float smoothOffsetY = 0;
	
	private float scalingDelay = 4f;
	
	public long currentTime = 0;
	
	public boolean autoAdvance = false;
	public float autoSpeed = 2f;
	private long lastAutoStepAt = new Date().getTime();
	
	private Vector<BioVizEvent> timeChangedListeners = new Vector<BioVizEvent>();
	
	private boolean highlightAdjacency = true;
	
	private Vector<DrawableField> fields = new Vector<>();
	private Vector<DrawableDroplet> droplets = new Vector<>();


	static Logger logger = LoggerFactory.getLogger(DrawableCircuit.class);



	public boolean getHighlightAdjacency() {
		return highlightAdjacency;
	}

	public void setHighlightAdjacency(boolean highlightAdjacency) {
		this.highlightAdjacency = highlightAdjacency;
		if (this.highlightAdjacency) {
			logger.info("Highlighting fields with adjacent droplets");
		} else {
		}
			logger.info("Stop highilghting fields with adjacent droplets");
	}
	
	public void toggleHighlightAdjacency() {
		this.setHighlightAdjacency(!this.highlightAdjacency);
	}

	/**
	 * Creates a drawable entity based on the data given.
	 * @param toDraw the data to draw
	 */
	public DrawableCircuit(Biochip toDraw) {
		this.data = toDraw;
		this.initializeDrawables();
	}
	
	/**
	 * Initializes the drawables according to the circuit stored in the data field
	 */
	private void initializeDrawables() {
		// clear remaining old data first, if any
		this.fields.clear();
		this.droplets.clear();

		logger.debug("Initializing drawables: {} fields, {} droplets", (data.field.length * data.field[0].length), data.getDroplets().size() );

		//setup fields
		for (int i = 0; i < data.field.length; i++) {
			for (int j = 0; j < data.field[i].length; j++) {
				DrawableField f = new DrawableField(data.field[i][j]);
				this.fields.add(f);
			}
		}
		
		//setup droplets
		for (Droplet d : data.getDroplets()) {
			DrawableDroplet dd = new DrawableDroplet(d);
			this.droplets.add(dd);
		}
	}

	@Override
	public void draw() {		
		smoothScaleX += (getScaleX() - smoothScaleX) / scalingDelay;
		smoothScaleY += (getScaleY() - smoothScaleY) / scalingDelay;
		smoothOffsetX += (offsetX - smoothOffsetX) / scalingDelay;
		smoothOffsetY += (offsetY - smoothOffsetY) / scalingDelay;

		drawGates();
		
		drawOverlay();
	}

	/**
	 * Draws additional overlaid information (currently only the moving rule overlay)
	 */
	private void drawOverlay() {

	}

	/**
	 * Draws the gates
	 */
	private void drawGates() {
		
		if (autoAdvance) {
			long current = new Date().getTime();
			if (lastAutoStepAt + (long)((1f / this.autoSpeed) * 1000) < current) {
				lastAutoStepAt = current;
				currentTime++;
				for (BioVizEvent listener : this.timeChangedListeners) {
					listener.bioVizEvent();
				}
			}
		}
		
		for (DrawableField f : this.fields) {
			f.draw();
		}
		
		for (DrawableDroplet d : this.droplets) {
			d.draw();
		}
	}

	/**
	 * Calculates the x coordinate of a given gate
	 * @param i the gate index
	 * @return the x coordinate on screen
	 */
	protected float xCoordOnScreen(int i) {
		return xCoordOnScreen((float)i);
	}
	
	/**
	 * Calculates the x coordinate of a given value. Keep in mind that
	 * this is still in gate-space, so a value of 0 would be at the center
	 * of the circuit's first gate.
	 * @param i the value to translate
	 * @return the x coordinate on screen
	 */
	protected float xCoordOnScreen(float i) {
		float xCoord = i;
		xCoord += smoothOffsetX;
		xCoord *= smoothScaleX;
		return xCoord;
	}
	
	protected float yCoordOnScreen(int i) {
		return yCoordOnScreen((float)i);
	}
	
	protected float yCoordOnScreen(float i) {
		float yCoord = i;
		yCoord += smoothOffsetY;
		yCoord *= smoothScaleY;
		return yCoord;
	}
	
	/**
	 * If the two scaling factors aren't equal, this sets the larger scaling factor to
	 * the smaller one in order to display square elements on screen
	 */
	public void shrinkToSquareAlignment() {
		if (getScaleY() < getScaleX())
			setScaleX(getScaleY());
		else
			setScaleY(getScaleX());
	}

	//http://stackoverflow.com/questions/7896280/converting-from-hsv-hsb-in-java-to-rgb-without-using-java-awt-color-disallowe
	private static Color hsvToRgb(float hue, float saturation, float value) {

		while (hue >= 1)
			hue -= 1;
		while (hue < 0)
			hue += 1;
		int h = (int)(hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0: return new Color(value, t, p, 1);
		case 1: return new Color(q, value, p, 1);
		case 2: return new Color(p, value, t, 1);
		case 3: return new Color(p, q, value, 1);
		case 4: return new Color(t, p, value, 1);
		case 5: return new Color(value, p, q, 1);
		default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
		}
	}
	

	/**
	 * retrieves the current x scaling factor
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * sets the current x scaling factor
	 * Keep in mind that the value used for actually drawing the
	 * circuit is successively approaching the given value for a
	 * smooth camera movement. Use setScaleImmediately if the viewport
	 * is supposed to skip those inbetween steps.
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}
	
	/**
	 * retrieves the current y scaling factor
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * Sets the current y scaling factor.
	 * Keep in mind that the value used for actually drawing the
	 * circuit is successively approaching the given value for a
	 * smooth camera movement. Use setScaleImmediately if the viewport
	 * is supposed to skip those inbetween steps.
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
	
	/**
	 * Calculates the screen bounds in gate-space
	 * @return the screen bounds
	 */
	public Rectangle getViewBounds() {
		Rectangle result = new Rectangle();
		
		float centerX = -offsetX;
		float width = Gdx.graphics.getWidth() * (1f / scaleX);
		float centerY = offsetY;
		float height = Gdx.graphics.getHeight() * (1f / scaleY);
		result.set(centerX - (width / 2f), centerY + (data.field.length / 2) - (height / 2f), width, height);
		return result;
	}
	
	/**
	 * Sets the screen bounds in gate-space
	 * @param bounds the area the viewport is supposed to show.
	 */
	public void setViewBounds(Rectangle bounds) {
		float targetHeight = Gdx.graphics.getHeight() / bounds.height;
		float targetWidth = Gdx.graphics.getWidth() / bounds.width;
		float targetOffsetX = (bounds.x + (bounds.width / 2f));
		float targetOffsetY = bounds.y - (data.field.length / 2) + (bounds.height / 2f);
				
		setScaleX(targetWidth);
		setScaleY(targetHeight);
		this.offsetX = -targetOffsetX;
		this.offsetY = targetOffsetY;
	}
	
	private int busDrawn = 0;
	
	
	/**
	 * Resets the zoom to 1 px per element
	 */
	public void zoomTo1Px() {
		this.scaleX = 1;
		this.scaleY = 1;
	}
	

	/**
	 * Resets the zoom so that the whole circuit is shown.
	 */
	public void zoomExtents() {
		float x = 1f / this.data.field.length;
		float y = 1f / this.data.field[0].length;
		float xFactor = Gdx.graphics.getWidth();
		float yFactor = Gdx.graphics.getHeight();
		float maxScale = Math.min(x * xFactor, y * yFactor);
		this.scaleX = maxScale;
		this.scaleY = maxScale;
		this.offsetY = this.data.field[0].length / -2f + 0.5f;
		this.offsetX = this.data.field.length / -2f + 0.5f;
	}
	
	/**
	 * Resets the zoom so that the whole circuit is shown without
	 * smoothly zooming to those settings.
	 */
	public void zoomExtentsImmediately() {
		zoomExtents();
		this.smoothScaleX = scaleX;
		this.smoothScaleY = scaleY;
	}
	
	/**
	 * Sets the zoom to the given values without smoothly approaching
	 * those target values (instead sets them immediately).
	 */
	public void setScaleImmediately(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.smoothScaleX = scaleX;
		this.scaleY = scaleY;
		this.smoothScaleY = scaleY;
	}
	
	public void addTimeChangedListener(BioVizEvent listener) {
		timeChangedListeners.add(listener);
	}
	
	/**
	 * Re-calculates the adjacency for all blobs and sets
	 * the fields' colours accordingly.
	 */
	public void updateAdjacencyColours() {
		Set<BiochipField> f = this.data.getAdjacentActivations();
		for (BiochipField biochipField : f) {
			
		}
	}

	@Override
	public String generateSVG() {
		String result = "";
		result += "<svg width=\"100%\" height=\"100%\" viewBox=\"0 0 " + this.data.field.length + " " + this.data.field[0].length + "\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">";
		
		for (Drawable d : this.fields) {
			result += d.generateSVG();
		}
		for (Drawable d : this.droplets) {
			result += d.generateSVG();
		}		
		
		result += "</svg>";
		return result;
	}
}
