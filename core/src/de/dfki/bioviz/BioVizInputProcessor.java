package de.dfki.bioviz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;

public class BioVizInputProcessor implements InputProcessor {
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
		switch (character) {
		case 's':
			BioVizGDX.singleton.currentCircuit.shrinkToSquareAlignment();
			break;
		case 'z':
			BioVizGDX.singleton.currentCircuit.zoomTo1Px();
			break;
		case 'Z':
			BioVizGDX.singleton.currentCircuit.zoomExtents();
			break;
		default:
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
		
		//RevVisGDX.singleton.menu.click(x, y);
		
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		if (isMoving) {
			BioVizGDX.singleton.currentCircuit.offsetX += (x - oldX) / BioVizGDX.singleton.currentCircuit.getScaleX();
			BioVizGDX.singleton.currentCircuit.offsetY -= (y - oldY) / BioVizGDX.singleton.currentCircuit.getScaleY();
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
					BioVizGDX.singleton.currentCircuit.setScaleX(BioVizGDX.singleton.currentCircuit.getScaleX()
							* Math.max(1 - (zoomFactorX), 0.01f));
				}
				if (oldY - oldY2 != 0) {
					float zoomFactorY = (float)zoomY / Math.abs(oldY - oldY2);
					BioVizGDX.singleton.currentCircuit.setScaleY(BioVizGDX.singleton.currentCircuit.getScaleY()
							* Math.max(1 - (zoomFactorY), 0.01f));
				}
			}
		}
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		float mouseAtWidth = (float)oldX / Gdx.graphics.getWidth();
		float mouseAtHeight = (float)oldY / Gdx.graphics.getHeight();
		
		Rectangle current = BioVizGDX.singleton.currentCircuit.getViewBounds();
		
		float mouseToLeftOriginal = current.width * mouseAtWidth;
		float mouseToBottomOriginal = current.height * mouseAtHeight;
		
		float zoomFactor = 1 + (amount * 0.2f);
		current.height *= zoomFactor;
		current.width *= zoomFactor;
		
		float expectedMouseToLeft = current.width * mouseAtWidth;
		float mouseDiffX = mouseToLeftOriginal - expectedMouseToLeft;
		current.x += mouseDiffX;
		
		float expectedMouseToBottom = current.height * mouseAtHeight;
		float mouseDiffY = mouseToBottomOriginal - expectedMouseToBottom;
		current.y += mouseDiffY;
		
		BioVizGDX.singleton.currentCircuit.setViewBounds(current);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		oldX = screenX;
		oldY = screenY;
		//RevVisGDX.singleton.menu.MouseCoords(screenX, screenY);
		return false;
	}
}
