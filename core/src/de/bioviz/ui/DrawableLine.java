package de.bioviz.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.graphics.Color;

import de.bioviz.util.Pair;

public class DrawableLine extends DrawableSprite {

	static DrawableLine singleton = null;

	private static Queue<Pair<Pair<Pair<Float, Float>, Pair<Float, Float>>, Color>>
		drawQueue = new LinkedList<Pair<Pair<Pair<Float, Float>, Pair<Float, Float>>, Color>>();

	public DrawableLine(BioViz parent) {
		super(TextureE.BlackPixel, parent);
	}

	public static void draw(
			Pair<Float, Float> from, 
			Pair<Float, Float> to,
			Color col) {
		drawQueue.add(new Pair(new Pair(from, to), col));
	}
	
	public static void drawNow() {
		for (Pair<Pair<Pair<Float, Float>, Pair<Float, Float>>, Color> line : drawQueue) {
			Pair<Float, Float> from = line.fst.fst;
			Pair<Float, Float> to = line.fst.snd;
			Color col = line.snd;
			if (singleton != null) {
				Pair<Float, Float> toTarget = new Pair<Float, Float> (
						from.fst - to.fst, from.snd - to.snd);
				final float len = (float)Math.sqrt(
						toTarget.fst * toTarget.fst + toTarget.snd * toTarget.snd); 
				singleton.setX(singleton.viz.currentCircuit.xCoordOnScreen(
						(to.fst + from.fst) / 2f));
				singleton.setY(singleton.viz.currentCircuit.yCoordOnScreen(
						(to.snd + from.snd) / 2f));
				singleton.setScaleX(
						singleton.viz.currentCircuit.smoothScaleX * len);
				singleton.setScaleY(2f);
				singleton.setRotation((float)
						(Math.atan2(toTarget.snd, toTarget.fst) *
								(180f / Math.PI)));
				singleton.setColorImmediately(col);
				singleton.draw();
			}
		}
		drawQueue.clear();
	}
}
