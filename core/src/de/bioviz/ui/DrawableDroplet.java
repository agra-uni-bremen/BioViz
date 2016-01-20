package de.bioviz.ui;

import de.bioviz.structures.Droplet;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.bioviz.structures.Point;

public class DrawableDroplet extends DrawableSprite {

	public Droplet droplet;

	public DrawableRoute route;

	private static Random randnum = null;

	public DrawableCircuit parentCircuit;

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

	public Color getDisplayColor() {

		DrawableCircuit circ = parentCircuit;

		Color color = this.getColor().cpy();

		Point p = droplet.getPositionAt(circ.currentTime);

		if (p == null) {
			color.sub(0, 0, 0, 1).clamp();

			if (circ.currentTime < droplet.getSpawnTime()) {
				p = droplet.getFirstPosition();
			} else if (circ.currentTime > droplet.getMaxTime()) {
				p = droplet.getLastPosition();
			}
		} else {
			if (parentCircuit.hiddenDroplets.contains(this)){
				color.a = 0.25f;
			} else {
				color.add(0, 0, 0, 1).clamp();
			}
		}

		return color;
	}

	public String getMsg() {
		String msg = null;

		if (parentCircuit.displayOptions.getOption(BDisplayOptions.DropletIDs)) {
			msg = Integer.toString(droplet.getID());
		}
		if (parentCircuit.displayOptions.getOption(BDisplayOptions.FluidIDs)) {
			// note: fluidID may be null!
			Integer fluidID = parentCircuit.data.fluidID(droplet.getID());
			if (fluidID != null) {
				msg = fluidID.toString();
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
			
			} else if (circ.currentTime > droplet.getMaxTime()) {
				p = droplet.getLastPosition();
						}
		} else {
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
							- this.scaleX *(invisibleIndex + 1);
					yCoord = Gdx.graphics.getHeight() / 2f - this.scaleY;
				}
				
				this.x = xCoord;
				this.y = yCoord;

				String msg= getMsg();

				displayText(msg);

				super.draw();
			}
		}
		if (!withinTimeRange) {
			// make sure that previous numbers are removed when the droplet is removed.
			displayText(null);
		}
	}
	
	public void toggleGridVisibility() {
		if (parentCircuit.hiddenDroplets.contains(this)) {
			parentCircuit.hiddenDroplets.remove(this);
		} else {
			parentCircuit.hiddenDroplets.add(this);
		}
	}
}
