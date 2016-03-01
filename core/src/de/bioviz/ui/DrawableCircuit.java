package de.bioviz.ui;

import java.util.Date;
import java.util.Set;
import java.util.Vector;

import de.bioviz.messages.MessageCenter;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The DrawableCircuit class provides methods to draw a given ReversibleCircuit.
 * Create a ReversibleCircuit first (e.g. by loading a given .real file), then
 * create a DrawableCircuit instance for the ReversibleCircuit to draw the latter.
 *
 * @author jannis
 */
public class DrawableCircuit implements Drawable {
	public Biochip data;

	private float scaleX = 1;
	public float offsetX = 0;
	private float scaleY = 1;
	public float offsetY = 0;

	protected float smoothScaleX = 1;
	protected float smoothScaleY = 1;
	protected float smoothOffsetX = 0;
	protected float smoothOffsetY = 0;

	private float scalingDelay = 4f;

	public int currentTime = 1;

	public boolean autoAdvance = false;
	public float autoSpeed = 2f;
	private long lastAutoStepAt = new Date().getTime();

	private Vector<BioVizEvent> timeChangedListeners = new Vector<BioVizEvent>();


	public Vector<DrawableField> fields = new Vector<>();
	public Vector<DrawableDroplet> droplets = new Vector<>();
	public Vector<DrawableDroplet> hiddenDroplets = new Vector<>();

	public DisplayOptions displayOptions = new DisplayOptions();

	/**
	 * The field that is currently hovered by the mouse.
	 * May be null, so be careful.
	 */
	private DrawableField hoveredField = null;

	static Logger logger = LoggerFactory.getLogger(DrawableCircuit.class);
	
	public BioViz parent;

	public void prevStep() {
		autoAdvance=false;
		setCurrentTime(currentTime - 1);

	}

	public void nextStep() {
		autoAdvance=false;
		setCurrentTime(currentTime+1);
	}
	
	public void toggleAutoAdvance() {
		this.autoAdvance = !(this.autoAdvance);
	}

	public void setCurrentTime(int timeStep) {
		if (parent != null) {
			if (timeStep >= 1 && timeStep <= data.getMaxT()) {
				currentTime = timeStep;
				parent.callTimeChangedListeners();
			}
		} else {
			throw new RuntimeException("circuit parent is null");
		}
	}


	/**
	 * Creates a drawable entity based on the data given.
	 *
	 * @param toDraw the data to draw
	 */
	public DrawableCircuit(Biochip toDraw, BioViz parent) {
		logger.debug("Creating new drawable chip based on " + toDraw);
		this.data = toDraw;
		this.parent = parent;
		this.initializeDrawables();
		this.displayOptions.addOptionChangedEvent(e -> {
			if (e.equals(BDisplayOptions.CellUsage)) {
				boolean doIt = displayOptions.getOption(e);
				if (doIt) {
					data.computeCellUsage();
				}
			}
		});
		logger.debug("New DrawableCircuit created successfully.");
	}
	/**
	 * Initializes the drawables according to the circuit stored in the data field
	 */
	private void initializeDrawables() {
		// clear remaining old data first, if any
		this.fields.clear();
		this.droplets.clear();

		logger.debug("Initializing drawables: {} fields, {} droplets", data.getAllCoordinates().size(), data.getDroplets().size());

		//setup fields
		data.getAllFields().forEach(fld -> {
			DrawableField f = new DrawableField(fld, this);
			this.fields.add(f);
		});

		logger.debug("Fields set up.");

		//setup droplets
		for (Droplet d : data.getDroplets()) {
			DrawableDroplet dd = new DrawableDroplet(d, this);
			this.droplets.add(dd);
		}

		logger.debug("Droplets set up.");

		logger.debug("Drawable initialization successfully done.");
	}

	@Override
	public void draw() {
		smoothScaleX += (getScaleX() - smoothScaleX) / scalingDelay;
		smoothScaleY += (getScaleY() - smoothScaleY) / scalingDelay;
		smoothOffsetX += (offsetX - smoothOffsetX) / scalingDelay;
		smoothOffsetY += (offsetY - smoothOffsetY) / scalingDelay;

		if (displayOptions.getOption(BDisplayOptions.Coordinates)) {
			displayCoordinates();
		}
		else {
			removeDisplayedCoordinates();
		}
		if (displayOptions.getOption(BDisplayOptions.CellUsage)) {

		}

		if (autoAdvance) {
			long current = new Date().getTime();
			if (lastAutoStepAt + (long) ((1f / this.autoSpeed) * 1000) < current) {
				lastAutoStepAt = current;

				logger.trace("data.getMaxT: {}\tcurrentTime: {}",data.getMaxT(), currentTime);
				setCurrentTime(currentTime +1);
			}
		}

		for (DrawableField f : this.fields) {
			if (f.isHovered()) {
				this.hoveredField = f;
			}
			f.draw();
		}

		for (DrawableDroplet d : this.droplets) {
			d.draw();
		}
	
	}


