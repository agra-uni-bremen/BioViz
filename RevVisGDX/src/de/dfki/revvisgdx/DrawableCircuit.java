package de.dfki.revvisgdx;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

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
	public boolean drawGroups = false;
	public boolean hideGates = false;
	public boolean countGatesForGroupColor = false;
	public boolean colorizeConstants = false;
	public boolean drawVerticalLines = true;
	public boolean drawLinesDarkWhenUsed = true;
	public float reduceGatesToBlocksWhenSmallerThanPixels = 8f;
	public float groupColorAmount = 16f;
	public boolean colourizeGatesByMobility = false;
	public boolean drawAccumulatedMovingRule = true;
	public boolean highlightHoveredGate = true;
	public boolean highlightHoveredGateMovingRule = true;
	public boolean colorizeLineUsage = false;
//	public boolean lineWidthByUsage = false;
	public boolean showLineNames = true;
	private lineWidth lineType = lineWidth.full;
	
	private int highlitGate = 0;
	
	private enum lineWidth {pixelWide, usageWide, full}

	public DrawableCircuit(ReversibleCircuit toDraw) {
		this.data = toDraw;
		line = new DrawableSprite("data/BlackPixel.png");
		gate01 = new DrawableSprite("data/tgate01.png");
		gate02 = new DrawableSprite("data/tgate02.png");
	}

	@Override
	public void draw() {

//		float distanceV = (RevVisGDX.singleton.camera.viewportHeight / (data.getAmountOfVars() + 2));
//		float distanceH = RevVisGDX.singleton.camera.viewportWidth / (data.getGates().size() + 2);
		
		smoothScaleX += (getScaleX() - smoothScaleX) / scalingDelay;
		smoothScaleY += (getScaleY() - smoothScaleY) / scalingDelay;
		
		HashMap<String, Float> signalsToCoords = new HashMap<String, Float>();

		for (int i = 0; i < data.getAmountOfVars(); i++) {

			int firstGateCoord = data.getCoordOfGate(data.getFirstGateOnLine(data.getVars().get(i)));
			int lastGateCoord = data.getCoordOfGate(data.getLastGateOnLine(data.getVars().get(i)));

			for (int j = 0; j < 3; j++) {
				Color col = new Color(Color.BLACK);
				line.color = col;
				col.add(0.25f, 0.25f, 0.25f, 0);

				if (data.isInputOnly(data.getVars().get(i)))
					col.add(0, 0.5f, 0, 0);
				else if (data.isTargetOnly(data.getVars().get(i)))
					col.add(0.5f, 0, 0, 0);

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
				
				float usagePercent = ((float)data.getLineUsage(data.getVars().get(i)) / (float)data.getMaximumLineUsage());
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
				if (line.scaleX < 0) {
					line.draw();
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

		if (!hideGates) {
			float minY = 0, maxY = 0, minX = 0, maxX = 0;
			Color groupCol = Color.RED.cpy();
			float currentHue = 0;
			float currentSaturation = 1;
			String currentGroup = "";
			int groupCount = 0;
			
			for (int i = 0; i < data.getGates().size(); i++) {
				float xCoord = xCoordOnScreen(i);

				if (!drawGroups) {
					if (xCoord > -RevVisGDX.singleton.camera.viewportWidth / 2 && xCoord < RevVisGDX.singleton.camera.viewportWidth / 2) {

						float maxDim = Math.min(smoothScaleX, smoothScaleY);

						Color gateColor = new Color(Color.BLACK);
						if(this.colourizeGatesByMobility) {
							int leftRange = data.calculateGateMobilityLeft(i);
							int rightRange = data.calculateGateMobilityRight(i);
							gateColor.r = Math.min(1, leftRange / 32f);
							gateColor.g = Math.min(1, rightRange / 32f);
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
						
						if (highlightHoveredGateMovingRule && i == highlitGate) {
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
					
					boolean drawGroup = !(data.getGate(i).output.equals(currentGroup));
					
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
							line.color = hsvToRgb(usedHue, currentSaturation, 0.5f);
							line.scaleX = maxX - minX; //RevVisGDX.singleton.camera.viewportWidth;
							line.scaleY = maxY - minY;
							line.x = (maxX + minX) / 2;
							line.y = (maxY + minY) / 2; //+ RevVisGDX.singleton.camera.viewportHeight;
							line.draw();
							groupCount++;
						}

						//set current group to output in order to collect all gates and reset variables.
						currentGroup = data.getGate(i).output;
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
			this.lineType = lineWidth.full;
			break;
		default:
			this.lineType = lineWidth.full;
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
}
