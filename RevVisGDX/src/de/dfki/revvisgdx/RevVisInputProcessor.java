package de.dfki.revvisgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;

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
		DrawableCircuit dc; 
		switch (character) {
		case 'g':
			RevVisGDX.singleton.currentCircuit.colorizeGarbageLine = !RevVisGDX.singleton.currentCircuit.colorizeGarbageLine;
			break;
		case 'G':
			RevVisGDX.singleton.currentCircuit.toggleGateDisplay();
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
		case 'C':
			RevVisGDX.singleton.currentCircuit.drawSubCircuits = !RevVisGDX.singleton.currentCircuit.drawSubCircuits;
			break;
		case 'v':
			// done in gui
			RevVisGDX.singleton.currentCircuit.drawVerticalLines = !RevVisGDX.singleton.currentCircuit.drawVerticalLines;
			break;
		case 'd':
			// done in gui
			RevVisGDX.singleton.currentCircuit.drawLinesDarkWhenUsed = !RevVisGDX.singleton.currentCircuit.drawLinesDarkWhenUsed;
			break;
		case 'm':
			RevVisGDX.singleton.currentCircuit.toggleMobilityGateColours();
			break;
		case 'M':
			RevVisGDX.singleton.currentCircuit.drawAccumulatedMovingRule = !RevVisGDX.singleton.currentCircuit.drawAccumulatedMovingRule;
			break;
		case 'h':
			RevVisGDX.singleton.currentCircuit.highlightHoveredGate = !RevVisGDX.singleton.currentCircuit.highlightHoveredGate;
			break;
		case 'H':
			// done in gui
			RevVisGDX.singleton.currentCircuit.toggleMovingRuleHighlight();
			break;
		case 'u':
			// seems to be unused, not done in gui
			RevVisGDX.singleton.currentCircuit.colorizeLineUsage = !RevVisGDX.singleton.currentCircuit.colorizeLineUsage;
			break;
		case 'U':
			// done in gui
			RevVisGDX.singleton.currentCircuit.toggleLineUsageColouring();
			break;
		case 'w':
			// done in gui
			RevVisGDX.singleton.currentCircuit.toggleLineWidth();
			break;
		case 'b':
			RevVisGDX.singleton.currentCircuit.toggleBusDrawing();
			break;
		case 'p':
			RevVisGDX.singleton.saveScreenshotFull();
			break;
		case 'P':
			RevVisGDX.singleton.saveScreenshotCircuit();
			break;
		case 'r':
			dc = RevVisGDX.singleton.currentCircuit;
			if (dc instanceof DrawableCircuitReordered) {
				DrawableCircuitReordered dcr = (DrawableCircuitReordered)dc;
				dcr.drawReordered = !dcr.drawReordered;
			}
			break;
		case 'R':
			dc = RevVisGDX.singleton.currentCircuit;
			if (dc instanceof DrawableCircuitReordered) {
				DrawableCircuitReordered dcr = (DrawableCircuitReordered)dc;
				dcr.drawShifted = !dcr.drawShifted;
			}
			break;
		case 'z':
			RevVisGDX.singleton.currentCircuit.zoomTo1Px();
			break;
		case 'Z':
			RevVisGDX.singleton.currentCircuit.zoomExtents();
			break;
		case '1':
			// done in gui
			Presets.setConstGarbage();
			break;
		case '2':
			// done in gui
			Presets.setBoxesAndUsage();
			break;
		case '3':
			// done in gui
			Presets.setColourizedUsage();
			break;
		case '4':
			// done in gui
			Presets.setGreyNeighboursWithBlackTargets();
			break;
		case '5':
			// done in gui
			Presets.setColourizeLineType();
			break;
		case '6':
			// done in gui
			Presets.setMovingRuleBoxOverlay();
			break;
		case '7':
			// done in gui
			Presets.setMovingRuleColoured();
			break;
		case '8':
			// done in gui
			Presets.setColourizeUsageAbsolute();
			break;
		case '9':
			// done in gui
			Presets.setMovingRuleColouredAbsolute();
			break;
		case '0':
			// done in gui
			RevVisGDX.singleton.currentCircuit.setAllDefault();
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
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		float mouseAtWidth = (float)oldX / Gdx.graphics.getWidth();
		float mouseAtHeight = (float)(Gdx.graphics.getHeight() - oldY) / Gdx.graphics.getHeight();
		
		Rectangle current = RevVisGDX.singleton.currentCircuit.getViewBounds();
		
		float mouseToLeftOriginal = current.width * mouseAtWidth;
		float mouseToBottomOriginal = current.height * mouseAtHeight;
		
		float zoomFactor = (1 + (amount * 0.2f));
		current.height *= zoomFactor;
		current.width *= zoomFactor;
		
		float expectedMouseToLeft = current.width * mouseAtWidth;
		float mouseDiffX = mouseToLeftOriginal - expectedMouseToLeft;
		current.x += mouseDiffX;
		
		float expectedMouseToBottom = current.height * mouseAtHeight;
		float mouseDiffY = mouseToBottomOriginal - expectedMouseToBottom;
		current.y += mouseDiffY;
		
		RevVisGDX.singleton.currentCircuit.setViewBounds(current);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		oldX = screenX;
		oldY = screenY;
		RevVisGDX.singleton.currentCircuit.highlightAt(screenX, screenY);
		//RevVisGDX.singleton.menu.MouseCoords(screenX, screenY);
		return false;
	}
}