	/**
	 * Draws the coordinates of the grid on top of and to the left of the grid.
	 * This in fact uses the message center to display the numbers, so the
	 * actual drawing will be done after the rest has been drawn.
	 * 
	 * @author jannis
	 */
	private void displayCoordinates() {
		// calculate current dimensions first so the coordinates can be either
		// displayed at the edge of the viewport (if the grid boundaries are
		// beyond the viewport boundaries) or at the edge of the grid (if they
		// are within)
		int minX = Integer.MAX_VALUE,
			minY = Integer.MAX_VALUE,
			maxX = Integer.MIN_VALUE,
			maxY = Integer.MIN_VALUE;

		for (DrawableField f : this.fields) {
			if (minX > f.getField().x()) {
				minX = f.getField().x();
			}
			if (minY > f.getField().y()) {
				minY = f.getField().y();
			}
			if (maxX < f.getField().x()) {
				maxX = f.getField().x();
			}
			if (maxY < f.getField().y()) {
				maxY = f.getField().y();
			}
		}
		
		float topYCoord = Gdx.graphics.getHeight() / 2f - 32;
		if (topYCoord > this.yCoordOnScreen(maxY + 1)) {
			topYCoord = this.yCoordOnScreen(maxY + 1);
		}
		float leftXCoord = -Gdx.graphics.getWidth() / 2f + 32;
		if (leftXCoord < this.xCoordOnScreen(minX - 1)) {
			leftXCoord = this.xCoordOnScreen(minX - 1);
		}
		
		// Defines when numbers should start fading and be completely hidden
		float startFadingAtScale = 32f;
		float endFadingAtScale = 24f;
		
		Color col = Color.WHITE.cpy();
		if (this.smoothScaleX < startFadingAtScale) {
			if (this.smoothScaleX > endFadingAtScale) {
				float alpha = 1f - ((startFadingAtScale - smoothScaleX) / (startFadingAtScale - endFadingAtScale));
				col.a = alpha;
			} else {
				// TODO: don't draw!
				col.a = 0;
			}
		}
		
		// scale text
		float scale = Math.min(MessageCenter.textRenderResolution, smoothScaleX / 2f);
		
		// indeed draw, top first, then left
		for (int i = minX; i < maxX + 1; i++) {
			this.parent.messageCenter.addHUDMessage(
					this.hashCode() + i,	// unique ID for each message
					Integer.toString(i).trim(),	// message
					this.xCoordOnScreen(i),	// x
					topYCoord, 				// y
					col,					// message color, used for fading
					scale);
		}
		
		for (int i = minY; i < maxY + 1; i++) {
			this.parent.messageCenter.addHUDMessage(
					this.hashCode() + maxX + Math.abs(minY) + 1 + i,
				// unique ID for each message, starting after the previous ids

					Integer.toString(i).trim(),	// message
					leftXCoord,				// x
					this.yCoordOnScreen(i), // y
					col,					// message color, used for fading
					scale);
		}
	}
	
	private void removeDisplayedCoordinates() {
		int minX = Integer.MAX_VALUE,
			minY = Integer.MAX_VALUE,
			maxX = Integer.MIN_VALUE,
			maxY = Integer.MIN_VALUE;

		for (DrawableField f : this.fields) {
			if (minX > f.getField().x()) {
				minX = f.getField().x();
			}
			if (minY > f.getField().y()) {
				minY = f.getField().y();
			}
			if (maxX < f.getField().x()) {
				maxX = f.getField().x();
			}
			if (maxY < f.getField().y()) {
				maxY = f.getField().y();
			}
		}
		
		// remove all HUD messages
		for (int i = minX; i < maxX + Math.abs(minY) + 2 + maxY; i++) {
			this.parent.messageCenter.removeHUDMessage(this.hashCode() + i);
		}
	}

	/**
	 * Calculates the x coordinate of a given cell
	 *
	 * @param i the cell index
	 * @return the x coordinate on screen
	 */
	protected float xCoordOnScreen(int i) {
		return xCoordOnScreen((float) i);
	}

