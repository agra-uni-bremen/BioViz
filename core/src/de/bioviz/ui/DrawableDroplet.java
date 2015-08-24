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
		super.color = new Color(randnum.nextInt());
		super.color.a = 1f;
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

		// TODO this casting probably is bad (although we should rarely reach the boundary of int)
		Point p = droplet.getPositionAt(circ.currentTime);

		if (p != null && BioViz.singleton.currentCircuit.getShowDroplets()) {


			droplet.targetX = p.first;
			droplet.targetY = p.second;

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
		else {
			// make sure that previous numbers are removed when the droplet is removed.
			BioViz.singleton.mc.removeHUDMessage(this.hashCode());
		}
	}
}
