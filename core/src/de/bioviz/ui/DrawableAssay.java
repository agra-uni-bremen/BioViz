/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Dispenser;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Sink;
import de.bioviz.util.Quadruple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The DrawableAssay class collects the data of an assay that is executed on
 * a biochip.
 * <p>
 * It also provides the means to draw it using libgdx.
 *
 * @author Jannis Stoppe
 */
public class DrawableAssay implements Drawable {

	/**
	 * The default value for the scalingDelay field.
	 */
	private static final float DEFAULT_SCALING_DELAY = 4f;

	/**
	 * The logger for this instance.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(DrawableAssay.class);

	/**
	 * This represents the actual biochip structure that is drawn by this
	 * DrawableAssay.
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
	 * represent the scale value that the user has last set but the one that is
	 * actually drawn. This is used for a smooth camera zoom, e.g. the user
	 * zooms in, which sets the scale value to a specific value and the
	 * smoothScale value is then over the duration of several frames slowly
	 * adjusted to the scale value in order to have a smoother cam movement.
	 */
	private float smoothScale = 1;

	/**
	 * The "smooth" offset. Unlike the offset values, this does not represent
	 * the offset value that the user has last set but the one that is actually
	 * drawn. This is used for a smooth camera movement, e.g. the user pans,
	 * which sets the offset value to a specific value and the smoothOffset
	 * value is then over the duration of several frames slowly adjusted to the
	 * offset value in order to have a smoother cam movement.
	 */
	private float smoothOffsetX = 0;

	/**
	 * The "smooth" offset. Unlike the offset values, this does not represent
	 * the offset value that the user has last set but the one that is actually
	 * drawn. This is used for a smooth camera movement, e.g. the user pans,
	 * which sets the offset value to a specific value and the smoothOffset
	 * value is then over the duration of several frames slowly adjusted to the
	 * offset value in order to have a smoother cam movement.
	 */
	private float smoothOffsetY = 0;

	/**
	 * The delay that is applied to the smooth cam movement. The value is
	 * altered every frame by a value of (offset - smoothOffset) / scalingDelay
	 * (respectively scale), so <b>increasing</b> this value results in
	 * <b>slower movement</b> of the camera.
	 */
	private float scalingDelay = DEFAULT_SCALING_DELAY;

	/**
	 * The timestep of the data field that is currently being drawn.
	 */
	private int currentTime = 1;

	/**
	 * A flag that specifies whether or not the display automatically advances
	 * in time.
	 */
	private boolean autoAdvance = false;

	/**
	 * The speed at which the dispaly advances through time <b>if</b> the
	 * autoAdvance field is set.
	 */
	private float autoDelay = 2f;

	/**
	 * The last timestep at which time was automatically advanced.
	 */
	private long lastAutoStepAt = new Date().getTime();

	/**
	 * This contains instances that need to be notified as soon as the
	 * currentTime value is altered.
	 */
	private ArrayList<BioVizEvent> timeChangedListeners = new ArrayList<>();

	/**
	 * The fields that are present on this circuit.
	 */
	private ArrayList<DrawableField> fields = new ArrayList<>();

	/**
	 * The droplets that are present on this circuit.
	 */
	private ArrayList<DrawableDroplet> droplets = new ArrayList<>();

	/**
	 * The droplets that are present on this circuit but should not be drawn at
	 * this moment.
	 */
	private ArrayList<DrawableDroplet> hiddenDroplets = new ArrayList<>();

	/**
	 * The nets that are contained within this assay.
	 * <p>
	 * (captain obvious to teh rescue!)
	 */
	private List<DrawableNet> nets = new ArrayList<>();

	/**
	 * The current displayOptions that determine drawing parameters.
	 */
	private DisplayOptions displayOptions = new DisplayOptions();

	/**
	 * The visualization this circuit is drawn in.
	 */
	private BioViz parent;

	/**
	 * This is a helper member that is incremented as soon as the time of the
	 * circuit advances beyond the maximum time. This value can then be
	 * compared to an arbitrary value and the whole circuit be reset to time 0
	 * after a certain grace period of being already past its final timestamp.
	 */
	private int autoloopOvertimeCounter = 0;


	/**
	 * This member stores the grace period after which the auto loop starts
	 * from
	 * the beginning.
	 *
	 * @see autoloopOvertimeCounter
	 */
	private int autoloopOvertimeGracePeriod = 5;

	/**
	 * The field that is currently hovered by the mouse. May be null, so be
	 * careful.
	 */
	private DrawableField hoveredField = null;

	/**
	 * The desired length of the displayed routes.
	 */
	private int displayRouteLength = 0;

