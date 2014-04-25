package de.dfki.revvisgdx;

import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;

import de.dfki.revlibReader.ReversibleCircuit;
import de.dfki.revvisgdx.DrawableCircuit.lineWidth;

public class DrawableCircuitReordered extends DrawableCircuit {
	
//	private Vector<ReorderInfo> reorders = new Vector<ReorderInfo>();
	private HashMap<String, Integer> shiftedIndices;

	public DrawableCircuitReordered(ReversibleCircuit toDraw) {
		super(toDraw);
	}
	
	@Override
	protected float getLineYScreenCoord(String line) {
		int indexOfLine = this.getShiftedLineCoords(line);
		float y = (indexOfLine - (data.getAmountOfVars() / 2)) - offsetY; //+ RevVisGDX.singleton.camera.viewportHeight;
		y *= smoothScaleY;
		System.out.println("gained " + (this.data.getVars().indexOf(line) - indexOfLine) + " for " + line);
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
	
	private boolean isVisibleAt(String line, int index) {
		int result = 0;
		int start = lineVisibleFrom(line);
		int end = lineVisibleTo(line);
		return (index >= start && index <= end);
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

		Color multiplier;
		
		drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, true);
		
		if (showLineNames && smoothScaleY > 10) {
			Color lineNameColor = new Color(Color.WHITE);
			lineNameColor.a = Math.max(0, Math.min(1, (smoothScaleY - 10f) / 5f));
			RevVisGDX.singleton.mc.addHUDMessage(data.getVars().get(indexOfVariable).hashCode(), data.getVars().get(indexOfVariable), RevVisGDX.singleton.camera.viewportWidth - 64, (line.y + RevVisGDX.singleton.camera.viewportHeight / 2f), lineNameColor);
		} else {
			RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(indexOfVariable).hashCode());
		}
	}
	
	@Override
	protected void drawLineSegment(int indexOfVariable, int firstGateCoord, int lastGateCoord, boolean currentlyUsed, Color additionalMultiplier) {
		super.drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, currentlyUsed, additionalMultiplier);
		
		float left = xCoordOnScreen(firstGateCoord);
		float right = xCoordOnScreen(lastGateCoord);
		float y = getLineYScreenCoord(data.getVars().get(indexOfVariable));
		
		if (this.lineType != lineWidth.hidden) {
			line.x = left;
			line.y = y + smoothScaleY / 4f;
			line.scaleX = 1;
			line.scaleY = smoothScaleY / 2f;
			line.color = Color.GREEN;
			line.draw();
			
			line.x = right;
			line.y = y - smoothScaleY / 4f;
			line.scaleX = 1;
			line.scaleY = smoothScaleY / 2f;
			line.color = Color.RED;
			line.draw();
		}
	}
	
	private void printVisibilityStatus() {
		for (int i = 0; i < this.data.getVars().size(); i++) {
			String line = this.data.getVars().get(i);
			System.out.println("Var " + line + " visible from " + data.getCoordOfGate(this.data.getFirstGateOnLine(line)) + " to " +data.getCoordOfGate(this.data.getLastGateOnLine(line)) + ", placed at " + getLineYScreenCoord(line));
		}
	}

}
