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
		super("Droplet.png", parent.parent);
		this.parentCircuit = parent;
		if (randnum == null) {
			randnum = new Random();
		}
		this.droplet = droplet;
		super.addLOD(DEFAULT_LOD_THRESHOLD, "BlackPixel.png");
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
						"y=\"" + (-this.droplet.smoothY + parentCircuit.data.getMaxCoord().snd - 1) + "\" " +
						"width=\"1\" height=\"1\" xlink:href=\"droplet.svg\" />" +
						this.route.generateSVG();
	}

	@Override
	public void draw() {

		DrawableCircuit circ = parentCircuit;

		Point p = droplet.getPositionAt(circ.currentTime);
		boolean withinTimeRange = false;

		if (p == null) {

			if (circ.currentTime < droplet.getSpawnTime()) {
				p = droplet.getFirstPosition();
				this.setColor(this.getColor().cpy().sub(0, 0, 0, 1).clamp());
			} else if (circ.currentTime > droplet.getMaxTime()) {
				p = droplet.getLastPosition();
				this.setColor(this.getColor().cpy().sub(0, 0, 0, 1).clamp());
			}
		} else {
			this.setColor(this.getColor().cpy().add(0, 0, 0, 1).clamp());
			withinTimeRange = true;
		}

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


				String msg = null;

				if (circ.getDisplayDropletIDs()) {
					msg = Integer.toString(droplet.getID());
				}
				if (circ.getDisplayFluidIDs()) {
					// note: fluidID may be null!
					Integer fluidID = circ.data.fluidID(droplet.getID());
					if (fluidID != null) {
						msg = fluidID.toString();
					}
				}

				if (msg != null) {
					parentCircuit.parent.mc.addHUDMessage(this.hashCode(), msg, xCoord, yCoord);
				} else {
					parentCircuit.parent.mc.removeHUDMessage(this.hashCode());
				}

				super.draw();
			}
		}
		if (!withinTimeRange) {
			// make sure that previous numbers are removed when the droplet is removed.
			parentCircuit.parent.mc.removeHUDMessage(this.hashCode());
		}
	}
}
