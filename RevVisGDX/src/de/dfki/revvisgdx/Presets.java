package de.dfki.revvisgdx;

import de.dfki.revvisgdx.DrawableCircuit.lineWidth;

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
}
