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

/**
 * This class serves as a wrapper around the original libgdx sprite batch but
 * adds depth sorting functionality.
 *
 * As the original sprite batch simply takes all draw calls and aggregates them
 * to be executed in the same order they were placed in, ordering all elements
 * as desired at some point got too messy to be done via ordering the calls.
 * This class therefore collects them to be sorted and executed via a given
 * priority value.
 *
 * @author jannis
 *
 */
public class BioVizSpriteBatch {
	/**
	 * The original sprite batch to draw things in.
	 */
	private SpriteBatch sb;

	/**
	 * This field collects all draw calls and their associated priority.
	 */
	private ArrayList<Pair<Float, Runnable>> drawCalls =
			new ArrayList<Pair<Float, Runnable>>();

	/**
	 * Creates a new batch to draw elements with.
	 * @param sb the original libgdx batch to be wrapped.
	 */
	public BioVizSpriteBatch(SpriteBatch sb) {
		this.sb = sb;
	}

	/**
	 * Disposes this batch.
	 */
	public void dispose() {
		sb.dispose();
	}

	/**
	 * Sets a new projection matrix by calling the wrapped batch's
	 * setProjectionMatrix method.
	 * @param matrix the matrix to be used, retrieve from the camera.
	 */
	public void setProjectionMatrix(Matrix4 matrix) {
		sb.setProjectionMatrix(matrix);
	}

	/**
	 * Draws a sprite using a given matrix.
	 * This sets the depth/priority value to 0.
	 * @param s the sprite to be drawn.
	 * @param m the matrix to transform the sprite with.
	 */
	public void draw(Sprite s, Matrix4 m) {
		draw(s, m, 0);
	}

	/**
	 * Draws a sprite using a given matrix at a given depth.
	 * @param s the sprite to be drawn.
	 * @param m the matrix to use.
	 * @param z the depth value of the sprite.
	 */
	public void draw(Sprite s, Matrix4 m, float z) {
		float[] vertices = s.getVertices().clone();
		Texture tex = s.getTexture();
		drawCalls.add(new Pair<Float, Runnable>(
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

	/**
	 * Starts a new bulk draw. Call this before calling any draw methods.
	 */
	public void begin() {
		sb.begin();
	}

	/**
	 * Ends the bulk draw. This sorts the given draw calls and executes the
	 * original batch's draw functions in the order specified.
	 */
	public void end() {
		drawCalls.sort((dca, dcb) -> dca.fst.compareTo(dcb.fst));
		for (Pair<Float, Runnable> pair : drawCalls) {
			pair.snd.run();
		}
		drawCalls.clear();
		sb.end();
	}

	/**
	 * Draws a message.
	 * @param messageFont the font to use.
	 * @param message the message to draw.
	 * @param startX the x coordinate.
	 * @param startY the y coordinate.
	 * @param m the transformation matrix to use.
	 */
	public void drawMessage(
			BitmapFont messageFont,
			String message,
			float startX,
			float startY,
			Matrix4 m) {
		drawCalls.add(new Pair<Float, Runnable>(
				0f,
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

	/**
	 * Draws a message.
	 * @param messageFont the font to use.
	 * @param message the message to draw.
	 * @param startX the x coordinate.
	 * @param startY the y coordinate.
	 * @param m the transformation matrix to use.
	 */
	public void drawMessage(
			BitmapFont messageFont,
			GlyphLayout message,
			float startX,
			float startY,
			Matrix4 m) {
		drawCalls.add(new Pair<Float, Runnable>(
				0f,
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

	/**
	 * Retrieves the current projection matrix.
	 * @return the matrix that is currently in use.
	 */
	public Matrix4 getProjectionMatrix() {
		return sb.getProjectionMatrix();
	}
}
