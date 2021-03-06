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

import de.bioviz.structures.Droplet;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawableDroplet extends DrawableSprite {

	/**
	 * Internal logger.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(DrawableDroplet.class);

	/**
	 * Random number generator used to determine the droplet's color.
	 */
	private static Random randnum = null;

	/**
	 * Time needed for a droplet to move a distance of one field in ms.
	 */
	private static int transitionDuration = 500;

	/**
	 * The droplet that is to be drawn.
	 * <p>
	 * This variable contains the 'data' of the droplet.
	 */
	public Droplet droplet;


	/**
	 * The route of the droplet.
	 */
	public DrawableRoute route;


	/**
	 * The circuit the droplet belongs to.
	 */
	public DrawableAssay parentAssay;

	/**
	 * The x coordinate the droplet is currently drawn at.
	 */
	public float smoothX;

	/**
	 * The y coordinate the droplet is currently drawn at.
	 */
	public float smoothY;

	/**
	 * The width the droplet is currently drawn with.
	 */
	public float smoothWidth;

	/**
	 * The height the droplet is currently drawn with.
	 */
	public float smoothHeight;

	/**
	 * The next x position the droplet moves to.
	 */
	private float targetX;

	/**
	 * The next y position the droplet moves to.
	 */
	private float targetY;

	/**
	 * The next width the droplet should be.
	 */
	private float targetWidth;

	/**
	 * The next height the droplet should be.
	 */
	private float targetHeight;


	/**
	 * The droplet's current x position.
	 */
	private float originX;

	/**
	 * The droplet's current y position.
	 */
	private float originY;

	/**
	 * The droplet's smooth starting width.
	 */
	private float originWidth;

	/**
	 * The droplet's smooth starting height.
	 */
	private float originHeight;


	/**
	 * The time step in which the movement of the droplet begins.
	 */
	private long movementTransitionStartTime = 0;

	/**
	 * The time step in which the movement of the droplet ends.
	 */
	private long movementTransitionEndTime = 0;


	/**
	 * The droplet's color.
	 */
	private Color dropletColor;

	/**
	 * @param droplet
	 * 		The 'data' droplet that is to be visualized.
	 * @param parent
	 * 		The circuit the droplet belongs to.
	 */
	public DrawableDroplet(final Droplet droplet,
						   final DrawableAssay parent) {
		super(TextureE.Droplet, parent.getParent());


		this.parentAssay = parent;
		this.droplet = droplet;


		super.addLOD(DEFAULT_LOD_THRESHOLD, TextureE.BlackPixel);
		super.setZ(DisplayValues.DEFAULT_DROPLET_DEPTH);



		/*
		Compute the droplet's color
		 */
		if (randnum == null) {
			randnum = new Random();
		}
		randnum.setSeed(droplet.getID());
		super.setColor(new Color(randnum.nextInt()));
		Color c = super.getColor();
		c.a = 1f;
		super.setColor(c);
		this.dropletColor = c;
		///////////////////////////


		route = new DrawableRoute(this);

		/*
		Set the initial coordinates for the droplet.

		Jannis chose the upper left corner of the droplet to be the reference.
		 */
		Point p = droplet.getFirstPosition().upperLeft();
		smoothX = p.fst;
		smoothY = p.snd;
		originX = p.fst;
		originY = p.snd;
	}

	/**
	 * Getter for the animation duration of the droplet movement.
	 *
	 * @return The animation duration of the droplet movement..
	 */
	public static int getTransitionDuration() {
		return transitionDuration;
	}

	/**
	 * Sets the duration of the droplet movement animation.
	 *
	 * @param transitionDuration
	 * 		The new duration for the movement.
	 */
	public static void setTransitionDuration(final int transitionDuration) {
		DrawableDroplet.transitionDuration = transitionDuration;
	}

	public void updateCoords() {
		float totalProgress = 1;
		if (movementTransitionStartTime != movementTransitionEndTime) {
			float timeDiff = (float) (movementTransitionEndTime -
									  movementTransitionStartTime);
			float transitionProgress =
					Math.max(0, Math.min(1, (float) (
							new Date().getTime() - movementTransitionStartTime)
											/ timeDiff));
			totalProgress =
					(float) (-(Math.pow((transitionProgress - 1), 4)) + 1);
		}

		smoothX = this.originX * (1 - totalProgress) +
				  this.targetX * totalProgress;
		smoothY = this.originY * (1 - totalProgress) +
				  this.targetY * totalProgress;
		smoothWidth = this.originWidth * (1 - totalProgress) +
					  this.targetWidth * totalProgress;
		smoothHeight = this.originHeight * (1 - totalProgress) +
					   this.targetHeight * totalProgress;
	}

	/**
	 * Sets the target position of this droplet.
	 * <p>
	 * The target position is the position the droplet will move to in the next
	 * step. The actual movements takes place when the updateCoords() method is
	 * called.
	 *
	 * @param x
	 * 		x position of the target cell.
	 * @param y
	 * 		y position of the target cell.
	 */
	public void setTargetPosition(final float x, final float y) {
		if (this.targetX != x || this.targetY != y) {
			originX = this.smoothX;
			originY = this.smoothY;
			this.targetX = x;
			this.targetY = y;
			Date d = new Date();
			this.movementTransitionStartTime = d.getTime();
			this.movementTransitionEndTime =
					d.getTime() + transitionDuration;
		}
	}

	private void setScale(final float width, final float height) {
		boolean done = false;
		if (this.targetWidth != width || this.targetHeight != height) {
			done = true;
		}
		this.targetWidth = width;
		this.originWidth = this.smoothWidth;
		this.targetHeight = height;
		this.originHeight = this.smoothHeight;
		if (done) {
			Date d = new Date();
			this.movementTransitionStartTime = d.getTime();
			this.movementTransitionEndTime =
					d.getTime() + transitionDuration;
		}
	}


	/**
	 * Computes the droplet's color.
	 *
	 * @return The color used to display the droplet
	 */
	public Color getDisplayColor() {

		Color color = this.dropletColor.cpy();

		Net net = droplet.getNet();
		if (net != null &&
			parentAssay.getDisplayOptions().getOption(
					BDisplayOptions.NetColorOnDroplets)) {
			color = net.getColor().buildGdxColor();
		}

		Rectangle pos = droplet.getPositionAt(parentAssay.getCurrentTime());

		if (pos != null) {
			Point p = pos.upperLeft();

			// if the droplet is currently not present, make it 'invisible' by
			// making it totally transparent
			if (p == null) {
				color.sub(Color.BLACK).clamp();

			} else {
				if (this.isHidden()) {
					color.a = 0.25f;
				} else {
					color.add(Color.BLACK).clamp();
				}
			}
		} else {
			color.sub(Color.BLACK).clamp();
		}

		return color;
	}

	/**
	 * @return True if the droplet should be hidden.b
	 * @brief Checks whether this droplet is to be drawn or not.
	 */
	public boolean isHidden() {
		return parentAssay.getHiddenDroplets().contains(this);
	}

	/**
	 * Computes the text that is displayed on top of the droplet.
	 * <p>
	 * This text may e.g. be the droplet's ID or the fluid type.
	 *
	 * @return Text to be displayed on top of the droplets
	 */
	public String getMsg() {
		ArrayList<String> msgs = new ArrayList<>();

		int dropID = droplet.getID();

		boolean dispDropIDs = parentAssay.getDisplayOptions().getOption(
				BDisplayOptions.DropletIDs);

		boolean dispFluidIDs = parentAssay.getDisplayOptions().getOption(
				BDisplayOptions.FluidIDs);

		boolean dispFluidName = parentAssay.getDisplayOptions()
				.getOption(BDisplayOptions.FluidNames);

		Integer fluidID = parentAssay.getData().fluidID(dropID);

		if (dispDropIDs) {
			msgs.add(Integer.toString(dropID));
		}

		if (dispFluidIDs && fluidID != null) {
			msgs.add(fluidID.toString());
		}

		if (dispFluidName && fluidID != null) {
			String fname = parentAssay.getData().fluidType(fluidID);

			if (fname != null) {
				msgs.add(fname);
			}
		}

		String msg = String.join(" - ", msgs);
		logger.trace("droplet msg after fluidNames option: {}", msg);
		return msg;
	}

	@Override
	/**
	 * Draws the droplet.
	 *
	 * It performs the following necessary computations
	 * - determine the position depending on the current time
	 *   depending on the 'hidden' status of the droplet, it might get updated
	 *   to reflect this fact.
	 * - determine the color of the droplet
	 * - determine the text that is displayed on top of the droplet
	 *   (e.g. ID or fluid type)
	 */
	public void draw() {

		DrawableAssay circ = parentAssay;

		Rectangle p = droplet.getPositionAt(circ.getCurrentTime());
		boolean withinTimeRange = false;

		if (p == null) {

			if (circ.getCurrentTime() < droplet.getSpawnTime()) {
				p = droplet.getFirstPosition();
			} else if (circ.getCurrentTime() > droplet.getMaxTime()) {
				p = droplet.getLastPosition();
			}
		} else {
			withinTimeRange = true;
		}

		this.setColor(getDisplayColor());

		Point upperLeft = p.upperLeft();
		Point size = p.size();
		setScale(size.fst, size.snd);

		// at this point, p is definitely not null. The getFirst/LastPosition
		// methods would have thrown an exception

		this.setTargetPosition(upperLeft.fst, upperLeft.snd);
		this.updateCoords();

		// only draw the route when the droplet is to be displayed normally.
		if (!this.isHidden()) {
			route.draw();
		}


		if (isVisible() && viz.currentAssay.getDisplayOptions().
				getOption(BDisplayOptions.Droplets)) {


			float xCoord = circ.xCoordOnScreen(
					smoothX + (smoothWidth - 1) / 2f);
			float yCoord = circ.yCoordOnScreen(
					smoothY - (smoothHeight - 1) / 2f);

			this.setScaleX(circ.getSmoothScale() * smoothWidth);
			this.setScaleY(circ.getSmoothScale() * smoothHeight);

			// if hidden, place below grid
			int invisibleIndex =
					this.parentAssay.getHiddenDroplets().indexOf(this);
			if (invisibleIndex >= 0) {

				this.setScaleX(32f);
				this.setScaleY(32f);

				xCoord = Gdx.graphics.getWidth() / 2f
						 - this.getScaleX() * (invisibleIndex + 1);
				yCoord = Gdx.graphics.getHeight() / 2f - this.getScaleY();
			}

			this.setX(xCoord);
			this.setY(yCoord);

			String msg = getMsg();

			displayText(msg);

			super.draw();
		}

		if (!withinTimeRange) {
			// make sure that previous numbers are removed when the droplet is
			// removed.
			displayText(null);
		}
	}

	/**
	 * Toggles the visibility of the droplet.
	 */
	public void toggleGridVisibility() {
		if (parentAssay.isHidden(this)) {
			parentAssay.unHideDroplet(this);
		} else {
			parentAssay.hideDroplet(this);
		}
	}

	/**
	 * Sets the droplet's color.
	 *
	 * @param c
	 * 		New color of the droplet.
	 */
	public void setDropletColor(final Color c) {
		this.dropletColor = c;
	}
}
