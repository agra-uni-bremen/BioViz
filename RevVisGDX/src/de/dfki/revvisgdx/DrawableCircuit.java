package de.dfki.revvisgdx;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.dfki.revlibReader.ReversibleCircuit;

/**
 * The DrawableCircuit class provides methods to draw a given ReversibleCircuit.
 * Create a ReversibleCircuit first (e.g. by loading a given .real file), then
 * create a DrawableCircuit instance for the ReversibleCircuit to draw the latter.
 * @author jannis
 *
 */
public class DrawableCircuit implements Drawable {
	ReversibleCircuit data;

	DrawableSprite line;
	DrawableSprite gate01;
	DrawableSprite gate02;

	private float scaleX = 1;
	public float offsetX = 0;
	private float scaleY = 1;
	public float offsetY = 0;
	
	protected float smoothScaleX = 1;
	protected float smoothScaleY = 1;
	protected float smoothOffsetX = 0;
	protected float smoothOffsetY = 0;
	
	private float scalingDelay = 4f;

	public boolean colorizeGarbageLine = false;
	public boolean hideGates = false;
	public boolean countGatesForGroupColor = false;
	public boolean colorizeConstants = false;
	public boolean drawVerticalLines = true;
	public boolean drawLinesDarkWhenUsed = false;
	public float reduceGatesToBlocksWhenSmallerThanPixels = 8f;
	public float groupColorAmount = 16f;
	public movingRuleDisplay colourizeGatesByMobility = movingRuleDisplay.none;
	public boolean drawAccumulatedMovingRule = false;
	public boolean highlightHoveredGate = false;
	public movingRuleHighlight highlightHoveredGateMovingRule = movingRuleHighlight.none;
	public boolean colorizeLineUsage = false;
	public boolean showLineNames = true;
	public lineWidth lineType = lineWidth.pixelWide;
	public lineGrouping neighbourhoodGrouping = lineGrouping.none;
	private String drawnBus = "";
	public boolean drawSubCircuits = true;
	public boolean markVariableTypes = false;
	public drawLinesColourizedByUsageType drawLinesColourizedWhenUsed = drawLinesColourizedByUsageType.none;
	public gateElementDisplay gateDisplay = gateElementDisplay.dynamic;
	public Color lineBaseColor = new Color(Color.BLACK);
	
	private int highlitGate = 0;
	
	public enum lineWidth {hidden, pixelWide, usageWide, full}
	public enum lineGrouping {none, single, singleGreyscale, bus}
	public enum movingRuleDisplay{none, leftRight, total, totalAbsolute}
	public enum movingRuleHighlight{none, whiteBars, boxes}
	public enum gateElementDisplay{alwaysDetailed, dynamic, alwaysBoxy}
	public enum drawLinesColourizedByUsageType{none, relative, absolute}
	
	private float boxOverlayX;
	private float boxOverlayWidth;

	/**
	 * Creates a drawable entity based on the data given.
	 * @param toDraw the data to draw
	 */
	public DrawableCircuit(ReversibleCircuit toDraw) {
		this.data = toDraw;
		line = new DrawableSprite("data/BlackPixel.png");
		gate01 = new DrawableSprite("data/tgate01.png");
		gate02 = new DrawableSprite("data/tgate02.png");
	}

	@Override
	public void draw() {		
		smoothScaleX += (getScaleX() - smoothScaleX) / scalingDelay;
		smoothScaleY += (getScaleY() - smoothScaleY) / scalingDelay;
		smoothOffsetX += (offsetX - smoothOffsetX) / scalingDelay;
		smoothOffsetY += (offsetY - smoothOffsetY) / scalingDelay;
		
		drawVariables();

		drawGates();
		
		drawOverlay();
	}