	/**
	 * Creates a drawable entity based on the data given.
	 *
	 * @param toDraw
	 * 		the data to draw
	 * @param parent
	 * 		The visualization this circuit belongs to.
	 */
	DrawableAssay(final Biochip toDraw, final BioViz parent) {
		LOGGER.debug("Creating new drawable chip based on " + toDraw);
		this.setData(toDraw);
		this.setParent(parent);
		this.initializeDrawables();
		this.getDisplayOptions().addOptionChangedEvent(e -> {
			if (e.equals(BDisplayOptions.CellUsage) ||
				e.equals(BDisplayOptions.CellUsageCount)) {
				boolean doIt = getDisplayOptions().getOption(e);
				if (doIt) {
					getData().computeCellUsage();
				}
			}
		});
		LOGGER.debug("New DrawableAssay created successfully.");
	}

	/**
	 * Skip to the previous timestep.
	 */
	public void prevStep() {
		setAutoAdvance(false);
		setCurrentTime(getCurrentTime() - 1);
	}

	/**
	 * Advance to the next timestep.
	 */
	public void nextStep() {
		setAutoAdvance(false);
		setCurrentTime(getCurrentTime() + 1);
	}

	/**
	 * Toggle (ie. enable if disabled or vice versa) the auto advance mode that
	 * automatically advances through the timesteps.
	 */
	public void toggleAutoAdvance() {
		this.setAutoAdvance(!(this.isAutoAdvance()));
	}

	/**
	 * Jump to a specific timestep.
	 *
	 * @param timeStep
	 * 		the timestep to jump to.
	 */
	public void setCurrentTime(final int timeStep) {
		if (getParent() != null) {
			if (timeStep >= 1 && timeStep <= getData().getMaxT()) {
				currentTime = timeStep;
				getParent().callTimeChangedListeners();
			}
		} else {
			throw new RuntimeException("assay parent is null");
		}
	}

	/**
	 * Sets the length of the routes to be displayed.
	 *
	 * The value is capped at 0 and the maximal length of the routes present
	 * in the assay.
	 *
	 * @param length Length of the displayed routes.
	 */
	public void setDisplayRouteLength(final int length) {
		if (length >= 0 && length <= getData().getMaxRouteLength()) {
			displayRouteLength=length;
		}
	}

	/**
	 * Returns how long routes are to be drawn.
	 * @return The max length routes have when being displayed.
	 */
	public int getDisplayRouteLength() {
		return displayRouteLength;
	}


	/**
	 * Initializes the drawables according to the circuit stored in the data
	 * field.
	 */
	private void initializeDrawables() {
		// clear remaining old data first, if any
		this.getFields().clear();
		this.getDroplets().clear();

		LOGGER.debug("Initializing drawables: {} fields, {} droplets",
					 getData().getAllCoordinates().size(), getData()
							 .getDroplets().size());

		//setup fields
		getData().getAllFields().forEach(fld -> {
			if (fld instanceof Sink) {
				fields.add(new DrawableSink((Sink) fld, this));
			} else if (fld instanceof Dispenser) {
				fields.add(new DrawableDispenser((Dispenser) fld, this));
			} else {
				DrawableField f = new DrawableField(fld, this);
				fields.add(f);
			}

		});

		LOGGER.debug("Fields set up.");

		//setup droplets
		for (final Droplet d : getData().getDroplets()) {
			DrawableDroplet dd = new DrawableDroplet(d, this);
			this.getDroplets().add(dd);
		}

		LOGGER.debug("Droplets set up.");


		//setup nets
		for (final Net n : getData().getNets()) {
			DrawableNet nn = new DrawableNet(n, this);
			this.getNets().add(nn);
		}

		LOGGER.debug("Nets set up.");

		LOGGER.debug("Drawable initialization successfully done.");
	}

