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
		//String c = character;
		switch (character) {
		case 'p':
			RevVisGDX.singleton.currentCircuit.pixelWideLines = !RevVisGDX.singleton.currentCircuit.pixelWideLines;
			break;
		case 'g':
			RevVisGDX.singleton.currentCircuit.colorizeGarbageLine = !RevVisGDX.singleton.currentCircuit.colorizeGarbageLine;
			break;
		case 'n':
			RevVisGDX.singleton.currentCircuit.drawGroups = !RevVisGDX.singleton.currentCircuit.drawGroups;
			break;
		case 'h':
			RevVisGDX.singleton.currentCircuit.hideGates = !RevVisGDX.singleton.currentCircuit.hideGates;
			break;
		case 'c':
			RevVisGDX.singleton.currentCircuit.countGatesForGroupColor = !RevVisGDX.singleton.currentCircuit.countGatesForGroupColor;
			break;
		case 's':
			RevVisGDX.singleton.currentCircuit.shrinkToSquareAlignment();
			break;
		}
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
		RevVisGDX.singleton.currentCircuit.offsetY += (y - oldY) / RevVisGDX.singleton.currentCircuit.scaleY;
		oldX = x;
		oldY = y;
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
			RevVisGDX.singleton.currentCircuit.scaleY *= 1 - (amount * 0.2f);
		else
			RevVisGDX.singleton.currentCircuit.scaleX *= 1 - (amount * 0.2f);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		oldX = screenX;
		oldY = screenY;
		return false;
	}
}
