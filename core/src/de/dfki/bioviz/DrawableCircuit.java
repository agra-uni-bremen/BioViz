package de.dfki.bioviz;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import structures.Biochip;
import structures.BiochipField;
import structures.Blob;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * The DrawableCircuit class provides methods to draw a given ReversibleCircuit.
 * Create a ReversibleCircuit first (e.g. by loading a given .real file), then
 * create a DrawableCircuit instance for the ReversibleCircuit to draw the latter.
 * @author jannis
 *
 */
public class DrawableCircuit implements Drawable {
	Biochip data;
	
	DrawableSprite field;
	DrawableSprite blob;

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

	/**
	 * Creates a drawable entity based on the data given.
	 * @param toDraw the data to draw
	 */
	public DrawableCircuit(Biochip toDraw) {
		this.data = toDraw;
		field = new DrawableSprite("GridMarker.png", 1, 1);
		field.color = new Color(0.8f, 0.9f, 1f, 1f);
		blob = new DrawableSprite("Blob.png", 1, 1);
		blob.color = new Color(0.5f, 0.65f, 1f, 0.75f);
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
		
		for (int i = 0; i < this.data.field.length; i++) {
			for (int j = 0; j < this.data.field[i].length; j++) {
				if (this.data.field[i][j].isEnabled) {
					float xCoord = xCoordOnScreen(i);
					float yCoord = yCoordOnScreen(j);
					
					field.x = xCoord;
					field.y = yCoord;
					field.scaleX = this.smoothScaleX;
					field.scaleY = this.smoothScaleY;
					field.draw();
				}
			}
		}
		
		for (Blob b : this.data.getBlobs()) {
			float xCoord = xCoordOnScreen(b.getXAt(currentTime));
			float yCoord = yCoordOnScreen(b.getYAt(currentTime));
			
			blob.x = xCoord;
			blob.y = yCoord;
			blob.scaleX = this.smoothScaleX;
			blob.scaleY = this.smoothScaleY;
			blob.draw();
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
	
	private int gateAt(int x) {
		float xResult = x - RevVisGDX.singleton.camera.viewportWidth / 2f;
		
		xResult /= getScaleX();
		xResult -= offsetX;
		xResult += 0.5f;
		
		return (int)xResult;
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
		this.offsetY = 0;
		this.offsetX = this.data.field.length / -2f;
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
}