	/**
	 * Draws additional overlaid information (currently only the moving rule overlay)
	 */
	private void drawOverlay() {
		if (drawAccumulatedMovingRule) {
			int[] movingRuleTargets = this.data.getMovingRuleAccumulations();
			
			for (int j = 0; j < movingRuleTargets.length; j++) {
				
				float xCoord = (xCoordOnScreen(j) + xCoordOnScreen(j - 1)) / 2f;
				
				line.color = new Color(Color.WHITE);
				line.color.a = (float)(Math.log(movingRuleTargets[j]) / Math.log((float)this.data.getMaximumMovingRuleTargetValue()));
				line.scaleX = 1; //RevVisGDX.singleton.camera.viewportWidth;
				line.scaleY = RevVisGDX.singleton.camera.viewportHeight;
				line.x = xCoord;
				line.y = 0; //+ RevVisGDX.singleton.camera.viewportHeight;
				line.draw();
			}
		}
		
		if (drawSubCircuits) {
			Vector<ReversibleCircuit.subCircuitDimensions> subs = this.data.getSubCircuits();
			
			for (int j = 0; j < subs.size(); j++) {
				float left = xCoordOnScreen(subs.get(j).startAt);
				float right = xCoordOnScreen(subs.get(j).endAt);
				int col = subs.get(j).name.hashCode();
				
//				float xCoord = (xCoordOnScreen(j) + xCoordOnScreen(j - 1)) / 2f;
				
				line.color = new Color(col);
				if (line.color.r < 0.5f)
					line.color.r += 0.5f;
				if (line.color.g < 0.5f)
					line.color.g += 0.5f;
				if (line.color.b < 0.5f)
					line.color.b += 0.5f;
				line.color.a = 1;
				
				line.scaleX = right - left; //RevVisGDX.singleton.camera.viewportWidth;
				line.scaleY = Math.min(32, 20 * smoothScaleY);
				line.x = (right + left) / 2f;
				line.y = (data.getAmountOfVars() / 2 + 10f) - offsetY;
				line.y *= smoothScaleY;
				line.y = Math.min(line.y, RevVisGDX.singleton.camera.viewportHeight / 2f - 24);
				line.draw();
				
				Color lineNameColor = new Color(line.color).mul(0.5f);
				Vector3 lineCoord = new Vector3(left, line.y + 10, 1);
				float fontWidth = RevVisGDX.singleton.mc.getFont().getBounds(subs.get(j).name).width;
				if (fontWidth > right - left)
					lineNameColor.a = 0;
				else if (fontWidth > right - left - 50)
					lineNameColor.a = ((right - left) - fontWidth) / 50f;
				else
					lineNameColor.a = 1;
				RevVisGDX.singleton.camera.project(lineCoord);
				RevVisGDX.singleton.mc.addHUDMessage(subs.get(j).hashCode(), subs.get(j).name, lineCoord.x, lineCoord.y, lineNameColor);
			}
		}
		
		if (highlightHoveredGateMovingRule == movingRuleHighlight.boxes) {
			line.color = new Color(0.25f, 0.25f, 0.25f, 0.5f);
			line.x = boxOverlayX;
			line.y = 0;
			line.scaleX = boxOverlayWidth;
			line.scaleY = RevVisGDX.singleton.camera.viewportHeight;
			line.draw();
		}
	}

