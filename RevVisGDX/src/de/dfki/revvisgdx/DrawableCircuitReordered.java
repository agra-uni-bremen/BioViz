package de.dfki.revvisgdx;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.dfki.revlibReader.ReversibleCircuit;
import de.dfki.revlibReader.ToffoliGate;

public class DrawableCircuitReordered extends DrawableCircuit {
	
//	private Vector<ReorderInfo> reorders = new Vector<ReorderInfo>();
	private HashMap<String, Integer> shiftedIndices;
	private HashMap<Integer, Integer> shiftedGateCoords;
	
	DrawableSprite lineStart, lineEnd;
	
	boolean inputsFromStart, functionsToEnd;
	boolean drawReordered = false;

	public DrawableCircuitReordered(ReversibleCircuit toDraw) {
		super(toDraw);
		
		lineStart = new DrawableSprite("data/lineStart.png");
		lineEnd = new DrawableSprite("data/lineEnd.png");
	}
	
	@Override
	protected float getLineYScreenCoord(String line) {
		if (drawReordered) {
			int indexOfLine = this.getShiftedLineCoords(line);
			float y = (indexOfLine - (data.getAmountOfVars() / 2)) - smoothOffsetY; //+ RevVisGDX.singleton.camera.viewportHeight;
			y *= smoothScaleY;
			return y;
		} else {
			return super.getLineYScreenCoord(line);
		}
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
		if (this.data.isFunctionLine(line))
			return this.data.getAmountOfVars();
		else
			return data.getCoordOfGate(this.data.getLastGateOnLine(line));
	}
	
	@Override
	protected void drawLine(int indexOfVariable) {
		if (drawReordered) {
			int firstGateCoord = data.getCoordOfGate(data.getFirstGateOnLine(data.getVars().get(indexOfVariable)));
			int lastGateCoord = data.getCoordOfGate(data.getLastGateOnLine(data.getVars().get(indexOfVariable)));
			
			drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, true);
			drawVariableNameOverlay(indexOfVariable);
		} else {
			super.drawLine(indexOfVariable);
		}
	}
	
	@Override
	protected void drawLineSegment(int indexOfVariable, int firstGateCoord, int lastGateCoord, boolean currentlyUsed, Color additionalMultiplier) {
		super.drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, currentlyUsed, additionalMultiplier);
		if (drawReordered) {
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
	}
	
	@Override
	protected void drawVariableNameOverlay(int indexOfVariable) {
		if (drawReordered) {
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
		} else {
			RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(indexOfVariable).hashCode());
			RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(indexOfVariable).hashCode() + Integer.MAX_VALUE / 2);
			super.drawVariableNameOverlay(indexOfVariable);
		}
	}

	@Override
	protected float xCoordOnScreen(int i) {
		if (shiftedGateCoords == null) {
			recalculateGateShift();
		}
		if (shiftedGateCoords.containsKey(i)) {
			return xCoordOnScreen((float)shiftedGateCoords.get(i));
		} else {
			return xCoordOnScreen((float)i);
		}
	}
	
	private void recalculateGateShift() {
		shiftedGateCoords = new HashMap<Integer, Integer>();
		int[] maximumCoord = new int[this.data.getAmountOfVars()];
		for (int i = 0; i < this.data.getGates().size(); i++) {
			int maxCoord = Integer.MIN_VALUE;
			int minCoord = Integer.MAX_VALUE;
			
			ToffoliGate current = this.data.getGate(i);
			for (int j = 0; j < current.getInputs().size(); j++) {
				String line = current.getInputs().get(j);
				int coord = this.getShiftedLineCoords(line);
				if (coord < minCoord)
					minCoord = coord;
				if (coord > maxCoord)
					maxCoord = coord;
			}
			String line = current.output;
			int coord = this.getShiftedLineCoords(line);
			if (coord < minCoord)
				minCoord = coord;
			if (coord > maxCoord)
				maxCoord = coord;
			
			int furthestCoord = 0;
			for (int j = minCoord; j <= maxCoord; j++) {
				if (maximumCoord[j] > furthestCoord)
					furthestCoord = maximumCoord[j];
			}
			
			int resultingCoord = furthestCoord + 1;
			for (int j = minCoord; j <= maxCoord; j++) {
				maximumCoord[j] = resultingCoord;
			}
			
			this.shiftedGateCoords.put(i, resultingCoord);
		}
	}
}
