package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

import de.bioviz.util.Pair;

public class DrawableLine extends DrawableSprite {

	static DrawableLine singleton = null;

	public DrawableLine(BioViz parent) {
		super(TextureE.BlackPixel, parent);
	}

	protected static void draw(
			Pair<Float, Float> from, 
			Pair<Float, Float> to,
			Color col) {
		if (singleton != null) {
			Pair<Float, Float> toTarget = new Pair<Float, Float> (
					from.fst - to.fst, from.snd - to.snd);
			final float len = (float)Math.sqrt(
					toTarget.fst * toTarget.fst + toTarget.snd * toTarget.snd); 
			singleton.x = singleton.viz.currentCircuit.xCoordOnScreen(
					(to.fst + from.fst) / 2f);
			singleton.y = singleton.viz.currentCircuit.yCoordOnScreen(
					(to.snd + from.snd) / 2f);
			singleton.scaleX = singleton.viz.currentCircuit.smoothScaleX * len;
			singleton.scaleY = 2f;
			singleton.rotation = (float)
					(Math.atan2(toTarget.snd, toTarget.fst) * (180f / Math.PI));
			singleton.setColorImmediately(col);
			singleton.draw();
		}
	}
}
