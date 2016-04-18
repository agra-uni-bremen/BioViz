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
 *
 * @author Jannis Stoppe
 */
public class DrawableCircuit implements Drawable {
	/**
	 * This represents the actual biochip structure that is drawn by this
	 * DrawableCircuit.
	 */
	private Biochip data;

	/**
	 * The current UI scaling factor.
	 */
	private float scale = 1;

	/**
	 * The current UI x offset (used for camera movement etc.).
	 */
	private float offsetX = 0;

	/**
	 * The current UI scaling factor.
	 */
	private float scaleY = 1;

	/**
	 * The current UI y offset (used for camera movement etc.).
	 */
	private float offsetY = 0;

	/**
	 * The "smooth" scaling factor. Unlike the scale values, this does not
	 * represent the scale value that the user has last set but the one that
	 * is actually drawn. This is used for a smooth camera zoom, e.g. the user
	 * zooms in, which sets the scale value to a specific value and the
	 * smoothScale value is then over the duration of several frames slowly
	 * adjusted to the scale value in order to have a smoother cam movement.
	 */
	private float smoothScale = 1;

	/**
	 * The "smooth" offset. Unlike the offset values, this does not
	 * represent the offset value that the user has last set but the one that
	 * is actually drawn. This is used for a smooth camera movement, e.g. the
	 * user pans, which sets the offset value to a specific value and the
	 * smoothOffset value is then over the duration of several frames slowly
	 * adjusted to the offset value in order to have a smoother cam movement.
	 */
	private float smoothOffsetX = 0;

	/**
	 * The "smooth" offset. Unlike the offset values, this does not
	 * represent the offset value that the user has last set but the one that
	 * is actually drawn. This is used for a smooth camera movement, e.g. the
	 * user pans, which sets the offset value to a specific value and the
	 * smoothOffset value is then over the duration of several frames slowly
	 * adjusted to the offset value in order to have a smoother cam movement.
	 */
	private float smoothOffsetY = 0;

	/**
	 * The delay that is applied to the smooth cam movement.
	 * The value is altered every frame by a value of
	 * (offset - smoothOffset) / scalingDelay (respectively scale), so
	 * <b>increasing</b> this value results in <b>slower movement</b> of the
	 * camera.
	 */
	private float scalingDelay = 4f;

	/**
	 * The timestep of the data field that is currently being drawn.
	 */
	public int currentTime = 1;

	/**
	 * A flag that specifies whether or not the display automatically advances
	 * in time.
	 */
	public boolean autoAdvance = false;

	/**
	 * The speed at which the dispaly advances through time <b>if</b> the
	 * autoAdvance field is set.
	 */
	public float autoSpeed = 2f;

	/**
	 * The last timestep at which time was automatically advanced.
	 */
	private long lastAutoStepAt = new Date().getTime();

	/**
	 * This contains instances that need to be notified as soon as the
	 * currentTime value is altered.
	 */
	private Vector<BioVizEvent> timeChangedListeners =
			new Vector<BioVizEvent>();

	/**
	 * The fields that are present on this circuit.
	 */
	public Vector<DrawableField> fields = new Vector<>();

	/**
	 * The droplets that are present on this circuit.
	 */
	public Vector<DrawableDroplet> droplets = new Vector<>();

	/**
	 * The droplets that are present on this circuit but should not be drawn
	 * at this moment.
	 */
	public Vector<DrawableDroplet> hiddenDroplets = new Vector<>();

	/**
	 * The current displayOptions that determine drawing parameters.
	 */
	public DisplayOptions displayOptions = new DisplayOptions();

	/**
	 * The logger for this instance.
	 */
	static Logger logger = LoggerFactory.getLogger(DrawableCircuit.class);

	/**
	 * The visualization this circuit is drawn in.
	 */
	public BioViz parent;

	private int autoloop_OvertimeCounter = 0;

