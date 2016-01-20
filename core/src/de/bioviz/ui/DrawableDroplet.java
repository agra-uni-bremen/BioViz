package de.bioviz.ui;

import de.bioviz.structures.Droplet;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Point;

public class DrawableDroplet extends DrawableSprite {

	public Droplet droplet;

	private DrawableRoute route;

	private static Random randnum = null;

	DrawableCircuit parentCircuit;

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
		route = new DrawableRoute(this);
	}

	@Override
	public String generateSVG() {
		return
				"<image x=\"" + this.droplet.smoothX + "\" " +
				"y=\"" +
				(-this.droplet.smoothY + parentCircuit.data.getMaxCoord().snd -
				 1) + "\" " +
				"width=\"1\" height=\"1\" xlink:href=\"droplet.svg\" />" +
				this.route.generateSVG();
	}


	public Color getDisplayColor() {

		DrawableCircuit circ = parentCircuit;

		Color color = this.getColor().cpy();

		Point p = droplet.getPositionAt(circ.currentTime);
		boolean withinTimeRange = false;

		if (p == null) {
			color = color.sub(0, 0, 0, 1).clamp();

			if (circ.currentTime < droplet.getSpawnTime()) {
				p = droplet.getFirstPosition();
			}
			else if (circ.currentTime > droplet.getMaxTime()) {
				p = droplet.getLastPosition();
			}
		}
		else {
			color = color.add(0, 0, 0, 1).clamp();
		}

		return color;
	}

	public String getMsg() {
		String msg = null;

		if (parentCircuit.displayOptions.getOption(
				BDisplayOptions.DropletIDs)) {
			msg = Integer.toString(droplet.getID()) + " ";
		}
		if (parentCircuit.displayOptions.getOption(BDisplayOptions.FluidIDs)) {
			// note: fluidID may be null!
			Integer fluidID = parentCircuit.data.fluidID(droplet.getID());
			if (fluidID != null) {
				msg = fluidID.toString() + " ";
			}
		}
		if (parentCircuit.displayOptions
				.getOption(BDisplayOptions.FluidNames)) {
			String fname = this.parentCircuit.data
					.fluidType(this.droplet.getID());
			//System.out.println("fname: " + fname);
			//System.out.println(this.parentCircuit.data.fluidTypes);
			if (fname != null) {
				msg += fname + " ";
			}
		}
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

				this.x = xCoord;
				this.y = yCoord;
				this.scaleX = circ.smoothScaleX;
				this.scaleY = circ.smoothScaleY;


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
}
