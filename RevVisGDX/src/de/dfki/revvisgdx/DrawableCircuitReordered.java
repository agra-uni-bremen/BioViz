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
			
			drawArc(left, y + smoothScaleY, 1f, 270, 180, true, true);
			
			line.x = right;
			line.y = y - smoothScaleY / 4f;
			line.scaleX = 1;
			line.scaleY = smoothScaleY / 2f;
			
			drawArc(right, y + smoothScaleY, 1f, 270, 360, true, true);
		}
	}
	
	private void printVisibilityStatus() {
		for (int i = 0; i < this.data.getVars().size(); i++) {
			String line = this.data.getVars().get(i);
			System.out.println("Var " + line + " visible from " + data.getCoordOfGate(this.data.getFirstGateOnLine(line)) + " to " +data.getCoordOfGate(this.data.getLastGateOnLine(line)) + ", placed at " + getLineYScreenCoord(line));
		}
	}
	
	private void drawArc(float centerX, float centerY, float radius, float from, float to) {
		drawArc(centerX, centerY, radius, from, to, false);
	}
	private void drawArc(float centerX, float centerY, float radius, float from, float to, boolean fadeCol) {
		drawArc(centerX, centerY, radius, from, to, fadeCol, false);
	}
	
	private void drawArc(float centerX, float centerY, float radius, float from, float to, boolean fadeCol, boolean getWider) {
		// TODO this should of course depend on the camera zoom value
		int steps = Math.max(2, (int)(smoothScaleX / 8f));
		
		float fromRad = (float)(from * (Math.PI / 180));
		float toRad = (float)(to * (Math.PI / 180));
		
		Color col = line.color.cpy();
		Color oldCol = col.cpy();
		float alphaBase = col.a;
		
		for (int i = 0; i < steps; i++) {
			// Calculate points on unit circle: from
			float x1 = (float)(Math.cos(fromRad + ((toRad - fromRad) * ((float)i / steps))));
			float y1 = (float)(Math.sin(fromRad + ((toRad - fromRad) * ((float)i / steps))));
			// and to.
			float x2 = (float)(Math.cos(fromRad + ((toRad - fromRad) * ((i + 1f) / steps))));
			float y2 = (float)(Math.sin(fromRad + ((toRad - fromRad) * ((i + 1f) / steps))));
			
			// scale points away from center according to the given radius and the camera zoom factor
			float fromX = centerX + (x1 * radius * smoothScaleX);
			float fromY = centerY + (y1 * radius * smoothScaleY);
			
			float targetX = centerX + (x2 * radius * smoothScaleX);
			float targetY = centerY + (y2 * radius * smoothScaleY);
			
			// Just some beautification
			float scale = (float)(Math.sqrt((fromX-targetX)*(fromX-targetX) + (fromY-targetY)*(fromY-targetY)));
			if (!getWider)
				line.scaleY = 1;
			else
				line.scaleY = 1 + (((float)i / steps) * smoothScaleX) / 8f;
			
			// actually paint the resulting line
			line.scaleX = scale;
			line.x = fromX;
			line.y = fromY;
			line.rotation = 90 + from + ((to - from) * ((float)i / steps));
			col.a = alphaBase * (1f - ((float)i / steps));
			line.color = col;
			line.draw();
			line.rotation = 0;
		}
		line.color = oldCol;
	}

}
