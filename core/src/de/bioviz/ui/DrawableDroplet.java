package de.bioviz.ui;

import de.bioviz.structures.Droplet;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Point;

public class DrawableDroplet extends DrawableSprite {

	public Droplet droplet;

	private DrawableRoute route;

	private static Random randnum = null;

	public DrawableDroplet(Droplet droplet) {
		super("Droplet.png");
		if (randnum == null) {
			randnum = new Random();
		}
		this.droplet = droplet;
		super.addLOD(defaultLODThreshold, "BlackPixel.png");
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
						"y=\"" + (-this.droplet.smoothY + BioViz.singleton.currentCircuit.data.getMaxCoord().second - 1) + "\" " +
						"width=\"1\" height=\"1\" xlink:href=\"droplet.svg\" />" +
						this.route.generateSVG();
	}

	@Override
	public void draw() {

		DrawableCircuit circ = BioViz.singleton.currentCircuit;

		Point p = droplet.getPositionAt(circ.currentTime);
		boolean visible = false;

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
			visible = true;
		}

		if (p!= null && BioViz.singleton.currentCircuit.getShowDroplets()) {


			droplet.setTargetPosition(p.first, p.second);

			droplet.update();


			float xCoord = circ.xCoordOnScreen(droplet.smoothX);
			float yCoord = circ.yCoordOnScreen(droplet.smoothY);

			this.x = xCoord;
			this.y = yCoord;
			this.scaleX = circ.smoothScaleX;
			this.scaleY = circ.smoothScaleY;


			route.draw();

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
				BioViz.singleton.mc.addHUDMessage(this.hashCode(), msg, xCoord, yCoord);
			} else {
				BioViz.singleton.mc.removeHUDMessage(this.hashCode());
			}

			super.draw();
		}
		if (!visible) {
			// make sure that previous numbers are removed when the droplet is removed.
			BioViz.singleton.mc.removeHUDMessage(this.hashCode());
		}
	}
}
