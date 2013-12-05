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

	public float scaleX = 1;
	public float offsetX = 0;
	public float scaleY = 1;
	public float offsetY = 0;

	public boolean colorizeGarbageLine = false;
	public boolean pixelWideLines = false;
	public boolean drawGroups = false;
	public boolean hideGates = false;
	public boolean countGatesForGroupColor = false;
	public boolean colorizeConstants = false;
	public boolean drawVerticalLines = true;
	public boolean drawLinesDarkWhenUsed = true;
	public float reduceGatesToBlocksWhenSmallerThanPixels = 8f;
	public float groupColorAmount = 16f;

	public DrawableCircuit(ReversibleCircuit toDraw) {
		this.data = toDraw;
		line = new DrawableSprite("data/BlackPixel.png");
		gate01 = new DrawableSprite("data/tgate01.png");
		gate02 = new DrawableSprite("data/tgate02.png");
	}

	@Override
	public void draw() {

		float distanceV = (RevVisGDX.singleton.camera.viewportHeight / (data.getAmountOfVars() + 2));
		float distanceH = RevVisGDX.singleton.camera.viewportWidth / (data.getGates().size() + 2);
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
					float right = xCoordOnScreen(distanceH, firstGateCoord);
					line.x = (left + right) / 2;
					line.scaleX = left - right;
					
					if (colorizeConstants && data.constValue(data.getVars().get(i)) >= 0) {
						col.add(0, 0, 0.75f, 0);
					}

					col.add(0.25f, 0.25f, 0.25f, 0);
				} else if (j == 1) {
					float left = xCoordOnScreen(distanceH, firstGateCoord);
					float right = xCoordOnScreen(distanceH, lastGateCoord);
					line.x = (left + right) / 2;
					line.scaleX = left - right;
					if (!drawLinesDarkWhenUsed)
						col.add(0.25f, 0.25f, 0.25f, 0);
				} else {
					float left = xCoordOnScreen(distanceH, lastGateCoord);
					float right = RevVisGDX.singleton.camera.viewportWidth;;
					line.x = (left + right) / 2;
					line.scaleX = left - right;
					
					if (colorizeGarbageLine && data.isGarbageLine(data.getVars().get(i))) {
						col.add(0, 0, 0.75f, 0);
					}
					
					col.add(0.25f, 0.25f, 0.25f, 0);
				}

				if (pixelWideLines)
					line.scaleY = 2;
				else
					line.scaleY = distanceV * scaleY;
				line.y = (i - (data.getAmountOfVars() / 2)) * distanceV - offsetY; //+ RevVisGDX.singleton.camera.viewportHeight;
				line.y *= scaleY;

				if (!data.isFunctionLine(data.getVars().get(i))) {
					//					line.color.mul(0.5f);
				}
				if (line.scaleX < 0) {
					line.draw();
				}
			}

			signalsToCoords.put(data.getVars().get(i), line.y);
		}

		if (!hideGates) {
			float minY = 0, maxY = 0, minX = 0, maxX = 0;
			Color groupCol = Color.RED.cpy();
			float currentHue = 0;
			float currentSaturation = 1;
			String currentGroup = "";
			int groupCount = 0;
			for (int i = 0; i < data.getGates().size(); i++) {
				float xCoord = xCoordOnScreen(distanceH, i);

				if (!drawGroups) {
					if (xCoord > -RevVisGDX.singleton.camera.viewportWidth / 2 && xCoord < RevVisGDX.singleton.camera.viewportWidth / 2) {

						float maxDim = Math.min(distanceH * scaleX, distanceV * scaleY);


						if (drawVerticalLines) {
							minY = signalsToCoords.get(data.getGate(i).output);
							maxY = minY;

							for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
								minY = Math.min(minY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
								maxY = Math.max(maxY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
							}

							line.color = Color.BLACK.cpy();
							line.scaleX = 1; //RevVisGDX.singleton.camera.viewportWidth;
							line.scaleY = maxY - minY;
							line.x = xCoord;
							line.y = (maxY + minY) / 2; //+ RevVisGDX.singleton.camera.viewportHeight;
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
							targetGate.setDimensions(distanceH * scaleX, distanceV * scaleY);
							controlGate.setDimensions(distanceH * scaleX, distanceV * scaleY);
						}
						targetGate.color = Color.BLACK.cpy();
						targetGate.y = signalsToCoords.get(data.getGate(i).output);
						targetGate.x = xCoord;
						
						targetGate.draw();

						controlGate.color = Color.BLACK.cpy();
						for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
							controlGate.y = signalsToCoords.get(data.getGate(i).getInputs().get(j));
							controlGate.x = xCoord;
							
							controlGate.draw();
						}
					}
				} else {
					if (!(data.getGate(i).output.equals(currentGroup))) {
						maxX = xCoord - (distanceH / 2f) * scaleX;

						minY -= (distanceV / 2f) * scaleY;
						maxY += (distanceV / 2f) * scaleY;

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
						minX = xCoord - (distanceH / 2f) * scaleX;
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

	private float xCoordOnScreen(float distanceH, int i) {
		float xCoord = (i - (data.getGates().size() / 2)) * distanceH;
		xCoord += offsetX;
		xCoord *= scaleX;
		return xCoord;
	}
	
	public void shrinkToSquareAlignment() {
		float aspectRatio = ((RevVisGDX.singleton.camera.viewportWidth / (data.getGates().size() + 2)) / (RevVisGDX.singleton.camera.viewportHeight / (data.getAmountOfVars() + 2)));
		if (scaleY / aspectRatio < scaleX)
			scaleX = scaleY / aspectRatio;
		else
			scaleY = scaleX * aspectRatio;
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



}