	/**
	 * Draws the gates
	 */
	private void drawGates() {
		if (!hideGates) {
			float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE, minX = xCoordOnScreen(0) - 0.5f * smoothScaleX, maxX = minX + 0.5f*smoothScaleX;
			float currentHue = 0;
			float currentSaturation = 1;
			String currentGroup = data.getGates().iterator().next().output;
			int groupCount = 0;
			
			/**
			 * Iterate through all gates and draw each of them.
			 */
			for (int i = 0; i < data.getGates().size(); i++) {
				float xCoord = xCoordOnScreen(i);

				/**
				 * Drawing grouped gates is totally different, so this is the topmost check.
				 * There's not much overlap between those two cases.
				 * If lines aren't drawn grouped, this first block will be called.
				 */
				if (this.neighbourhoodGrouping == lineGrouping.none) {
					if (xCoord > -RevVisGDX.singleton.camera.viewportWidth / 2 - smoothScaleX && xCoord < RevVisGDX.singleton.camera.viewportWidth / 2 + smoothScaleX) {

						float maxDim = Math.min(smoothScaleX, smoothScaleY);

						Color gateColor = new Color(Color.BLACK);
						if(this.colourizeGatesByMobility == movingRuleDisplay.leftRight) {
							int leftRange = data.calculateGateMobilityLeft(i);
							int rightRange = data.calculateGateMobilityRight(i);
							gateColor.r = Math.min(1, leftRange / (float)data.calculateMaximumMobility());
							gateColor.g = Math.min(1, rightRange / (float)data.calculateMaximumMobility());
						} else if (this.colourizeGatesByMobility == movingRuleDisplay.total) {
							int minMobility = data.calculateMinimumMobilityTotal();
							int maxMobility = data.calculateMaximumMobilityTotal();
							int gateMobility = data.calculateGateMobilityLeft(i) + data.calculateGateMobilityRight(i);
							if (gateMobility - minMobility < (maxMobility - minMobility) / 2) {
								gateColor.r = 1f;
								gateColor.g = (gateMobility - minMobility) / ((maxMobility - minMobility) / 2f);
							} else {
								gateColor.g = 1f;
								gateColor.r = 1 - (((gateMobility - minMobility) - ((maxMobility - minMobility) / 2f)) / ((maxMobility - minMobility) / 2f));
							}
						} else if (this.colourizeGatesByMobility == movingRuleDisplay.totalAbsolute) {
							int gateMobility = data.calculateGateMobilityLeft(i) + data.calculateGateMobilityRight(i);
							int max = data.getGates().size();
							if (gateMobility < max / 2) {
								gateColor.r = 1f;
								gateColor.g = gateMobility / (max / 2f);
							} else {
								gateColor.g = 1f;
								gateColor.r = 1 - ((gateMobility - (max / 2f)) / (max/2f));
							}
						}
						
						if (i == highlitGate && highlightHoveredGate) {
							gateColor.add(new Color(0.25f, 0.25f, 1f, 0f));
						}

						if (drawVerticalLines) {
							minY = getLineYScreenCoord(data.getGate(i).output);
							maxY = minY;

							for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
								minY = Math.min(minY, getLineYScreenCoord(data.getGate(i).getInputs().get(j)));
								maxY = Math.max(maxY, getLineYScreenCoord(data.getGate(i).getInputs().get(j)));
							}
															
							line.color = gateColor;
							line.scaleX = 1; //RevVisGDX.singleton.camera.viewportWidth;
							line.scaleY = maxY - minY;
							line.x = xCoord;
							line.y = (maxY + minY) / 2; //+ RevVisGDX.singleton.camera.viewportHeight;
							line.draw();
						}
						
						if (highlightHoveredGateMovingRule != movingRuleHighlight.none &&
								(i == highlitGate ||
									(highlitGate >= this.data.getGates().size() && i == this.data.getGates().size() - 1) ||
									(highlitGate < 0 && i == 0)
								)
							){
							if (highlightHoveredGateMovingRule == movingRuleHighlight.whiteBars) {
								line.color = new Color(Color.WHITE);
								line.scaleX = 3; //RevVisGDX.singleton.camera.viewportWidth;
								line.scaleY = RevVisGDX.singleton.camera.viewportHeight;
								float movementLeft = data.calculateGateMobilityLeft(i);
								float movementRight = data.calculateGateMobilityRight(i);
								line.y = 0;

								line.x = xCoord + ((movementRight * smoothScaleX) + ((movementRight + 1) * smoothScaleX)) / 2f;
								line.draw();

								line.x = xCoord - ((movementLeft * smoothScaleX) + ((movementLeft + 1) * smoothScaleX)) / 2f;
								line.draw();
							} else if (highlightHoveredGateMovingRule == movingRuleHighlight.boxes) {
								line.color = new Color(0.5f, 0.5f, 0.5f, 0.5f);
								
								float movementLeft = data.calculateGateMobilityLeft(i);
								float movementRight = data.calculateGateMobilityRight(i);
								
								this.boxOverlayX = xCoord + ((movementRight - movementLeft) / 2f) * smoothScaleX;
								this.boxOverlayWidth = (movementRight + movementLeft + 1) * smoothScaleX;
							}
						}

						DrawableSprite targetGate;
						DrawableSprite controlGate;

						if (!(this.gateDisplay == gateElementDisplay.alwaysBoxy) && (maxDim >= reduceGatesToBlocksWhenSmallerThanPixels || this.gateDisplay == gateElementDisplay.alwaysDetailed)) {
							targetGate = gate01;
							controlGate = gate02;
							targetGate.setDimensions(maxDim, maxDim);
							controlGate.setDimensions(maxDim, maxDim);
						} else {
							targetGate = line;
							controlGate = line;
							targetGate.setDimensions(smoothScaleX, smoothScaleY);
							controlGate.setDimensions(smoothScaleX, smoothScaleY);
						}
						targetGate.color = gateColor;
						targetGate.y = getLineYScreenCoord(data.getGate(i).output);
						targetGate.x = xCoord;
						
						targetGate.draw();

						controlGate.color = gateColor;
						for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
							controlGate.y = getLineYScreenCoord(data.getGate(i).getInputs().get(j));
							controlGate.x = xCoord;
							
							controlGate.draw();
						}
					}
				/**
				 * If lines are supposed to be drawn grouped, go here!
				 * This is a little messy... as the loop is still being carried
				 * out once per gate, groups are generated on-the-fly, with each
				 * gate not being drawn by itself but instead being added to the
				 * group, which is drawn as soon as the current gate has a different
				 * target variable
				 */
				} else {
					
					//First: Add current gate to group.
					for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
						minY = Math.min(minY, getLineYScreenCoord(data.getGate(i).getInputs().get(j)));
						maxY = Math.max(maxY, getLineYScreenCoord(data.getGate(i).getInputs().get(j)));
					}
					minY = Math.min(minY, getLineYScreenCoord(data.getGate(i).output));
					maxY = Math.max(maxY, getLineYScreenCoord(data.getGate(i).output));
					
					//Second: Check if current gate is the group's last gate
					boolean drawGroup;
					if (this.neighbourhoodGrouping == lineGrouping.single || this.neighbourhoodGrouping == lineGrouping.singleGreyscale) {
						drawGroup = (i == data.getGates().size() - 1) || (!(data.getGate(i + 1).output.equals(currentGroup)));
					} else {
						if (data.getBus(data.getGate(i).output) != null) {
							drawGroup = (i == data.getGates().size() - 1) || (!(data.getBus(data.getGate(i + 1).output).equals(currentGroup)));
						} else {
							drawGroup = (i == data.getGates().size() - 1) || (!(data.getGate(i + 1).output.equals(currentGroup)));
						}
					}
					
					//Third: If second, draw group
					if (drawGroup) {
						maxX = xCoord + (0.5f) * smoothScaleX;

						minY -= 0.5f * smoothScaleY;
						maxY += 0.5f * smoothScaleY;

						if (!currentGroup.equals("")) {
							
							currentHue += 1f / groupColorAmount;
							float usedHue = currentHue;
							if (groupCount % 2 == 0)
								usedHue += 0.5f;
							currentSaturation = 1f;
							
							//actually draw group
							if (this.neighbourhoodGrouping != lineGrouping.singleGreyscale)
								line.color = hsvToRgb(usedHue, currentSaturation, 0.5f);
							else
								line.color = new Color(0.5f, 0.5f, 0.5f, 1);
							line.scaleX = maxX - minX; //RevVisGDX.singleton.camera.viewportWidth;
							line.scaleY = maxY - minY;
							line.x = (maxX + minX) / 2;
							line.y = (maxY + minY) / 2; //+ RevVisGDX.singleton.camera.viewportHeight;
							line.draw();
							
							
							if (this.neighbourhoodGrouping == lineGrouping.singleGreyscale) {
								line.color = new Color(0f, 0f, 0f, 1);
								line.scaleX = maxX - minX; //RevVisGDX.singleton.camera.viewportWidth;
								line.scaleY = smoothScaleY;
								line.x = (maxX + minX) / 2;
								line.y = (getLineYScreenCoord(data.getGate(i).output));
								line.draw();
							}
							
							groupCount++;
						}

						if (i < data.getGates().size() - 1) {
							//set current group to output in order to collect all gates and reset variables.
							if (!(this.neighbourhoodGrouping == lineGrouping.bus) || data.getBus(data.getGate(i).output) == null) {
								currentGroup = data.getGate(i + 1).output;
							} else {
								currentGroup = data.getBus(data.getGate(i + 1).output);
							}
							minX = xCoord + 0.5f * smoothScaleX;
							minY = Float.MAX_VALUE;
							maxY = -Float.MAX_VALUE;
						}
					}
				}
			}
		}
	}
	
	protected float getLineYScreenCoord(String line) {
		float y = (this.data.getVars().indexOf(line) - (data.getAmountOfVars() / 2)) - smoothOffsetY; //+ RevVisGDX.singleton.camera.viewportHeight;
		y *= smoothScaleY;
		return y;
	}

	/**
	 * Draws the variables and calculates the y coordinate for each variable, which is
	 * returned as a HashMap
	 * @return the mapping from variable names to y-coordinates on screen
	 */
	private void drawVariables() {		
		for (int i = 0; i < data.getAmountOfVars(); i++) {
			drawLine(i);
		}
	}

	protected void drawLine(int indexOfVariable) {
		int firstGateCoord = getFirstGateCoord(indexOfVariable);
		int lastGateCoord = getLastGateCoord(indexOfVariable);

		Color multiplier;
		
		if (colorizeConstants && this.data.constValue(data.getVars().get(indexOfVariable)) >= 0)
			multiplier = Color.BLACK;
		else
			multiplier = Color.WHITE;
		
		drawLineSegment(indexOfVariable, -1, firstGateCoord, false, multiplier);
		
		drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, true);
		
		if (colorizeGarbageLine && this.data.isGarbageLine(data.getVars().get(indexOfVariable)))
			multiplier = Color.BLACK;
		else
			multiplier = Color.WHITE;
		drawLineSegment(indexOfVariable, lastGateCoord,	this.data.getGates().size(), false, multiplier);
		
		drawVariableNameOverlay(indexOfVariable);
	}

	protected int getLastGateCoord(int indexOfVariable) {
		int lastGateCoord = data.getCoordOfGate(data.getLastGateOnLine(data.getVars().get(indexOfVariable)));
		return lastGateCoord;
	}

	protected int getFirstGateCoord(int indexOfVariable) {
		int firstGateCoord = data.getCoordOfGate(data.getFirstGateOnLine(data.getVars().get(indexOfVariable)));
		return firstGateCoord;
	}

	protected void drawVariableNameOverlay(int indexOfVariable) {
		if (showLineNames && smoothScaleY > 10) {
			Color lineNameColor = new Color(Color.WHITE);
			lineNameColor.a = Math.max(0, Math.min(1, (smoothScaleY - 10f) / 5f));
			RevVisGDX.singleton.mc.addHUDMessage(data.getVars().get(indexOfVariable).hashCode(), data.getVars().get(indexOfVariable), RevVisGDX.singleton.camera.viewportWidth - 64, (line.y + RevVisGDX.singleton.camera.viewportHeight / 2f), lineNameColor);
		} else {
			RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(indexOfVariable).hashCode());
		}
	}

	protected void drawLineSegment(int indexOfVariable, int firstGateCoord, int lastGateCoord, boolean currentlyUsed) {
		drawLineSegment(indexOfVariable, firstGateCoord, lastGateCoord, currentlyUsed, Color.WHITE);
	}
	
	/**
	 * Draws a segment of a variable (i.e. a horizontal line)
	 * @param indexOfVariable the index of the given variable (i.e. the y coordinate in circuit-space, if you want to put it that way).
	 * @param firstGateCoord the starting point (on the left hand side) of the line
	 * @param lastGateCoord the end coordinate (on the right hand side) of the line
	 * @param currentlyUsed if the drawLinesColourizedWhenUsed field is set to absolute or relative and this
	 * value is set to true, the line is colourized accordingly. Set this to true if the segment is placed
	 * after the first gate on the variable but before the last one.
	 * @param additionalMultiplier after all other colour calculations, the resulting line colour is multiplied with the colour
	 * given here. Use Color.WHITE if the colour should remain unchanged.
	 */
	protected void drawLineSegment(int indexOfVariable, int firstGateCoord, int lastGateCoord, boolean currentlyUsed, Color additionalMultiplier) {
		float minimumUsagePercent = ((float)data.getMinimumLineUsage() / (float)data.getMaximumLineUsage());
		float usagePercent = ((float)data.getLineUsage(data.getVars().get(indexOfVariable)) / (float)data.getMaximumLineUsage());
		
		Color col = new Color(lineBaseColor);
		line.color = col;
		
		if (drawLinesColourizedWhenUsed == drawLinesColourizedByUsageType.relative) {
			float lineUsageValue = (usagePercent - minimumUsagePercent) / (1 - minimumUsagePercent);
			if (lineUsageValue <= 0.5f) {
				col.r = lineUsageValue * 2;
				col.g = 1;
				col.b = 0;
			} else {
				col.r = 1;
				col.g = 1 - (lineUsageValue - 0.5f) * 2;
				col.b = 0;
			}
		} else if (drawLinesColourizedWhenUsed == drawLinesColourizedByUsageType.absolute) {
			float lineUsageValue = (float)data.getLineUsage(data.getVars().get(indexOfVariable)) / (float)data.getGates().size();
			if (lineUsageValue <= 0.5f) {
				col.r = lineUsageValue * 2;
				col.g = 1;
				col.b = 0;
			} else {
				col.r = 1;
				col.g = 1 - (lineUsageValue - 0.5f) * 2;
				col.b = 0;
			}
		}

		if (markVariableTypes) {
			if (data.isInputOnly(data.getVars().get(indexOfVariable)))
				col.add(0, 1f, 0, 0);
			else if (data.isTargetOnly(data.getVars().get(indexOfVariable)))
				col.add(1f, 0, 0, 0);
			else
				col.add(1f, 1f, 0, 0);
		}

		float left = xCoordOnScreen(firstGateCoord);
		float right = xCoordOnScreen(lastGateCoord);
		line.x = (left + right) / 2;
		line.scaleX = left - right;
		if (drawLinesDarkWhenUsed && currentlyUsed) {
			col.sub(0.25f, 0.25f, 0.25f, 0);
		}

		
		if (!drawnBus.equals("")) {
			if (data.isMemberOfBus(data.getVars().get(indexOfVariable), drawnBus)) {
				col.add(0,0,0.5f,0);
			}
		}
		
		switch(this.lineType) {
		case full:
			line.scaleY = smoothScaleY;
			break;
		case usageWide:
			line.scaleY = smoothScaleY;
			line.scaleY *= usagePercent;
			line.scaleY = Math.max(1, line.scaleY);
			break;
		case pixelWide:
			line.scaleY = 1;
			break;
		default:
			line.scaleY = smoothScaleY;
			break;
		}
		line.y = getLineYScreenCoord(data.getVars().get(indexOfVariable));
		
		if (colorizeLineUsage) {
			line.color = line.color.mul(usagePercent, usagePercent, usagePercent, 1);
		}

		line.color.mul(additionalMultiplier);
		if (this.lineType != lineWidth.hidden) {
			if (line.scaleX < 0) {
				line.draw();
			}
		}
	}

	/**
	 * Calculates the x coordinate of a given gate
	 * @param i the gate index
	 * @return the x coordinate on screen
	 */
	protected float xCoordOnScreen(int i) {
		return xCoordOnScreen((float)i);
	}
	
	/**
	 * Calculates the x coordinate of a given value. Keep in mind that
	 * this is still in gate-space, so a value of 0 would be at the center
	 * of the circuit's first gate.
	 * @param i the value to translate
	 * @return the x coordinate on screen
	 */
	protected float xCoordOnScreen(float i) {
		float xCoord = i;
		xCoord += smoothOffsetX;
		xCoord *= smoothScaleX;
		return xCoord;
	}
	
	/**
	 * If the two scaling factors aren't equal, this sets the larger scaling factor to
	 * the smaller one in order to display square elements on screen
	 */
	public void shrinkToSquareAlignment() {
		if (getScaleY() < getScaleX())
			setScaleX(getScaleY());
		else
			setScaleY(getScaleX());
	}
	
	private int gateAt(int x) {
		float xResult = x - RevVisGDX.singleton.camera.viewportWidth / 2f;
		
		xResult /= getScaleX();
		xResult -= offsetX;
		xResult += 0.5f;
		
		return (int)xResult;
	}
	
	/**
	 * Highlights the gate at the given mouse coordinates, as in "hovering something"
	 * @param x x coordinate of the cursor
	 * @param y y coordinate of the cursor
	 */
	public void highlightAt(int x, int y) {
		highlitGate = gateAt(x);
	}

	//http://stackoverflow.com/questions/7896280/converting-from-hsv-hsb-in-java-to-rgb-without-using-java-awt-color-disallowe
	private static Color hsvToRgb(float hue, float saturation, float value) {

		while (hue >= 1)
			hue -= 1;
		while (hue < 0)
			hue += 1;
		int h = (int)(hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0: return new Color(value, t, p, 1);
		case 1: return new Color(q, value, p, 1);
		case 2: return new Color(p, value, t, 1);
		case 3: return new Color(p, q, value, 1);
		case 4: return new Color(t, p, value, 1);
		case 5: return new Color(value, p, q, 1);
		default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
		}
	}
	

	/**
	 * Cycles through the available settings of the lineType field.
	 */
	public void toggleLineWidth() {
		switch (this.lineType) {
		case full:
			this.lineType = lineWidth.usageWide;
			System.out.println("Toggled line width to usage");
			break;
		case usageWide:
			this.lineType = lineWidth.pixelWide;
			System.out.println("Toggled line width to pixel");
			break;
		case pixelWide:
			this.lineType = lineWidth.hidden;
			System.out.println("Toggled line width to hidden");
			break;
		case hidden:
			this.lineType = lineWidth.full;
			System.out.println("Toggled line width to full");
			break;
		default:
			this.lineType = lineWidth.full;
			System.out.println("Toggled line width to full via error fallback");
			break;
		}
	}
	
	/**
	 * Cycles through the available settings of the neighbourhoodGrouping field.
	 */
	public void toggleNeighbourGrouping() {
		switch(this.neighbourhoodGrouping) {
		case none:
			this.neighbourhoodGrouping = lineGrouping.single;
			break;
		case single:
			this.neighbourhoodGrouping = lineGrouping.singleGreyscale;
			break;
		case singleGreyscale:
			this.neighbourhoodGrouping = lineGrouping.bus;
			break;
		case bus:
			this.neighbourhoodGrouping = lineGrouping.none;
			break;
		default:
			this.neighbourhoodGrouping = lineGrouping.none;
				
		}
	}
	
	/**
	 * Cycles through the available settings of the highlightHoveredGateMovingRule field
	 */
	public void toggleMovingRuleHighlight() {
		switch (this.highlightHoveredGateMovingRule) {
		case none:
			this.highlightHoveredGateMovingRule = movingRuleHighlight.whiteBars;
			break;
		case whiteBars:
			this.highlightHoveredGateMovingRule = movingRuleHighlight.boxes;
			break;
		case boxes:
			this.highlightHoveredGateMovingRule = movingRuleHighlight.none;
			break;
		default:
			break;
		}
	}
	
	/**
	 * Cycles through the available settings of the colourizeGatesByMobility field
	 */
	public void toggleMobilityGateColours() {
		switch (this.colourizeGatesByMobility) {
		case none:
			this.colourizeGatesByMobility = movingRuleDisplay.leftRight;
			break;
		case leftRight:
			this.colourizeGatesByMobility = movingRuleDisplay.total;
			break;
		case total:
			this.colourizeGatesByMobility = movingRuleDisplay.totalAbsolute;
			break;
		case totalAbsolute:
			this.colourizeGatesByMobility = movingRuleDisplay.none;
			break;
		default:
			break;
		}
	}
	
	/**
	 * Cycles through the available settings of the gateDisplay field
	 */
	public void toggleGateDisplay() {
		switch (this.gateDisplay) {
		case alwaysDetailed:
			this.gateDisplay = gateElementDisplay.dynamic;
			break;
		case dynamic:
			this.gateDisplay = gateElementDisplay.alwaysBoxy;
			break;
		case alwaysBoxy:
			this.gateDisplay = gateElementDisplay.alwaysDetailed;
			break;
		default:
			break;
		}
	}

	/**
	 * retrieves the current x scaling factor
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * sets the current x scaling factor
	 * Keep in mind that the value used for actually drawing the
	 * circuit is successively approaching the given value for a
	 * smooth camera movement. Use setScaleImmediately if the viewport
	 * is supposed to skip those inbetween steps.
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}
	
	/**
	 * retrieves the current y scaling factor
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * Sets the current y scaling factor.
	 * Keep in mind that the value used for actually drawing the
	 * circuit is successively approaching the given value for a
	 * smooth camera movement. Use setScaleImmediately if the viewport
	 * is supposed to skip those inbetween steps.
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
	
	/**
	 * Calculates the screen bounds in gate-space
	 * @return the screen bounds
	 */
	public Rectangle getViewBounds() {
		Rectangle result = new Rectangle();
		
		float centerX = -offsetX;
		float width = Gdx.graphics.getWidth() * (1f / scaleX);
		float centerY = offsetY;
		float height = Gdx.graphics.getHeight() * (1f / scaleY);
		result.set(centerX - (width / 2f), centerY + (data.getAmountOfVars() / 2) - (height / 2f), width, height);
		return result;
	}
	
	/**
	 * Sets the screen bounds in gate-space
	 * @param bounds the area the viewport is supposed to show.
	 */
	public void setViewBounds(Rectangle bounds) {
		float targetHeight = Gdx.graphics.getHeight() / bounds.height;
		float targetWidth = Gdx.graphics.getWidth() / bounds.width;
		float targetOffsetX = (bounds.x + (bounds.width / 2f));
		float targetOffsetY = bounds.y - (data.getAmountOfVars() / 2) + (bounds.height / 2f);
				
		setScaleX(targetWidth);
		setScaleY(targetHeight);
		this.offsetX = -targetOffsetX;
		this.offsetY = targetOffsetY;
	}
	
	private int busDrawn = 0;
	
	/**
	 * Cycles through all available buses and draws them separately.
	 * This includes a "draw no bus" state, so just keep calling this
	 * method until no bus is drawn anymore if you need that.
	 */
	public void toggleBusDrawing() {
		if (drawnBus.equals("")) {
			if (this.data.getBuses().size() > 0) {
				drawnBus = this.data.getBuses().get(0);
				busDrawn = 0;
			}
		} else {
			busDrawn++;
			if (busDrawn < this.data.getBuses().size()) {
				drawnBus = this.data.getBuses().get(busDrawn);
			} else {
				busDrawn = 0;
				drawnBus = "";
			}
		}
		
		if (drawnBus.equals("")) {
			RevVisGDX.singleton.mc.addMessage(Messages.highlightNoBus);
		} else {
			RevVisGDX.singleton.mc.addMessage(Messages.highlightBus.replace("$1", this.drawnBus));
		}
	}
	
	/**
	 * Cycles through the available values of the drawLinesColourizedWhenUsed field.
	 */
	public void toggleLineUsageColouring() {
		switch (this.drawLinesColourizedWhenUsed) {
		case none:
			this.drawLinesColourizedWhenUsed = drawLinesColourizedByUsageType.relative;
			System.out.println("Toggled line usage color to relative");
			break;
		case relative:
			this.drawLinesColourizedWhenUsed = drawLinesColourizedByUsageType.absolute;
			System.out.println("Toggled line usage color to absolute");
			break;
		case absolute:
			this.drawLinesColourizedWhenUsed = drawLinesColourizedByUsageType.none;
			System.out.println("Toggled line usage color to none");
			break;
		default:
			break;
		}
	}
	
	/**
	 * Resets the zoom to 1 px per element
	 */
	public void zoomTo1Px() {
		this.scaleX = 1;
		this.scaleY = 1;
	}
	
	/**
	 * Resets all drawing parameters to their default value.
	 */
	public void setAllDefault() {
		colorizeGarbageLine = false;
		hideGates = false;
		countGatesForGroupColor = false;
		colorizeConstants = false;
		drawVerticalLines = true;
		drawLinesDarkWhenUsed = false;
		reduceGatesToBlocksWhenSmallerThanPixels = 8f;
		groupColorAmount = 16f;
		colourizeGatesByMobility = movingRuleDisplay.none;
		drawAccumulatedMovingRule = false;
		highlightHoveredGate = false;
		highlightHoveredGateMovingRule = movingRuleHighlight.none;
		colorizeLineUsage = false;
		showLineNames = true;
		lineType = lineWidth.pixelWide;
		neighbourhoodGrouping = lineGrouping.none;
		drawnBus = "";
		drawSubCircuits = true;
		markVariableTypes = false;
		drawLinesColourizedWhenUsed = drawLinesColourizedByUsageType.none;
		gateDisplay = gateElementDisplay.dynamic;
		lineBaseColor = new Color(Color.BLACK);
	}

	/**
	 * Resets the zoom so that the whole circuit is shown.
	 */
	public void zoomExtents() {
		float x = 1f / this.data.getGates().size();
		float y = 1f / this.data.getVars().size();
		float xFactor = Gdx.graphics.getWidth();
		float yFactor = Gdx.graphics.getHeight();
		float maxScale = Math.min(x * xFactor, y * yFactor);
		this.scaleX = maxScale;
		this.scaleY = maxScale;
		this.offsetY = 0;
		this.offsetX = this.data.getGates().size() / -2f;
	}
	
	/**
	 * Resets the zoom so that the whole circuit is shown without
	 * smoothly zooming to those settings.
	 */
	public void zoomExtentsImmediately() {
		zoomExtents();
		this.smoothScaleX = scaleX;
		this.smoothScaleY = scaleY;
	}
	
	/**
	 * Sets the zoom to the given values without smoothly approaching
	 * those target values (instead sets them immediately).
	 */
	public void setScaleImmediately(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.smoothScaleX = scaleX;
		this.scaleY = scaleY;
		this.smoothScaleY = scaleY;
	}
}
