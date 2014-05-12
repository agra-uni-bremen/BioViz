package de.dfki.revvisgdx;

import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;

import de.dfki.revlibReader.ReversibleCircuit;
import de.dfki.revvisgdx.DrawableCircuit.lineWidth;

public class DrawableCircuitReordered extends DrawableCircuit {
	
//	private Vector<ReorderInfo> reorders = new Vector<ReorderInfo>();
	private HashMap<String, Integer> shiftedIndices;
	
	DrawableSprite lineStart, lineEnd;

	public DrawableCircuitReordered(ReversibleCircuit toDraw) {
		super(toDraw);
		
		lineStart = new DrawableSprite("data/lineStart.png");
		lineEnd = new DrawableSprite("data/lineEnd.png");
	}
	
	@Override
	protected float getLineYScreenCoord(String line) {
		int indexOfLine = this.getShiftedLineCoords(line);
		float y = (indexOfLine - (data.getAmountOfVars() / 2)) - offsetY; //+ RevVisGDX.singleton.camera.viewportHeight;
		y *= smoothScaleY;
		return y;
	}
	
	private void calculateShiftedLineCoords() {
		for (int i = 0; i < this.data.getVars().size(); i++) {
			int newCoord = firstFreeIndex(this.data.getVars().get(i));
			this.shiftedIndices.put(this.data.getVars().get(i), newCoord);
		}
	}
	
	private int firstFreeIndex(String line) {
		int originalIndex = this.data.getVars().indexOf(line);
		for (int i = 0; i < originalIndex; i++) {
			boolean isFree = true;
			for (String otherLine : shiftedIndices.keySet()) {
				if (shiftedIndices.get(otherLine) == i) {
					if (lineVisibleFrom(line) <= lineVisibleTo(otherLine) && lineVisibleTo(line) >= lineVisibleFrom(otherLine)) {
						isFree = false;
						break;
					}
				}
			}
			if (isFree)
				return i;
		}
		return originalIndex;
	}
	
	protected int getShiftedLineCoords(String line) {
		if (shiftedIndices == null) {
			 shiftedIndices = new HashMap<String, Integer>();
			 calculateShiftedLineCoords();
		}
		return shiftedIndices.get(line);
		
	}
	
	protected int lineVisibleFrom(String line) {
		return data.getCoordOfGate(this.data.getFirstGateOnLine(line));
	}
	
	protected int lineVisibleTo(String line) {
		return data.getCoordOfGate(this.data.getLastGateOnLine(line));
	}
	
	@Override
	protected void drawLine(int indexOfVariable) {
		int firstGateCoord = data.getCoordOfGate(data.getFirstGateOnLine(data.getVars().get(indexOfVariable)));
		int lastGateCoord = data.getCoordOfGate(data.getLastGateOnLine(data.getVars().get(indexOfVariable)));
		
		drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, true);
		drawVariableNameOverlay(indexOfVariable);
	}
	
	@Override
	protected void drawLineSegment(int indexOfVariable, int firstGateCoord, int lastGateCoord, boolean currentlyUsed, Color additionalMultiplier) {
		super.drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, currentlyUsed, additionalMultiplier);
		
		float y = getLineYScreenCoord(data.getVars().get(indexOfVariable));
		
		float maxDim = Math.min(smoothScaleX, smoothScaleY);
		lineStart.x = xCoordOnScreen(firstGateCoord - 0.5f);
		lineStart.y = y;
		lineStart.setDimensions(maxDim, maxDim);
		lineStart.draw();
		
		lineEnd.x = xCoordOnScreen(lastGateCoord + 0.5f);
		lineEnd.y = y;
		lineEnd.setDimensions(maxDim, maxDim);
		lineEnd.draw();
	}
	
	@Override
	protected void drawVariableNameOverlay(int indexOfVariable) {
		if (showLineNames && smoothScaleY > 10) {
			Color lineNameColor = new Color(Color.GREEN);
			lineNameColor.a = Math.max(0, Math.min(1, (smoothScaleY - 10f) / 5f));
			RevVisGDX.singleton.mc.addHUDMessage(
					data.getVars().get(indexOfVariable).hashCode(),
					data.getVars().get(indexOfVariable),
					xCoordOnScreen(this.getFirstGateCoord(indexOfVariable)) + Gdx.graphics.getWidth() / 2f,
					(line.y + RevVisGDX.singleton.camera.viewportHeight / 2f) + smoothScaleY / 1.5f,
					lineNameColor);
			
			lineNameColor = new Color(Color.RED);
			lineNameColor.a = Math.max(0, Math.min(1, (smoothScaleY - 10f) / 5f));
			RevVisGDX.singleton.mc.addHUDMessage(
					data.getVars().get(indexOfVariable).hashCode() + Integer.MAX_VALUE / 2,
					data.getVars().get(indexOfVariable),
					xCoordOnScreen(this.getLastGateCoord(indexOfVariable)) + Gdx.graphics.getWidth() / 2f,
					(line.y + RevVisGDX.singleton.camera.viewportHeight / 2f) + smoothScaleY / 1.5f,
					lineNameColor);
		} else {
			RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(indexOfVariable).hashCode());
			RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(indexOfVariable).hashCode() + Integer.MAX_VALUE / 2);
		}
	}

}
