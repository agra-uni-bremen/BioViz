package de.bioviz.ui;

import de.bioviz.structures.Droplet;

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
		super(TextureE.Droplet, parent.parent);
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

		DrawableCircuit circ = parentCircuit;

		Color color = this.dropletColor.cpy();

		Net net = droplet.getNet();
		if (net != null &&
			parentCircuit.displayOptions.getOption(
					BDisplayOptions.NetColorOnDroplets)) {
			color = net.getColor().buildGdxColor();
		}


		Point p = droplet.getPositionAt(circ.currentTime);

		// if the droplet is currently not present, make it 'invisible' by
		// making it totally transparent
		if (p == null) {
			color.sub(0, 0, 0, 1).clamp();

		}
		else {
			if (parentCircuit.hiddenDroplets.contains(this)) {
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
		String msg = "";

		int dropID = droplet.getID();

		if (parentCircuit.displayOptions.getOption(
				BDisplayOptions.DropletIDs)) {
			msg = Integer.toString(dropID) + " ";

		}
		if (parentCircuit.displayOptions.getOption(BDisplayOptions.FluidIDs)) {
			// note: fluidID may be null!
			Integer fluidID = parentCircuit.getData().fluidID(dropID);
			if (fluidID != null) {
				if (!msg.isEmpty()) {
					msg += "-";
				}
				msg += " " + fluidID.toString() + " ";
			}

		}
		if (parentCircuit.displayOptions
				.getOption(BDisplayOptions.FluidNames)) {
			Integer fluidID = parentCircuit.getData().fluidID(dropID);
			if (fluidID != null) {
				String fname = parentCircuit.getData().fluidType(fluidID);

				if (fname != null) {
					if (!msg.isEmpty()) {
						msg += "-";
					}
					msg += " " + fname;
				}
			}

		}
		logger.trace("droplet msg after fluidNames option: {}", msg);
		return msg.isEmpty() ? null : msg;
	}

	@Override
	public void draw() {

		DrawableCircuit circ = parentCircuit;

		Point p = droplet.getPositionAt(circ.currentTime);
		boolean withinTimeRange = false;

		if (p == null) {

			if (circ.currentTime < droplet.getSpawnTime()) {
				p = droplet.getFirstPosition();
			}
			else if (circ.currentTime > droplet.getMaxTime()) {
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

			if (isVisible() && viz.currentCircuit.displayOptions.
					getOption(BDisplayOptions.Droplets)) {

				float xCoord = circ.xCoordOnScreen(droplet.smoothX);
				float yCoord = circ.yCoordOnScreen(droplet.smoothY);

				this.setScaleX(circ.smoothScaleX);
				this.setScaleY(circ.smoothScaleY);

				// if hidden, place below grid
				int invisibleIndex =
						this.parentCircuit.hiddenDroplets.indexOf(this);
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
		if (parentCircuit.hiddenDroplets.contains(this)) {
			parentCircuit.hiddenDroplets.remove(this);
		}
		else {
			parentCircuit.hiddenDroplets.add(this);
		}
	}

	public void setDropletColor(Color c) {
		this.dropletColor = c;
	}
}