	/**
	 * Calculates the x coordinate of a given value. Keep in mind that
	 * this is still in cell-space, so a value of 0 would be at the center
	 * of the chip's first cell.
	 *
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
		return yCoordOnScreen((float) i);
	}

	protected float yCoordOnScreen(float i) {
		float yCoord = i;
		yCoord += smoothOffsetY;
		yCoord *= smoothScaleY;
		return yCoord;
	}

	protected float yCoordInCells(float i) {
		float yCoord = i;
		yCoord /= smoothScaleY;
		yCoord -= smoothOffsetY;
		return yCoord;
	}

	protected float xCoordInCells(float i) {
		float xCoord = i;
		xCoord /= smoothScaleX;
		xCoord -= smoothOffsetX;
		return xCoord;
	}

	/**
	 * If the two scaling factors aren't equal, this sets the larger scaling factor to
	 * the smaller one in order to display square elements on screen
	 */
	public void shrinkToSquareAlignment() {
		if (getScaleY() < getScaleX()) {
			setScaleX(getScaleY());
		} else {
			setScaleY(getScaleX());
		}
	}

	//http://stackoverflow.com/questions/7896280/converting-from-hsv-hsb-in-java-to-rgb-without-using-java-awt-color-disallowe
	private static Color hsvToRgb(float hue, final float saturation, final float value) {

		while (hue >= 1) {
			hue -= 1;
		}
		while (hue < 0) {
			hue += 1;
		}
		int h = (int) (hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
			case 0:
				return new Color(value, t, p, 1);
			case 1:
				return new Color(q, value, p, 1);
			case 2:
				return new Color(p, value, t, 1);
			case 3:
				return new Color(p, q, value, 1);
			case 4:
				return new Color(t, p, value, 1);
			case 5:
				return new Color(value, p, q, 1);
			default:
				throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
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
	 * Retrieves the current x scaling factor that is used for the smooth
	 * animated camera.
	 */
	public float getSmoothScaleX() {
		return smoothScaleX;
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
	public void setScaleY(final float scaleY) {
		this.scaleY = scaleY;
	}

	/**
	 * Calculates the screen bounds
	 *
	 * @return the screen bounds
	 */
	public Rectangle getViewBounds() {
		Rectangle result = new Rectangle();

		float centerX = -offsetX;
		float width = Gdx.graphics.getWidth() * (1f / scaleX);
		float centerY = offsetY;
		float height = Gdx.graphics.getHeight() * (1f / scaleY);
		result.set(centerX - (width / 2f), centerY - (height / 2f), width, height);
		return result;
	}

	/**
	 * Sets the screen bounds
	 *
	 * @param bounds the area the viewport is supposed to show.
	 */
	public void setViewBounds(final Rectangle bounds) {
		float targetHeight = Gdx.graphics.getHeight() / bounds.height;
		float targetWidth = Gdx.graphics.getWidth() / bounds.width;
		float targetOffsetX = (bounds.x + (bounds.width / 2f));
		float targetOffsetY = bounds.y + (bounds.height / 2f);

		setScaleX(targetWidth);
		setScaleY(targetHeight);
		this.offsetX = -targetOffsetX;
		this.offsetY = targetOffsetY;
	}

	/**
	 * Resets the zoom to 1 px per element
	 */
	public void zoomTo1Px() {
		this.scaleX = 1;
		this.scaleY = 1;
	}


	/**
	 * Resets the zoom so that the whole chip is shown.
	 */
	public void zoomExtents() {
		// FIXME Does not properly handle non-0 minimum coordinates yet
		Point max = this.data.getMaxCoord();
		Point min = this.data.getMinCoord();
		logger.debug("Auto zoom around " + min + " <--/--> " + max);

		float x = (1f / (max.fst - min.fst + 2));
		float y = (1f / (max.snd - min.snd + 2));
		float xFactor = Gdx.graphics.getWidth();
		float yFactor = Gdx.graphics.getHeight();
		float maxScale = Math.min(x * xFactor, y * yFactor);
		this.scaleX = maxScale;
		this.scaleY = maxScale;
		this.offsetX = (max.fst) / -2f + min.fst / -2f;
		this.offsetY = (max.snd) / -2f + min.snd / -2f;


		logger.debug("Offset now at " + this.offsetX + "/" + this.offsetY);
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

	public void addTimeChangedListener(final BioVizEvent listener) {
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

	/**
	 * Retrieves the field that is currently being hovered.
	 * @return the currently hovered field.
	 */
	public DrawableField getHoveredField() {
		return this.hoveredField;
	}
}
