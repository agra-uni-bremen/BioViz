package de.bioviz.ui;

import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BioVizInputProcessor implements InputProcessor {
	boolean isMoving = false;
	boolean multiTouchZoom = false;
	private int oldX, oldY, oldX2, oldY2, oldDistanceX, oldDistanceY;
	
	private boolean ctrl, shift, alt;

	static Logger logger = LoggerFactory.getLogger((BioVizInputProcessor.class));
	
	@Override
	public boolean keyDown (int keycode) {
		if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrl = true;
		} else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT) {
			alt = true;
		} else if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shift = true;
		}
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrl = false;
		} else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT) {
			alt = false;
		} else if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shift = false;
		} else if (keycode == Keys.O) {
			if (ctrl) {
				BioViz.singleton.callLoadFileListeners();
			}
		} else if (keycode == Keys.S) {
			if (ctrl) {
				BioViz.singleton.callSaveFileListeners();
			}
		} else if (keycode == Keys.A) {
			BioViz.singleton.currentCircuit.toggleHighlightAdjacency();
		}
		else if (keycode == Keys.RIGHT || keycode == Keys.UP) {
			BioViz.singleton.currentCircuit.nextStep();
		}
		else if (keycode == Keys.LEFT || keycode == Keys.DOWN) {
			BioViz.singleton.currentCircuit.prevStep();
		}
		
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		DrawableCircuit dc; 
		switch (character) {
		case 's':
			BioViz.singleton.currentCircuit.shrinkToSquareAlignment();
			break;
		case 'z':
			BioViz.singleton.currentCircuit.zoomTo1Px();
			break;
		case 'Z':
			BioViz.singleton.currentCircuit.zoomExtents();
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
		
		logger.trace("TouchUp at " + x + "/" + y + " with " + pointer + "/" + button);
		
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		if (isMoving) {
			BioViz.singleton.currentCircuit.offsetX += (x - oldX) / BioViz.singleton.currentCircuit.getScaleX();
			BioViz.singleton.currentCircuit.offsetY -= (y - oldY) / BioViz.singleton.currentCircuit.getScaleY();
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
					BioViz.singleton.currentCircuit.setScaleX(BioViz.singleton.currentCircuit.getScaleX()
							* Math.max(1 - (zoomFactorX), 0.01f));
				}
				if (oldY - oldY2 != 0) {
					float zoomFactorY = (float)zoomY / Math.abs(oldY - oldY2);
					BioViz.singleton.currentCircuit.setScaleY(BioViz.singleton.currentCircuit.getScaleY()
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
		
		Rectangle current = BioViz.singleton.currentCircuit.getViewBounds();
		
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
		
		BioViz.singleton.currentCircuit.setViewBounds(current);
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
