package de.bioviz.ui;

import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
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
	
	BioViz parentViz;
	
	public BioVizInputProcessor(BioViz parentVisualization) {
		this.parentViz = parentVisualization;
	}
	
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
		}
		else if (keycode == Keys.RIGHT || keycode == Keys.UP) {
			parentViz.currentCircuit.nextStep();
		}
		else if (keycode == Keys.LEFT || keycode == Keys.DOWN) {
			parentViz.currentCircuit.prevStep();
		}
		else if (keycode == Keys.PLUS && ctrl) {
			parentViz.messageCenter.incScales();
		}
		else if (keycode == Keys.MINUS && ctrl) {
			parentViz.messageCenter.decScales();
		}
		else if (keycode == Keys.A) {
			if (ctrl) {
				parentViz.currentCircuit.displayOptions.toggleOption(
						BDisplayOptions.Adjacency);
			}
			if (shift) {
				parentViz.currentCircuit.displayOptions.toggleOption(
						BDisplayOptions.DetectorIcon);
			}

		}
		else if (keycode == Keys.C) {
			if (ctrl) {
				parentViz.currentCircuit.displayOptions.toggleOption(BDisplayOptions.Coordinates);
			}
		}
		else if (keycode == Keys.D) {
			if (ctrl) {
				parentViz.currentCircuit.displayOptions.toggleOption(
						BDisplayOptions.DispenserID);
			}
			if (shift) {
				parentViz.currentCircuit.displayOptions.toggleOption(
						BDisplayOptions.DispenserIcon);
			}
		}
		else if (keycode == Keys.W) {
			if (ctrl) {
				parentViz.callCloseFileListeners();
			}
		}
		else if (keycode == Keys.O) {
			if (ctrl) {
				parentViz.callLoadFileListeners();
			}
		}
		else if (keycode == Keys.R) {
			parentViz.currentCircuit.displayOptions.toggleOption(BDisplayOptions.ColorfulRoutes);
		}
		else if (keycode == Keys.S) {
			if (ctrl) {
				parentViz.callSaveFileListeners();
			}
			else if (shift) {
				parentViz.currentCircuit.displayOptions.toggleOption(
						BDisplayOptions.SinkIcon);
			}
			else {
				parentViz.currentCircuit.shrinkToSquareAlignment();
			}
		}
		else if (keycode == Keys.Z)  {
			if (shift) {
				parentViz.currentCircuit.zoomExtents();
			}
			else {
				parentViz.currentCircuit.zoomTo1Px();
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
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
		
		for(DrawableDroplet d: parentViz.currentCircuit.droplets) {
			if (d.isHovered()) {
				if (button == Buttons.LEFT) {
					d.toggleGridVisibility();
				} else if (button == Buttons.RIGHT) {
					parentViz.selectedDroplet = d;
					parentViz.callPickColourListeners();
				}
			}
		}
		
		logger.trace("TouchUp at " + x + "/" + y + " with " + pointer + "/" + button);
		
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		if (isMoving) {
			parentViz.currentCircuit.offsetX += (x - oldX) / parentViz.currentCircuit.getScaleX();
			parentViz.currentCircuit.offsetY -= (y - oldY) / parentViz.currentCircuit.getScaleY();
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
					parentViz.currentCircuit.setScaleX(parentViz.currentCircuit.getScaleX()
							* Math.max(1 - (zoomFactorX), 0.01f));
				}
				if (oldY - oldY2 != 0) {
					float zoomFactorY = (float)zoomY / Math.abs(oldY - oldY2);
					parentViz.currentCircuit.setScaleY(parentViz.currentCircuit.getScaleY()
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
		
		Rectangle current = parentViz.currentCircuit.getViewBounds();
		
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
		
		parentViz.currentCircuit.setViewBounds(current);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		oldX = screenX;
		oldY = screenY;
		return false;
	}
}
