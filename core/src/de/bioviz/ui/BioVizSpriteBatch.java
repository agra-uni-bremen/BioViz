package de.bioviz.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class BioVizSpriteBatch {

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

	public void draw(Sprite s) {
		sb.draw(s.getTexture(), s.getVertices(), 0, 20);
	}

	public void begin() {
		sb.begin();
	}

	public void end() {
		sb.end();
	}
}
