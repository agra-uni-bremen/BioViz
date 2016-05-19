package de.bioviz.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import de.bioviz.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class BioVizSpriteBatch {

	private ArrayList<Pair<Integer, Runnable>> drawCalls =
			new ArrayList<Pair<Integer, Runnable>>();
	private SpriteBatch sb;
	public BioVizSpriteBatch(SpriteBatch sb) {
		this.sb = sb;
	}

	public void dispose() {
		sb.dispose();
	}

	public void setProjectionMatrix(Matrix4 matrix) {
		sb.setProjectionMatrix(matrix);
	}

	public void draw(Sprite s, Matrix4 m) {
		draw(s, m, 0);
	}

	public void draw(Sprite s, Matrix4 m, int z) {
		float[] vertices = s.getVertices().clone();
		Texture tex = s.getTexture();
		drawCalls.add(new Pair<Integer, Runnable>(
					z,
					() -> {
						Matrix4 old = sb.getProjectionMatrix();
						if (!(old.equals(m))) {
							sb.end();
							sb.setProjectionMatrix(m);
							sb.begin();
						}
						
						sb.draw(tex, vertices, 0, 20);
					}
				));
	}

	public void begin() {
		sb.begin();
	}

	public void end() {
		drawCalls.sort((dca, dcb) -> dca.fst.compareTo(dcb.fst));
		for (Pair<Integer, Runnable> pair : drawCalls) {
			pair.snd.run();
		}
		drawCalls.clear();
		sb.end();
	}

	public void drawMessage(
			BitmapFont messageFont,
			String message,
			float startX,
			float startY,
			Matrix4 m) {
		drawCalls.add(new Pair<Integer, Runnable>(
				0,
				() -> {
					Matrix4 old = sb.getProjectionMatrix();
					if (!(old.equals(m))) {
						sb.end();
						sb.setProjectionMatrix(m);
						sb.begin();
					}
					messageFont.draw(sb, message, startX, startY);
				}));
	}

	public void drawMessage(
			BitmapFont messageFont,
			GlyphLayout message,
			float startX,
			float startY,
			Matrix4 m) {
		drawCalls.add(new Pair<Integer, Runnable>(
				0,
				() -> {
					Matrix4 old = sb.getProjectionMatrix();
					if (!(old.equals(m))) {
						sb.end();
						sb.setProjectionMatrix(m);
						sb.begin();
					}
					messageFont.draw(sb, message, startX, startY);
				}));
	}

	public Matrix4 getProjectionMatrix() {
		return sb.getProjectionMatrix();
	}
}
