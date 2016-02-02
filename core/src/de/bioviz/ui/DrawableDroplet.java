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
			parentCircuit.displayOptions.getOption(BDisplayOptions
														   .NetColors)) {
			color = new Color(net.color);
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

		if (parentCircuit.displayOptions.getOption(
				BDisplayOptions.DropletIDs)) {
			msg = Integer.toString(droplet.getID()) + " ";

		}
		logger.trace("droplet msg after dropletIDs option: {}", msg);
		if (parentCircuit.displayOptions.getOption(BDisplayOptions.FluidIDs)) {
			// note: fluidID may be null!
			Integer fluidID = parentCircuit.data.fluidID(droplet.getID());
			if (fluidID != null) {
				if (!msg.isEmpty()) {
					msg += "-";
				}
				msg += " " + fluidID.toString() + " ";
			}

		}
		logger.trace("droplet msg after fluidIDs option: {}", msg);
		if (parentCircuit.displayOptions
				.getOption(BDisplayOptions.FluidNames)) {
			String fname = this.parentCircuit.data
					.fluidType(this.droplet.getID());
			//System.out.println("fname: " + fname);
			//System.out.println(this.parentCircuit.data.fluidTypes);
			if (fname != null) {
				if (!msg.isEmpty()) {
					msg += "-";
				}
				msg += " " + fname;
			}

		}
		logger.trace("droplet msg after fluidNames option: {}", msg);
		return msg;
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

			if (isVisible) {

				float xCoord = circ.xCoordOnScreen(droplet.smoothX);
				float yCoord = circ.yCoordOnScreen(droplet.smoothY);

				this.scaleX = circ.smoothScaleX;
				this.scaleY = circ.smoothScaleY;

				// if hidden, place below grid
				int invisibleIndex =
						this.parentCircuit.hiddenDroplets.indexOf(this);
				if (invisibleIndex >= 0) {

					this.scaleX = 32f;
					this.scaleY = 32f;

					xCoord = Gdx.graphics.getWidth() / 2f
							 - this.scaleX * (invisibleIndex + 1);
					yCoord = Gdx.graphics.getHeight() / 2f - this.scaleY;
				}

				this.x = xCoord;
				this.y = yCoord;

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
}
