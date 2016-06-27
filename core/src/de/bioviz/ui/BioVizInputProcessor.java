package de.bioviz.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BioVizInputProcessor implements InputProcessor {

	/**
	 * The internal logger.
	 */
	static Logger logger =
			LoggerFactory.getLogger(BioVizInputProcessor.class);


	boolean isMoving = false;

	boolean multiTouchZoom = false;

	BioViz parentViz;

	private int oldX;
	private int oldY;
	private int oldX2;
	private int oldY2;
	private int oldDistanceX;
	private int oldDistanceY;

	/**
	 * Whether ctrl was pushed.
	 */
	private boolean ctrl;

	/**
	 * Whether shift was pushed.
	 */
	private boolean shift;

	/**
	 * Whether alt was pushed.
	 */
	private boolean alt;

	public BioVizInputProcessor(final BioViz parentVisualization) {
		this.parentViz = parentVisualization;
	}

	@Override
	public boolean keyDown(final int keycode) {
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
	public boolean keyUp(final int keycode) {
		if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrl = false;
		}
		if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT) {
			alt = false;
		}
		if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shift = false;
		} else if ((keycode == Keys.RIGHT || keycode == Keys.UP)
				   && !ctrl && !alt) {
			parentViz.currentCircuit.nextStep();
		} else if ((keycode == Keys.LEFT || keycode == Keys.DOWN)
				   && !ctrl && !alt) {
			parentViz.currentCircuit.prevStep();
		} else if ((keycode == Keys.PAGE_UP && ctrl) ||
				   (keycode == Keys.LEFT && alt)) {
			parentViz.callPreviousTabListeners();
		} else if (keycode == Keys.PAGE_DOWN && ctrl ||
				   (keycode == Keys.RIGHT && alt)) {
			parentViz.callNextTabListeners();
		} else if (keycode == Keys.PLUS && ctrl) {
			parentViz.messageCenter.incScales();
		} else if (keycode == Keys.MINUS && ctrl) {
			parentViz.messageCenter.decScales();
		} else if (keycode == Keys.W && ctrl) {
			parentViz.callCloseFileListeners();
		} else if (keycode == Keys.O && ctrl) {
			parentViz.callLoadFileListeners();
		} else if (keycode == Keys.R && ctrl) {
 				parentViz.callReloadFileListeners();
		}
		else if (keycode == Keys.S) {
			if (ctrl) {
				parentViz.callSaveFileListeners();
			} else {
				parentViz.currentCircuit.shrinkToSquareAlignment();
			}
		} else if (keycode == Keys.T && ctrl) {
			parentViz.callLoadFileListeners();
		} else if (keycode == Keys.Z) {
			if (shift) {
				parentViz.currentCircuit.zoomExtents();
			} else {
				parentViz.currentCircuit.zoomTo1Px();
			}
		}

		toggleOptions(keycode);

		return false;
	}

	void toggleOptions(final int keycode) {
		BDisplayOptions.findOption(keycode, ctrl, shift, alt).ifPresent(
				it -> parentViz.currentCircuit.getDisplayOptions().toggleOption
						(it));
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int x, final int y, final int pointer,
							 final int button) {
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
	public boolean touchUp(final int x, final int y, final int pointer, final
	int button) {
		if (pointer == 0) {
			isMoving = false;
		} else if (pointer == 1) {
			isMoving = true;
			multiTouchZoom = false;
		}

		for (final DrawableDroplet d :
				parentViz.currentCircuit.getDroplets()) {
			if (d.isHovered()) {
				if (button == Buttons.LEFT) {
					d.toggleGridVisibility();
				} else if (button == Buttons.RIGHT) {
					parentViz.selectedDroplet = d;
					parentViz.callPickColourListeners();
				}
			}
		}

		logger.trace("TouchUp at " + x + "/" + y + " with " + pointer + "/" +
					 button);

		return false;
	}

	@Override
	public boolean touchDragged(final int x, final int y, final int pointer) {
		if (isMoving) {
			parentViz.currentCircuit.setOffsetX(
					parentViz.currentCircuit.getOffsetX() +
					(x - oldX) / parentViz.currentCircuit.getScaleX());
			parentViz.currentCircuit.setOffsetY(
					parentViz.currentCircuit.getOffsetY() -
					(y - oldY) / parentViz.currentCircuit.getScaleY());
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
					float zoomFactorX = (float) zoomX / Math.abs(oldX - oldX2);
					parentViz.currentCircuit.setScaleX(
							parentViz.currentCircuit.getScaleX()
							* Math.max(1 - zoomFactorX, 0.01f));
				}
				if (oldY - oldY2 != 0) {
					float zoomFactorY = (float) zoomY / Math.abs(oldY - oldY2);
					parentViz.currentCircuit.setScaleY(
							parentViz.currentCircuit.getScaleY()
							* Math.max(1 - zoomFactorY, 0.01f));
				}
			}
		}
		return false;
	}

	@Override
	public boolean scrolled(final int amount) {
		float mouseAtWidth = (float) oldX / Gdx.graphics.getWidth();
		float mouseAtHeight = (float) oldY / Gdx.graphics.getHeight();

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
	public boolean mouseMoved(final int screenX, final int screenY) {
		oldX = screenX;
		oldY = screenY;
		return false;
	}
}
