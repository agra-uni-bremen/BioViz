package de.dfki.revvisgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class RevVisInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown (int keycode) {
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		return false;
	}

	private int oldX, oldY;
	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		RevVisGDX.singleton.currentCircuit.offsetX += (x - oldX) / RevVisGDX.singleton.currentCircuit.scaleX;
		oldX = x;
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		RevVisGDX.singleton.currentCircuit.scaleX *= 1 + (amount * 0.2f);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		oldX = screenX;
		return false;
	}
}
