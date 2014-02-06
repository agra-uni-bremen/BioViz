package de.dfki.revvisgdx;

import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import de.dfki.revlibReader.ReversibleCircuit;

public class DrawableCircuit implements Drawable {
	private ReversibleCircuit data;

	DrawableSprite line;
	DrawableSprite gate01;
	DrawableSprite gate02;

	private float scaleX = 1;
	public float offsetX = 0;
	private float scaleY = 1;
	public float offsetY = 0;
	
	private float smoothScaleX = 1;
	private float smoothScaleY = 1;
	
	private float scalingDelay = 4f;

	public boolean colorizeGarbageLine = false;
//	public boolean pixelWideLines = false;
//	public boolean drawGroups = false;
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
//	public boolean lineWidthByUsage = false;
	public boolean showLineNames = true;
	public lineWidth lineType = lineWidth.pixelWide;
	private lineGrouping neighbourhoodGrouping = lineGrouping.none;
	private String drawnBus = "";
	public boolean drawSubCircuits = true;
	public boolean markVariableTypes = false;
	public boolean drawLinesColourizedWhenUsed = false;
	
	private int highlitGate = 0;
	
	public enum lineWidth {hidden, pixelWide, usageWide, full}
	public enum lineGrouping {none, single, singleGreyscale, bus}
	public enum movingRuleDisplay{none, leftRight, total}
	public enum movingRuleHighlight{none, whiteBars, boxes}
	
	private float boxOverlayX;
	private float boxOverlayWidth;

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
		
		HashMap<String, Float> signalsToCoords = drawVariables();

