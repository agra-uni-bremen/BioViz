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

	public DrawableCircuit(ReversibleCircuit toDraw) {
		this.data = toDraw;
		line = new DrawableSprite("data/BlackPixel.png");
		gate01 = new DrawableSprite("data/tgate01.png");
		gate02 = new DrawableSprite("data/tgate02.png");
	}

	@Override
	public void draw() {

		float distanceV = RevVisGDX.singleton.camera.viewportHeight / (data.getAmountOfVars() + 2);
		HashMap<String, Float> signalsToCoords = new HashMap<String, Float>();

		for (int i = 0; i < data.getAmountOfVars(); i++) {
			line.scaleX = RevVisGDX.singleton.camera.viewportWidth;
			line.scaleY = 2;
			line.x = 0;
			line.y = (i - (data.getAmountOfVars() / 2)) * distanceV; //+ RevVisGDX.singleton.camera.viewportHeight;
			if (data.isInputOnly(data.getVars().get(i)))
				line.color = Color.GREEN.cpy();
			else if (data.isTargetOnly(data.getVars().get(i)))
				line.color = Color.RED.cpy();
			else
				line.color = Color.BLACK.cpy();
			line.draw();

			signalsToCoords.put(data.getVars().get(i), line.y);
		}

		float distanceH = RevVisGDX.singleton.camera.viewportWidth / (data.getGates().size() + 2);

		float minY, maxY;
		for (int i = 0; i < data.getGates().size(); i++) {
			float xCoord = (i - (data.getGates().size() / 2)) * distanceH;
			xCoord += offsetX;
			xCoord *= scaleX;

			if (xCoord > -RevVisGDX.singleton.camera.viewportWidth / 2 && xCoord < RevVisGDX.singleton.camera.viewportWidth / 2) {

				float maxDim = Math.min(distanceH * scaleX, distanceV);

				minY = signalsToCoords.get(data.getGate(i).output);
				maxY = minY;
				
				for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
					minY = Math.min(minY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
					maxY = Math.max(maxY, signalsToCoords.get(data.getGate(i).getInputs().get(j)));
				}
				line.color = Color.GRAY.cpy();
				line.scaleX = 1; //RevVisGDX.singleton.camera.viewportWidth;
				line.scaleY = maxY - minY;
				line.x = xCoord;
				line.y = (maxY + minY) / 2; //+ RevVisGDX.singleton.camera.viewportHeight;
				line.draw();
				
				gate01.y = signalsToCoords.get(data.getGate(i).output);
				gate01.x = xCoord;
				gate01.setDimensions(maxDim, maxDim);
				gate01.draw();
				

				for (int j = 0; j < data.getGate(i).getInputs().size(); j++) {
					gate02.y = signalsToCoords.get(data.getGate(i).getInputs().get(j));
					gate02.x = xCoord;
					gate02.setDimensions(maxDim, maxDim);
					gate02.draw();
				}
			}
		}
	}

}