	@Override
	public void draw() {

		setSmoothScale(getSmoothScale() +
					   (getScaleX() - getSmoothScale()) / scalingDelay);
		setSmoothOffsetX(getSmoothOffsetX() +
						 (getOffsetX() - getSmoothOffsetX()) / scalingDelay);
		setSmoothOffsetY(getSmoothOffsetY() +
						 (getOffsetY() - getSmoothOffsetY()) / scalingDelay);

		if (getDisplayOptions().getOption(BDisplayOptions.Coordinates)) {
			displayCoordinates();
		} else {
			removeDisplayedCoordinates();
		}

		final long autoDelayScaling = 1000;
		final long scaledAutoDelay =
				(long) ((this.getAutoDelay()) * autoDelayScaling);
		if (isAutoAdvance()) {
			long current = new Date().getTime();
			if (lastAutoStepAt + scaledAutoDelay < current) {
				lastAutoStepAt = current;

				LOGGER.trace("data.getMaxT: {}\tcurrentTime: {}",
							 getData().getMaxT(), getCurrentTime());
				setCurrentTime(getCurrentTime() + 1);
				if (getCurrentTime() >= getData().getMaxT() &&
					this.getDisplayOptions().getOption(
							BDisplayOptions.LoopAutoplay)) {
					++autoloopOvertimeCounter;
					if (autoloopOvertimeCounter >
						autoloopOvertimeGracePeriod) {
						setCurrentTime(1);
						autoloopOvertimeCounter = 0;
					}
				}
			}
		}

		for (final DrawableField f : this.getFields()) {
			if (f.isHovered()) {
				this.hoveredField = f;
			}
			f.draw();
		}

		for (final DrawableDroplet d : this.getDroplets()) {
			d.draw();
		}

		for (final DrawableNet n : this.getNets()) {
			n.draw();
		}

	}


	/**
	 * Draws the coordinates of the grid on top of and to the left of the grid.
	 * This in fact uses the message center to display the numbers, so the
	 * actual drawing will be done after the rest has been drawn.
	 */
	private void displayCoordinates() {

		Quadruple<Integer, Integer, Integer, Integer> minMaxVals = minMaxXY();

		int minX = minMaxVals.fst;
		int minY = minMaxVals.snd;
		int maxX = minMaxVals.thd;
		int maxY = minMaxVals.fth;

		final int offset = 32;

		float topYCoord = Gdx.graphics.getHeight() / 2f - offset;
		if (topYCoord > this.yCoordOnScreen(maxY + 1)) {
			topYCoord = this.yCoordOnScreen(maxY + 1);
		}
		float leftXCoord = -Gdx.graphics.getWidth() / 2f + offset;
		if (leftXCoord < this.xCoordOnScreen(minX - 1)) {
			leftXCoord = this.xCoordOnScreen(minX - 1);
		}

		// Defines when numbers should start fading and be completely hidden
		final float startFadingAtScale = 32f;
		final float endFadingAtScale = 24f;

		Color col = Color.WHITE.cpy();
		if (this.getSmoothScale() < startFadingAtScale) {
			if (this.getSmoothScale() > endFadingAtScale) {
				float alpha = 1f - ((startFadingAtScale - getSmoothScale()) /
									(startFadingAtScale - endFadingAtScale));
				col.a = alpha;
			} else {
				col.a = 0;
			}
		}

		// indeed draw, top first, then left
		for (int i = minX; i < maxX + 1; i++) {
			this.getParent().messageCenter.addHUDMessage(
					this.hashCode() + i,    // unique ID for each message
					Integer.toString(i).trim(),    // message
					this.xCoordOnScreen(i),    // x
					topYCoord,                // y
					col                 // message color, used for fading
			);
		}

		for (int i = minY; i < maxY + 1; i++) {
			this.getParent().messageCenter.addHUDMessage(
					this.hashCode() + maxX + Math.abs(minY) + 1 + i,
					// unique ID for each message, starting after the previous
					// ids

					Integer.toString(i).trim(),    // message
					leftXCoord,                // x
					this.yCoordOnScreen(i), // y
					col                    // message color, used for fading
			);
		}
	}


