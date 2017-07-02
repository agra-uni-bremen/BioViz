/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BioVizInputProcessor implements InputProcessor {


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

	BioVizInputProcessor(final BioViz parentVisualization) {
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
			parentViz.currentAssay.nextStep();
		} else if ((keycode == Keys.LEFT || keycode == Keys.DOWN)
				   && !ctrl && !alt) {
			parentViz.currentAssay.prevStep();
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
				parentViz.currentAssay.shrinkToSquareAlignment();
			}
		} else if (keycode == Keys.T && ctrl) {
			parentViz.callLoadFileListeners();
		} else if (keycode == Keys.Z) {
			if (shift) {
				parentViz.currentAssay.zoomExtents();
			} else {
				parentViz.currentAssay.zoomTo1Px();
			}
		}

		toggleOptions(keycode);

		return false;
	}

	void toggleOptions(final int keycode) {
		BDisplayOptions.findOption(keycode, ctrl, shift, alt).ifPresent(
				it -> parentViz
						.currentAssay
						.getDisplayOptions().toggleOption(it));
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
				parentViz.currentAssay.getDroplets()) {
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

		final float minimalZoomFactor = 0.01f;

		if (isMoving) {
			parentViz.currentAssay.setOffsetX(
					parentViz.currentAssay.getOffsetX() +
					(x - oldX) / parentViz.currentAssay.getScaleX());
			parentViz.currentAssay.setOffsetY(
					parentViz.currentAssay.getOffsetY() -
					(y - oldY) / parentViz.currentAssay.getScaleY());
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
					parentViz.currentAssay.setScaleX(
							parentViz.currentAssay.getScaleX()
							* Math.max(1 - zoomFactorX, minimalZoomFactor));
				}
				if (oldY - oldY2 != 0) {
					float zoomFactorY = (float) zoomY / Math.abs(oldY - oldY2);
					parentViz.currentAssay.setScaleY(
							parentViz.currentAssay.getScaleY()
							* Math.max(1 - zoomFactorY, minimalZoomFactor));
				}
			}
		}
		return false;
	}

	@Override
	// TODO @Jannis comment this ^^
	public boolean scrolled(final int amount) {
		float mouseAtWidth = (float) oldX / Gdx.graphics.getWidth();
		float mouseAtHeight = (float) oldY / Gdx.graphics.getHeight();

		Rectangle current = parentViz.currentAssay.getViewBounds();

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

		parentViz.currentAssay.setViewBounds(current);
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		oldX = screenX;
		oldY = screenY;
		return false;
	}
}
