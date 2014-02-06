package de.dfki.revvisgdx;

import de.dfki.revvisgdx.DrawableCircuit.lineWidth;
import de.dfki.revvisgdx.DrawableCircuit.movingRuleDisplay;

public class Presets {
	public static void setConstGarbage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.colorizeConstants = true;
		dc.colorizeGarbageLine = true;
		dc.hideGates = true;
		dc.lineType = lineWidth.full;
		dc.drawLinesDarkWhenUsed = false;
	}
	
	public static void setBoxesAndUsage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.lineType = lineWidth.full;
		dc.drawVerticalLines = false;
		dc.drawLinesDarkWhenUsed = true;
		dc.hideGates = false;
		dc.colorizeConstants = false;
		dc.colorizeGarbageLine = false;
	}
	
	public static void setColourizedUsage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.drawLinesColourizedWhenUsed = true;
	}
	
	public static void setColourizeLineType() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.markVariableTypes = true;
		dc.lineType = lineWidth.full;
	}
	
	public static void setMovingRuleColoured() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.colourizeGatesByMobility = movingRuleDisplay.total;
		dc.drawVerticalLines = false;
		dc.lineType = lineWidth.hidden;
	}
}