		drawGates(signalsToCoords);
		
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
				
//				if (showLineNames && smoothScaleY > 10) {
				Color lineNameColor = new Color(line.color).mul(0.5f);
//					lineNameColor.a = Math.max(0, Math.min(1, (smoothScaleY - 10f) / 5f));
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
//				} else {
//					RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(i).hashCode());
//				}
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
	 * Draws the gates using previously calculated y coordinates.
	 * @param signalsToCoords the y coordinates for each variable
	 */
	private void drawGates(HashMap<String, Float> signalsToCoords) {
		if (!hideGates) {
			float minY = 0, maxY = 0, minX = 0, maxX = 0;
			Color groupCol = Color.RED.cpy();
			float currentHue = 0;
			float currentSaturation = 1;
			String currentGroup = "";
			int groupCount = 0;
			
			for (int i = 0; i < data.getGates().size(); i++) {
				float xCoord = xCoordOnScreen(i);

				if (this.neighbourhoodGrouping == lineGrouping.none) {
					if (xCoord > -RevVisGDX.singleton.camera.viewportWidth / 2 && xCoord < RevVisGDX.singleton.camera.viewportWidth / 2) {

						float maxDim = Math.min(smoothScaleX, smoothScaleY);

						Color gateColor = new Color(Color.BLACK);
						if(this.colourizeGatesByMobility == movingRuleDisplay.leftRight) {
							int leftRange = data.calculateGateMobilityLeft(i);
							int rightRange = data.calculateGateMobilityRight(i);
							gateColor.r = Math.min(1, leftRange / (float)data.calculateMaximumMobility());
							gateColor.g = Math.min(1, rightRange / (float)data.calculateMaximumMobility());
						} else if (this.colourizeGatesByMobility == colourizeGatesByMobility.total) {
							int minMobility = data.calculateMinimumMobilityTotal();
							int maxMobility = data.calculateMaximumMobilityTotal();
							int gateMobility = data.calculateGateMobilityLeft(i) + data.calculateGateMobilityRight(i);
							if (gateMobility < (maxMobility - minMobility) / 2) {
								gateColor.r = 1f;
								gateColor.g = gateMobility / ((maxMobility - minMobility) / 2f);
							} else {
								gateColor.g = 1f;
								gateColor.r = (gateMobility - ((maxMobility - minMobility) / 2f)) / ((maxMobility - minMobility) / 2f);
							}
						}
						
						if (i == highlitGate && highlightHoveredGate) {
							gateColor.add(new Color(0.25f, 0.25f, 1f, 0f));
						}

						if (drawVerticalLines) {
							minY = signalsToCoords.get(data.getGate(i).output);
							maxY = minY;

							for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
								minY = Math.min(minY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
								maxY = Math.max(maxY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
							}
															
							line.color = gateColor;
							line.scaleX = 1; //RevVisGDX.singleton.camera.viewportWidth;
							line.scaleY = maxY - minY;
							line.x = xCoord;
							line.y = (maxY + minY) / 2; //+ RevVisGDX.singleton.camera.viewportHeight;
							line.draw();
						}
						
						if (highlightHoveredGateMovingRule != movingRuleHighlight.none && i == highlitGate) {
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

						if (maxDim >= reduceGatesToBlocksWhenSmallerThanPixels) {
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
						targetGate.y = signalsToCoords.get(data.getGate(i).output);
						targetGate.x = xCoord;
						
						targetGate.draw();

						controlGate.color = gateColor;
						for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
							controlGate.y = signalsToCoords.get(data.getGate(i).getInputs().get(j));
							controlGate.x = xCoord;
							
							controlGate.draw();
						}
					}
				} else {
					
					boolean drawGroup;
					if (this.neighbourhoodGrouping == lineGrouping.single || this.neighbourhoodGrouping == lineGrouping.singleGreyscale) {
						drawGroup = !(data.getGate(i).output.equals(currentGroup));
					} else {
						if (data.getBus(data.getGate(i).output) != null) {
							drawGroup = !(data.getBus(data.getGate(i).output).equals(currentGroup));
						} else {
							drawGroup = !(data.getGate(i).output.equals(currentGroup));
						}
					}
					
					if (i >= data.getGates().size() - 1) {
						for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
							minY = Math.min(minY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
							maxY = Math.max(maxY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
						}
						minY = Math.min(minY, signalsToCoords.get(data.getGate(i).output));
						maxY = Math.max(maxY, signalsToCoords.get(data.getGate(i).output));
						 xCoord = xCoordOnScreen(i + 1);
						drawGroup = true;
					}
					
					if (drawGroup) {
						maxX = xCoord - (0.5f) * smoothScaleX;

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
								line.y = (signalsToCoords.get(data.getGate(i -1).output)); //+ RevVisGDX.singleton.camera.viewportHeight;
								line.draw();
							}
							
							groupCount++;
						}

						//set current group to output in order to collect all gates and reset variables.
						if (this.neighbourhoodGrouping == lineGrouping.single || data.getBus(data.getGate(i).output) == null) {
							currentGroup = data.getGate(i).output;
						} else {
							currentGroup = data.getBus(data.getGate(i).output);
						}
						minX = xCoord - 0.5f * smoothScaleX;
						minY = signalsToCoords.get(data.getGate(i).output);
						maxY = minY;
						//groupCol.
					}

					for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
						minY = Math.min(minY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
						maxY = Math.max(maxY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
					}
					minY = Math.min(minY, signalsToCoords.get(data.getGate(i).output));
					maxY = Math.max(maxY, signalsToCoords.get(data.getGate(i).output));
				}
			}
		}
	}

	/**
	 * Draws the variables and calculates the y coordinate for each variable, which is
	 * returned as a HashMap
	 * @return the mapping from variable names to y-coordinates on screen
	 */
	private HashMap<String, Float> drawVariables() {
		HashMap<String, Float> signalsToCoords = new HashMap<String, Float>();
		float minimumUsagePercent = ((float)data.getMinimumLineUsage() / (float)data.getMaximumLineUsage());
		
		for (int i = 0; i < data.getAmountOfVars(); i++) {

			int firstGateCoord = data.getCoordOfGate(data.getFirstGateOnLine(data.getVars().get(i)));
			int lastGateCoord = data.getCoordOfGate(data.getLastGateOnLine(data.getVars().get(i)));
			float usagePercent = ((float)data.getLineUsage(data.getVars().get(i)) / (float)data.getMaximumLineUsage());

			for (int j = 0; j < 3; j++) {
				Color col = new Color(Color.BLACK);
				line.color = col;
				col.add(0.25f, 0.25f, 0.25f, 0);

				if (markVariableTypes) {
					if (data.isInputOnly(data.getVars().get(i)))
						col.add(0, 0.5f, 0, 0);
					else if (data.isTargetOnly(data.getVars().get(i)))
						col.add(0.5f, 0, 0, 0);
					else
						col.add(0.5f, 0.5f, 0, 0);
				}

				if (j == 0) {
					float left = -RevVisGDX.singleton.camera.viewportWidth;
					float right = xCoordOnScreen(firstGateCoord);
					line.x = (left + right) / 2;
					line.scaleX = left - right;
					
					if (colorizeConstants && data.constValue(data.getVars().get(i)) >= 0) {
						col.add(0, 0, 0.75f, 0);
					}

					col.add(0.25f, 0.25f, 0.25f, 0);
				} else if (j == 1) {
					float left = xCoordOnScreen(firstGateCoord);
					float right = xCoordOnScreen(lastGateCoord);
					line.x = (left + right) / 2;
					line.scaleX = left - right;
					if (!drawLinesDarkWhenUsed)
						col.add(0.25f, 0.25f, 0.25f, 0);
					if (drawLinesColourizedWhenUsed) {
						float lineUsageValue = (usagePercent - minimumUsagePercent) / (1 - minimumUsagePercent);
						if (lineUsageValue <= 0.5f) {
							col.r = lineUsageValue * 2;
							col.g = 1;
							col.b = 0;
						} else {
							col.r = 1;
							col.g = lineUsageValue - 0.5f * 2;
							col.b = 0;
						}
					}
				} else {
					float left = xCoordOnScreen(lastGateCoord);
					float right = RevVisGDX.singleton.camera.viewportWidth;;
					line.x = (left + right) / 2;
					line.scaleX = left - right;
					
					if (colorizeGarbageLine && data.isGarbageLine(data.getVars().get(i))) {
						col.add(0, 0, 0.75f, 0);
					}
					
					col.add(0.25f, 0.25f, 0.25f, 0);
				}
				
				if (!drawnBus.equals("")) {
					if (data.isMemberOfBus(data.getVars().get(i), drawnBus)) {
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
				line.y = (i - (data.getAmountOfVars() / 2)) - offsetY; //+ RevVisGDX.singleton.camera.viewportHeight;
				line.y *= smoothScaleY;
				
				if (colorizeLineUsage) {
					line.color = line.color.mul(usagePercent, usagePercent, usagePercent, 1);
				}

				if (!data.isFunctionLine(data.getVars().get(i))) {
					//					line.color.mul(0.5f);
				}
				//TODO: this probably should be catched earlier
				if (this.lineType != lineWidth.hidden) {
					if (line.scaleX < 0) {
						line.draw();
					}
				}
			}

			signalsToCoords.put(data.getVars().get(i), line.y);
			
			if (showLineNames && smoothScaleY > 10) {
				Color lineNameColor = new Color(Color.WHITE);
				lineNameColor.a = Math.max(0, Math.min(1, (smoothScaleY - 10f) / 5f));
				RevVisGDX.singleton.mc.addHUDMessage(data.getVars().get(i).hashCode(), data.getVars().get(i), RevVisGDX.singleton.camera.viewportWidth - 64, (line.y + RevVisGDX.singleton.camera.viewportHeight / 2f), lineNameColor);
			} else {
				RevVisGDX.singleton.mc.removeHUDMessage(data.getVars().get(i).hashCode());
			}
		}
		return signalsToCoords;
	}

	private float xCoordOnScreen(int i) {
		float xCoord = i;
		xCoord += offsetX;
		xCoord *= smoothScaleX;
		return xCoord;
	}
	
	public void shrinkToSquareAlignment() {
		if (getScaleY() < getScaleX())
			setScaleX(getScaleY());
		else
			setScaleY(getScaleX());
	}
	
	private int gateAt(int x) {
		float xResult = x - RevVisGDX.singleton.camera.viewportWidth / 2;
		
		xResult /= getScaleX();
		xResult -= offsetX;
		
		return (int)xResult;
	}
	
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
	
	//TODO
	public void toggleLineWidth() {
		switch (this.lineType) {
		case full:
			this.lineType = lineWidth.usageWide;
			break;
		case usageWide:
			this.lineType = lineWidth.pixelWide;
			break;
		case pixelWide:
			this.lineType = lineWidth.hidden;
			break;
		case hidden:
			this.lineType = lineWidth.full;
			break;
		default:
			this.lineType = lineWidth.full;
			break;
		}
	}
	
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
	
	public void toggleMobilityGateColours() {
		switch (this.colourizeGatesByMobility) {
		case none:
			this.colourizeGatesByMobility = movingRuleDisplay.leftRight;
			break;
		case leftRight:
			this.colourizeGatesByMobility = movingRuleDisplay.total;
			break;
		case total:
			this.colourizeGatesByMobility = movingRuleDisplay.none;
			break;
		default:
			break;
		}
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
	
	private int busDrawn = 0;
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
	 * Resets the zoom to 1 px per element
	 */
	public void zoomTo1Px() {
		this.scaleX = 1;
		this.scaleY = 1;
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
}
