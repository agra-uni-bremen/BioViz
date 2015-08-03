package de.dfki.bioviz.ui;

import de.dfki.bioviz.structures.Droplet;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;

public class DrawableDroplet extends DrawableSprite {
	
	public Droplet droplet;
	
	private DrawableRoute route;
	
	private static Random randnum = null;

	public DrawableDroplet(Droplet droplet) {
		super("Droplet.png");
		if (randnum == null)
			randnum = new Random();
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
		droplet.targetX = droplet.getXAt(BioViz.singleton.currentCircuit.currentTime);
		droplet.targetY = droplet.getYAt(BioViz.singleton.currentCircuit.currentTime);

		droplet.update();
		
		if (droplet.getXAt(BioViz.singleton.currentCircuit.currentTime) >= 0) {

			float xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(droplet.smoothX);
			float yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(droplet.smoothY);

			this.x = xCoord;
			this.y = yCoord;
			this.scaleX = BioViz.singleton.currentCircuit.smoothScaleX;
			this.scaleY = BioViz.singleton.currentCircuit.smoothScaleY;

			route.draw();

			super.draw();
		}
	}
}