	/**
	 * Calculates the current dimensions.
	 *
	 * @return Quadruple (minX,minY,maxX,maxY)
	 */
	private Quadruple<Integer, Integer, Integer, Integer> minMaxXY() {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (final DrawableField f : this.getFields()) {
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
		return new Quadruple<>(minX, minY, maxX, maxY);
	}

	private void removeDisplayedCoordinates() {
		Quadruple<Integer, Integer, Integer, Integer> minMaxVals = minMaxXY();

		int minX = minMaxVals.fst;
		int minY = minMaxVals.snd;
		int maxX = minMaxVals.thd;
		int maxY = minMaxVals.fth;

		// remove all HUD messages
		for (int i = minX; i < maxX + Math.abs(minY) + 2 + maxY; i++) {
			this.getParent().messageCenter.removeHUDMessage(
					this.hashCode() + i);
		}
	}

	/**
	 * Calculates the x coordinate of a given cell.
	 *
	 * @param i
	 * 		the cell index
	 * @return the x coordinate on screen
	 */
	protected float xCoordOnScreen(final int i) {
		return xCoordOnScreen((float) i);
	}

	/**
	 * Calculates the x coordinate of a given value. Keep in mind that this is
	 * still in cell-space, so a value of 0 would be at the center of the
	 * chip's
	 * first cell.
	 *
	 * @param i
	 * 		the value to translate
	 * @return the x coordinate on screen
	 */
	float xCoordOnScreen(final float i) {
		float xCoord = i;
		xCoord += getSmoothOffsetX();
		xCoord *= getSmoothScale();
		return xCoord;
	}

	float yCoordOnScreen(final int i) {
		return yCoordOnScreen((float) i);
	}

	float yCoordOnScreen(final float i) {
		float yCoord = i;
		yCoord += getSmoothOffsetY();
		yCoord *= getSmoothScale();
		return yCoord;
	}

	protected float yCoordInCells(final float i) {
		float yCoord = i;
		yCoord /= getSmoothScale();
		yCoord -= getSmoothOffsetY();
		return yCoord;
	}

	protected float xCoordInCells(final float i) {
		float xCoord = i;
		xCoord /= getSmoothScale();
		xCoord -= getSmoothOffsetX();
		return xCoord;
	}

	/**
	 * If the two scaling factors aren't equal, this sets the larger scaling
	 * factor to the smaller one in order to display square elements on screen.
	 */
	public void shrinkToSquareAlignment() {
		if (getScaleY() < getScaleX()) {
			setScaleX(getScaleY());
		} else {
			setScaleY(getScaleX());
		}
	}


	/**
	 * Retrieves the current x scaling factor.
	 *
	 * @return The Current x scaling factor.
	 */
	public float getScaleX() {
		return scale;
	}

	/**
	 * sets the current x scaling factor Keep in mind that the value used for
	 * actually drawing the circuit is successively approaching the given value
	 * for a smooth camera movement. Use setScaleImmediately if the viewport is
	 * supposed to skip those inbetween steps.
	 *
	 * @param scaleX
	 * 		The new value for the x scaling value.
	 */
	public void setScaleX(final float scaleX) {
		this.scale = scaleX;
	}

	/**
	 * Retrieves the current x scaling factor that is used for the smooth
	 * animated camera.
	 *
	 * @return The current x scaling factor.
	 */
	public float getSmoothScaleX() {
		return getSmoothScale();
	}

	/**
	 * Retrieves the current y scaling factor.
	 *
	 * @return The scaling factor for the y axis.
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * Sets the current y scaling factor. Keep in mind that the value used for
	 * actually drawing the circuit is successively approaching the given value
	 * for a smooth camera movement. Use setScaleImmediately if the viewport is
	 * supposed to skip those in between steps.
	 *
	 * @param scaleY
	 * 		The new scaling factor for the y axis.
	 */
	public void setScaleY(final float scaleY) {
		this.scaleY = scaleY;
	}

	/**
	 * Calculates the screen bounds.
	 *
	 * @return the screen bounds
	 */
	public Rectangle getViewBounds() {
		Rectangle result = new Rectangle();

		float centerX = -getOffsetX();
		float width = Gdx.graphics.getWidth() * (1f / scale);
		float centerY = getOffsetY();
		float height = Gdx.graphics.getHeight() * (1f / scaleY);
		result.set(centerX - (width / 2f), centerY - (height / 2f), width,
				   height);
		return result;
	}

	/**
	 * Sets the screen bounds.
	 *
	 * @param bounds
	 * 		the area the viewport is supposed to show.
	 */
	public void setViewBounds(final Rectangle bounds) {
		float targetHeight = Gdx.graphics.getHeight() / bounds.height;
		float targetWidth = Gdx.graphics.getWidth() / bounds.width;
		float targetOffsetX = bounds.x + (bounds.width / 2f);
		float targetOffsetY = bounds.y + (bounds.height / 2f);

		setScaleX(targetWidth);
		setScaleY(targetHeight);
		this.setOffsetX(-targetOffsetX);
		this.setOffsetY(targetOffsetY);
	}

	/**
	 * Resets the zoom to 1 px per element.
	 */
	public void zoomTo1Px() {
		this.scale = 1;
		this.scaleY = 1;
	}


	/**
	 * Resets the zoom so that the whole chip is shown.
	 */
	public void zoomExtents() {
		Point max = this.getData().getMaxCoord();
		Point min = this.getData().getMinCoord();
		LOGGER.debug("Auto zoom around " + min + " <--/--> " + max);

		float x = 1f / (max.fst - min.fst + 2);
		float y = 1f / (max.snd - min.snd + 2);
		float xFactor = Gdx.graphics.getWidth();
		float yFactor = Gdx.graphics.getHeight();
		float maxScale = Math.min(x * xFactor, y * yFactor);
		this.scale = maxScale;
		this.scaleY = maxScale;
		this.setOffsetX((max.fst) / -2f + min.fst / -2f);
		this.setOffsetY((max.snd) / -2f + min.snd / -2f);


		LOGGER.debug(
				"Offset now at " +
				this.getOffsetX() + "/" + this.getOffsetY());
	}

	/**
	 * Resets the zoom so that the whole circuit is shown without smoothly
	 * zooming to those settings.
	 */
	public void zoomExtentsImmediately() {
		zoomExtents();
		this.setSmoothScale(scale);
	}

	/**
	 * Sets the zoom to the given values without smoothly approaching those
	 * target values (instead sets them immediately).
	 *
	 * @param newScale
	 * 		The new immediate scale.
	 */
	public void setScaleImmediately(final float newScale) {
		this.scale = newScale;
		this.setSmoothScale(newScale);
	}

	public void addTimeChangedListener(final BioVizEvent listener) {
		timeChangedListeners.add(listener);
	}

	/**
	 * Retrieves the field that is currently being hovered.
	 *
	 * @return the currently hovered field.
	 */
	public DrawableField getHoveredField() {
		return this.hoveredField;
	}

	public Biochip getData() {
		return data;
	}

	public void setData(final Biochip data) {
		this.data = data;
	}

	float getOffsetX() {
		return offsetX;
	}

	void setOffsetX(final float offsetX) {
		this.offsetX = offsetX;
	}

	float getOffsetY() {
		return offsetY;
	}

	void setOffsetY(final float offsetY) {
		this.offsetY = offsetY;
	}

	float getSmoothScale() {
		return smoothScale;
	}

	protected float getSmoothOffsetX() {
		return smoothOffsetX;
	}

	protected void setSmoothOffsetX(final float smoothOffsetX) {
		this.smoothOffsetX = smoothOffsetX;
	}

	protected float getSmoothOffsetY() {
		return smoothOffsetY;
	}

	protected void setSmoothOffsetY(final float smoothOffsetY) {
		this.smoothOffsetY = smoothOffsetY;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public boolean isAutoAdvance() {
		return autoAdvance;
	}

	public void setAutoAdvance(final boolean autoAdvance) {
		this.autoAdvance = autoAdvance;
	}

	public float getAutoDelay() {
		return autoDelay;
	}

	public void setAutoDelay(final float autoDelay) {
		this.autoDelay = autoDelay;
	}

	public List<DrawableField> getFields() {
		return fields;
	}

	public void setFields(final ArrayList<DrawableField> fields) {
		this.fields = fields;
	}

	public List<DrawableDroplet> getDroplets() {
		return droplets;
	}

	public void setDroplets(final ArrayList<DrawableDroplet> droplets) {
		this.droplets = droplets;
	}

	/**
	 * Checks whether a certain droplet is hidden.
	 * <p>
	 * 'Hidden' means that the droplet is not present on the field as such but
	 * displayed on the top right corner.
	 *
	 * @param drop
	 * 		Droplet to test
	 * @return true if the droplet is hidden, false otherwise.
	 */
	public boolean isHidden(final DrawableDroplet drop) {
		return hiddenDroplets.contains(drop);
	}

	/**
	 * Hides a droplet.
	 *
	 * @param drop
	 * 		Droplet to hide
	 */
	public void hideDroplet(final DrawableDroplet drop) {
		hiddenDroplets.add(drop);
	}

	/**
	 * Unhides a droplet.
	 *
	 * @param drop
	 * 		The droplet to unhide.
	 */
	public void unHideDroplet(final DrawableDroplet drop) {
		hiddenDroplets.remove(drop);
	}

	public List<DrawableDroplet> getHiddenDroplets() {
		return hiddenDroplets;
	}

	public void setHiddenDroplets(final ArrayList<DrawableDroplet>
										  hiddenDroplets) {
		this.hiddenDroplets = hiddenDroplets;
	}

	public List<DrawableNet> getNets() {
		return nets;
	}

	public void setNets(final List<DrawableNet> nets) {
		this.nets.clear();
		this.nets.addAll(nets);
	}

	public DisplayOptions getDisplayOptions() {
		return displayOptions;
	}

	public void setDisplayOptions(final DisplayOptions displayOptions) {
		this.displayOptions = displayOptions;
	}

	public BioViz getParent() {
		return parent;
	}

	public void setParent(final BioViz parent) {
		this.parent = parent;
	}

	private void setSmoothScale(final float smoothScale) {
		this.smoothScale = smoothScale;
	}
}
