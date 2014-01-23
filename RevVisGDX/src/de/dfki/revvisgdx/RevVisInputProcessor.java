package de.dfki.revvisgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class RevVisInputProcessor implements InputProcessor {
	boolean isMoving = false;
	boolean multiTouchZoom = false;
	private int oldX, oldY, oldX2, oldY2, oldDistanceX, oldDistanceY;
	
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
		case 'g':
			RevVisGDX.singleton.currentCircuit.colorizeGarbageLine = !RevVisGDX.singleton.currentCircuit.colorizeGarbageLine;
			break;
		case 'n':
			RevVisGDX.singleton.currentCircuit.toggleNeighbourGrouping();
			break;
		case 'S':
			RevVisGDX.singleton.currentCircuit.hideGates = !RevVisGDX.singleton.currentCircuit.hideGates;
			break;
		case 's':
			RevVisGDX.singleton.currentCircuit.shrinkToSquareAlignment();
			break;
		case 'c':
			RevVisGDX.singleton.currentCircuit.colorizeConstants = !RevVisGDX.singleton.currentCircuit.colorizeConstants;
			break;
		case 'v':
			RevVisGDX.singleton.currentCircuit.drawVerticalLines = !RevVisGDX.singleton.currentCircuit.drawVerticalLines;
			break;
		case 'd':
			RevVisGDX.singleton.currentCircuit.drawLinesDarkWhenUsed = !RevVisGDX.singleton.currentCircuit.drawLinesDarkWhenUsed;
			break;
		case 'm':
			RevVisGDX.singleton.currentCircuit.colourizeGatesByMobility = !RevVisGDX.singleton.currentCircuit.colourizeGatesByMobility;
			break;
		case 'M':
			RevVisGDX.singleton.currentCircuit.drawAccumulatedMovingRule = !RevVisGDX.singleton.currentCircuit.drawAccumulatedMovingRule;
			break;
		case 'h':
			RevVisGDX.singleton.currentCircuit.highlightHoveredGate = !RevVisGDX.singleton.currentCircuit.highlightHoveredGate;
			break;
		case 'H':
			RevVisGDX.singleton.currentCircuit.highlightHoveredGateMovingRule = !RevVisGDX.singleton.currentCircuit.highlightHoveredGateMovingRule;
			break;
		case 'u':
			RevVisGDX.singleton.currentCircuit.colorizeLineUsage = !RevVisGDX.singleton.currentCircuit.colorizeLineUsage;
			break;
		case 'w':
			RevVisGDX.singleton.currentCircuit.toggleLineWidth();
			break;
		case 'b':
			RevVisGDX.singleton.currentCircuit.toggleBusDrawing();
			break;
		}
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		if (pointer == 0) {
			oldX = x;
			oldY = y;
			isMoving = true;
		} else if (pointer == 1) {
			isMoving = false;
			multiTouchZoom = true;
			oldX2 = x;
			oldY2 = y;
		}
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		if (pointer == 0) {
			isMoving = false;
		} else if (pointer == 1) {
			isMoving = true;
			multiTouchZoom = false;
		}
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		if (isMoving) {
			RevVisGDX.singleton.currentCircuit.offsetX += (x - oldX) / RevVisGDX.singleton.currentCircuit.getScaleX();
			RevVisGDX.singleton.currentCircuit.offsetY += (y - oldY) / RevVisGDX.singleton.currentCircuit.getScaleY();
			oldX = x;
			oldY = y;
		} else if (multiTouchZoom) {
			if (pointer == 0) {
				
				oldDistanceX = Math.abs(oldX - oldX2);
				oldDistanceY = Math.abs(oldY - oldY2);
				
				oldX = x;
				oldY = y;
			} else if (pointer == 1) {
				oldX2 = x;
				oldY2 = y;
				
				int zoomX = oldDistanceX - Math.abs(oldX - oldX2);
				int zoomY = oldDistanceY - Math.abs(oldY - oldY2);
				
				if (oldX - oldX2 != 0) {
					float zoomFactorX = (float)zoomX / Math.abs(oldX - oldX2);
					RevVisGDX.singleton.currentCircuit.setScaleX(RevVisGDX.singleton.currentCircuit.getScaleX()
							* Math.max(1 - (zoomFactorX), 0.01f));
				}
				if (oldY - oldY2 != 0) {
					float zoomFactorY = (float)zoomY / Math.abs(oldY - oldY2);
					RevVisGDX.singleton.currentCircuit.setScaleY(RevVisGDX.singleton.currentCircuit.getScaleY()
							* Math.max(1 - (zoomFactorY), 0.01f));
				}
			}
		}
		RevVisGDX.singleton.mc.addHUDMessage(this.hashCode(), "Mouse now at " + oldX + ",  " + oldY, 32, 32);
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
			RevVisGDX.singleton.currentCircuit.setScaleY(RevVisGDX.singleton.currentCircuit.getScaleY()
					* (1 - (amount * 0.2f)));
		else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
			RevVisGDX.singleton.currentCircuit.setScaleX(RevVisGDX.singleton.currentCircuit.getScaleX()
					* (1 - (amount * 0.2f)));
		else {
			RevVisGDX.singleton.currentCircuit.setScaleY(RevVisGDX.singleton.currentCircuit.getScaleY()
					* (1 - (amount * 0.2f)));
			RevVisGDX.singleton.currentCircuit.setScaleX(RevVisGDX.singleton.currentCircuit.getScaleX()
					* (1 - (amount * 0.2f)));
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		oldX = screenX;
		oldY = screenY;
		RevVisGDX.singleton.mc.addHUDMessage(this.hashCode(), "Mouse now at " + oldX + ",  " + oldY, 32, 32);
		RevVisGDX.singleton.currentCircuit.highlightAt(screenX, screenY);
		return false;
	}
}