	/**
	 * The field that is currently hovered by the mouse.
	 * May be null, so be careful.
	 */
	private DrawableField hoveredField = null;

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
			if (timeStep >= 1 && timeStep <= getData().getMaxT()) {
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
		this.setData(toDraw);
		this.parent = parent;
		this.initializeDrawables();
		this.displayOptions.addOptionChangedEvent(e -> {
			if (e.equals(BDisplayOptions.CellUsage)) {
				boolean doIt = displayOptions.getOption(e);
				if (doIt) {
					getData().computeCellUsage();
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

		logger.debug("Initializing drawables: {} fields, {} droplets", getData().getAllCoordinates().size(), getData().getDroplets().size());

		//setup fields
		getData().getAllFields().forEach(fld -> {
			DrawableField f = new DrawableField(fld, this);
			this.fields.add(f);
		});

		logger.debug("Fields set up.");

		//setup droplets
		for (Droplet d : getData().getDroplets()) {
			DrawableDroplet dd = new DrawableDroplet(d, this);
			this.droplets.add(dd);
		}

		logger.debug("Droplets set up.");

		logger.debug("Drawable initialization successfully done.");
	}

	@Override
	public void draw() {
		setSmoothScale(getSmoothScale() + (getScaleX() - getSmoothScale()) / scalingDelay);
		setSmoothOffsetX(getSmoothOffsetX() + (getOffsetX() - getSmoothOffsetX()) / scalingDelay);
		setSmoothOffsetY(getSmoothOffsetY() + (getOffsetY() - getSmoothOffsetY()) / scalingDelay);

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

				logger.trace("data.getMaxT: {}\tcurrentTime: {}",getData().getMaxT(), currentTime);
				setCurrentTime(currentTime +1);
				if (currentTime >= getData().getMaxT() &&
						this.displayOptions.getOption(
								BDisplayOptions.LoopAutoplay)) {
					++autoloop_OvertimeCounter;
					if (autoloop_OvertimeCounter > 5) { //todo magic number
						setCurrentTime(1);
						autoloop_OvertimeCounter = 0;
					}
				}
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
	 * @author Jannis Stoppe
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
		if (this.getSmoothScale() < startFadingAtScale) {
			if (this.getSmoothScale() > endFadingAtScale) {
				float alpha = 1f - ((startFadingAtScale - getSmoothScale()) / (startFadingAtScale - endFadingAtScale));
				col.a = alpha;
			} else {
				// TODO: don't draw!
				col.a = 0;
			}
		}
		
		// scale text
		float scale = Math.min(MessageCenter.textRenderResolution, getSmoothScale() / 2f);
		
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
		xCoord += getSmoothOffsetX();
		xCoord *= getSmoothScale();
		return xCoord;
	}

	protected float yCoordOnScreen(int i) {
		return yCoordOnScreen((float) i);
	}

	protected float yCoordOnScreen(float i) {
		float yCoord = i;
		yCoord += getSmoothOffsetY();
		yCoord *= getSmoothScale();
		return yCoord;
	}

	protected float yCoordInCells(float i) {
		float yCoord = i;
		yCoord /= getSmoothScale();
		yCoord -= getSmoothOffsetY();
		return yCoord;
	}

	protected float xCoordInCells(float i) {
		float xCoord = i;
		xCoord /= getSmoothScale();
		xCoord -= getSmoothOffsetX();
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


	/**
	 * retrieves the current x scaling factor
	 */
	public float getScaleX() {
		return scale;
	}

	/**
	 * sets the current x scaling factor
	 * Keep in mind that the value used for actually drawing the
	 * circuit is successively approaching the given value for a
	 * smooth camera movement. Use setScaleImmediately if the viewport
	 * is supposed to skip those inbetween steps.
	 */
	public void setScaleX(float scaleX) {
		this.scale = scaleX;
	}
	
	/**
	 * Retrieves the current x scaling factor that is used for the smooth
	 * animated camera.
	 */
	public float getSmoothScaleX() {
		return getSmoothScale();
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

		float centerX = -getOffsetX();
		float width = Gdx.graphics.getWidth() * (1f / scale);
		float centerY = getOffsetY();
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
		this.setOffsetX(-targetOffsetX);
		this.setOffsetY(targetOffsetY);
	}

	/**
	 * Resets the zoom to 1 px per element
	 */
	public void zoomTo1Px() {
		this.scale = 1;
		this.scaleY = 1;
	}


	/**
	 * Resets the zoom so that the whole chip is shown.
	 */
	public void zoomExtents() {
		// FIXME Does not properly handle non-0 minimum coordinates yet
		Point max = this.getData().getMaxCoord();
		Point min = this.getData().getMinCoord();
		logger.debug("Auto zoom around " + min + " <--/--> " + max);

		float x = (1f / (max.fst - min.fst + 2));
		float y = (1f / (max.snd - min.snd + 2));
		float xFactor = Gdx.graphics.getWidth();
		float yFactor = Gdx.graphics.getHeight();
		float maxScale = Math.min(x * xFactor, y * yFactor);
		this.scale = maxScale;
		this.scaleY = maxScale;
		this.setOffsetX((max.fst) / -2f + min.fst / -2f);
		this.setOffsetY((max.snd) / -2f + min.snd / -2f);


		logger.debug("Offset now at " + this.getOffsetX() + "/" + this.getOffsetY());
	}

	/**
	 * Resets the zoom so that the whole circuit is shown without
	 * smoothly zooming to those settings.
	 */
	public void zoomExtentsImmediately() {
		zoomExtents();
		this.setSmoothScale(scale);
	}

	/**
	 * Sets the zoom to the given values without smoothly approaching
	 * those target values (instead sets them immediately).
	 */
	public void setScaleImmediately(float scale) {
		this.scale = scale;
		this.setSmoothScale(scale);
	}

	public void addTimeChangedListener(final BioVizEvent listener) {
		timeChangedListeners.add(listener);
	}

	/**
	 * Re-calculates the adjacency for all blobs and sets
	 * the fields' colours accordingly.
	 */
	public void updateAdjacencyColours() {
		Set<BiochipField> f = this.getData().getAdjacentActivations();
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

	public Biochip getData() {
		return data;
	}

	public void setData(Biochip data) {
		this.data = data;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	protected float getSmoothScale() {
		return smoothScale;
	}

	protected void setSmoothScale(float smoothScale) {
		this.smoothScale = smoothScale;
	}

	protected float getSmoothOffsetX() {
		return smoothOffsetX;
	}

	protected void setSmoothOffsetX(float smoothOffsetX) {
		this.smoothOffsetX = smoothOffsetX;
	}

	protected float getSmoothOffsetY() {
		return smoothOffsetY;
	}

	protected void setSmoothOffsetY(float smoothOffsetY) {
		this.smoothOffsetY = smoothOffsetY;
	}
}
