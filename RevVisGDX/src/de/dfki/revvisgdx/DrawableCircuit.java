package de.dfki.revvisgdx;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Sprite;

import de.dfki.revlibReader.ReversibleCircuit;

public class DrawableCircuit implements Drawable {
	private ReversibleCircuit data;
	
	DrawableSprite line;
	DrawableSprite gate01;
	
	public float scaleX = 1;
	public float offsetX = 0;
	
	public DrawableCircuit(ReversibleCircuit toDraw) {
		this.data = toDraw;
		line = new DrawableSprite("data/BlackPixel.png");
		gate01 = new DrawableSprite("data/tgate01.png");
	}
	
	@Override
	public void draw() {
		
		float distanceV = RevVisGDX.singleton.camera.viewportHeight / (data.getAmountOfVars() + 2);
		HashMap<String, Float> signalsToCoords = new HashMap<String, Float>();
		
		for (int i = 0; i < data.getAmountOfVars(); i++) {
			line.scaleX = RevVisGDX.singleton.camera.viewportWidth;
			line.scaleY = 1;
			line.x = 0;
			line.y = (i - (data.getAmountOfVars() / 2)) * distanceV; //+ RevVisGDX.singleton.camera.viewportHeight;
			line.draw();
			
			signalsToCoords.put(data.getVars().get(i), line.y);
		}
		
		float distanceH = RevVisGDX.singleton.camera.viewportWidth / (data.getGates().size() + 2);
		
		for (int i = 0; i < data.getGates().size(); i++) {
			gate01.y = signalsToCoords.get(data.getGate(i).output);
			gate01.x = (i - (data.getGates().size() / 2)) * distanceH;
			gate01.x += offsetX;
			gate01.x *= scaleX;
			gate01.draw();
		}
	}

}
