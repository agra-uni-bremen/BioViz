package de.bioviz.ui;

import de.bioviz.structures.Droplet;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawableDroplet extends DrawableSprite {

	static Logger logger = LoggerFactory.getLogger(DrawableDroplet.class);

	public Droplet droplet;

	public DrawableRoute route;

	private static Random randnum = null;

	public DrawableCircuit parentCircuit;

	private Color dropletColor;

	public DrawableDroplet(Droplet droplet, DrawableCircuit parent) {
		super(TextureE.Droplet, parent.getParent());
		this.parentCircuit = parent;
		if (randnum == null) {
			randnum = new Random();
		}
		this.droplet = droplet;
		super.addLOD(DEFAULT_LOD_THRESHOLD, TextureE.BlackPixel);
		randnum.setSeed(droplet.getID());
		super.setColor(new Color(randnum.nextInt()));
		Color c = super.getColor();
		c.a = 1f;
		super.setColor(c);
		this.dropletColor = c;
		route = new DrawableRoute(this);
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
			parentCircuit.getDisplayOptions().getOption(
					BDisplayOptions.NetColorOnDroplets)) {
			color = net.getColor().buildGdxColor();
		}


		Point p = droplet.getPositionAt(parentCircuit.getCurrentTime());

		// if the droplet is currently not present, make it 'invisible' by
		// making it totally transparent
		if (p == null) {
			color.sub(0, 0, 0, 1).clamp();

		}
		else {
			if (parentCircuit.getHiddenDroplets().contains(this)) {
				color.a = 0.25f;
			}
			else {
				color.add(0, 0, 0, 1).clamp();
			}
		}

		return color;
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

		boolean dispDropIDs = parentCircuit.getDisplayOptions().getOption(
				BDisplayOptions.DropletIDs);

		boolean dispFluidIDs = parentCircuit.getDisplayOptions().getOption(
				BDisplayOptions.FluidIDs);

		boolean dispFluidName = parentCircuit.getDisplayOptions()
				.getOption(BDisplayOptions.FluidNames);

		Integer fluidID = parentCircuit.getData().fluidID(dropID);

		if (dispDropIDs) {
			msgs.add(Integer.toString(dropID));
		}

		if (dispFluidIDs && fluidID != null) {
			msgs.add(fluidID.toString());
		}

		if (dispFluidName && fluidID != null) {
			String fname = parentCircuit.getData().fluidType(fluidID);

			if (fname != null) {
				msgs.add(fname);
			}
		}

		String msg = String.join(" - ", msgs);
		logger.trace("droplet msg after fluidNames option: {}", msg);
		return msg;
	}

	@Override
	public void draw() {

		DrawableCircuit circ = parentCircuit;

		Point p = droplet.getPositionAt(circ.getCurrentTime());
		boolean withinTimeRange = false;

		if (p == null) {

			if (circ.getCurrentTime() < droplet.getSpawnTime()) {
				p = droplet.getFirstPosition();
			}
			else if (circ.getCurrentTime() > droplet.getMaxTime()) {
				p = droplet.getLastPosition();
			}
		}
		else {
			withinTimeRange = true;
		}

		this.setColor(getDisplayColor());

		if (p != null) {
			droplet.setTargetPosition(p.fst, p.snd);
			droplet.update();
			route.draw();

			if (isVisible() && viz.currentCircuit.getDisplayOptions().
					getOption(BDisplayOptions.Droplets)) {

				float xCoord = circ.xCoordOnScreen(droplet.smoothX);
				float yCoord = circ.yCoordOnScreen(droplet.smoothY);

				this.setScaleX(circ.getSmoothScale());
				this.setScaleY(circ.getSmoothScale());

				// if hidden, place below grid
				int invisibleIndex =
						this.parentCircuit.getHiddenDroplets().indexOf(this);
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
		}
		if (!withinTimeRange) {
			// make sure that previous numbers are removed when the droplet is
			// removed.
			displayText(null);
		}
	}

	public void toggleGridVisibility() {
		if (parentCircuit.isHidden(this)) {
			parentCircuit.unHideDroplet(this);
		}
		else {
			parentCircuit.hideDroplet(this);
		}
	}

	public void setDropletColor(Color c) {
		this.dropletColor = c;
	}
}
