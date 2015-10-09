package de.bioviz.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * This is a wrapper for the 2d drawing methods. 
 * 
 * @author jannis
 *
 */
public abstract class DrawableSprite implements Drawable {

	protected Sprite sprite;
	private static HashMap<String, TextureRegion> allTextures;
	private Color targetColor = Color.WHITE.cpy();
	private Color currentColor = Color.WHITE.cpy();
	private Color originColor = Color.WHITE.cpy();
	private long colorTransitionStartTime = 0, colorTransitionEndTime = 0, colorTransitionDuration = 500;
	float colorShiftDelay = 8f;
	private HashMap<Float, String> LevelOfDetailTextures = new HashMap<>();
	private String currentTextureName;
	
	public static final float defaultLODThreshold = 8f;
	
	public float x = 0, y = 0, scaleX = 1, scaleY = 1, rotation = 0;
	
	/**
	 * This constructor checks if the given texture has been loaded before and does so
	 * if that's not the case. A sprite is initialized accordingly.
	 * 
	 * @param textureFilename the texture to use
	 */
	public DrawableSprite(String textureFilename, float sizeX, float sizeY) {
		currentTextureName = textureFilename;
		if (allTextures == null) {
			allTextures = new HashMap<>();
		}
		this.addLOD(Float.MAX_VALUE, textureFilename);
		this.targetColor.a = 0;
		this.currentColor.a = 0;
	}

	private void initializeSprite(float sizeX, float sizeY, TextureRegion region) {
		sprite = new Sprite(region);
		sprite.setSize(sizeX, sizeY);
		sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight()/2f);
		sprite.setPosition(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
	}
	
	public DrawableSprite(String textureFilename) {
		this(textureFilename, 1, 1);
	}
	
	public void draw() {
		
		if (sprite== null) {
			TextureRegion region = loadTexture(currentTextureName);
			initializeSprite(1, 1, region);
		}
	
		// if LOD is set, enable LOD calculation and set
		// sprite accordingly
		if (this.LevelOfDetailTextures.size() > 0) {
			float bestLODFactor = Float.MAX_VALUE;
			boolean foundLOD = false;
			for (Float factor : LevelOfDetailTextures.keySet()) {
				if (factor >= this.scaleX && factor <= bestLODFactor) {
					bestLODFactor = factor;
					foundLOD = true;
				}
			}
			if (foundLOD) {
				currentTextureName = LevelOfDetailTextures.get(bestLODFactor);
			}

			this.setTexture();
		}
		
		update();
		
		this.sprite.setPosition(x-sprite.getWidth()/2f, y-sprite.getHeight()/2f);
		this.sprite.setScale(scaleX, scaleY);
		this.sprite.setRotation(rotation);
		this.sprite.setColor(currentColor);
		this.sprite.draw(BioViz.singleton.batch);
	}
	
	public void setDimensions(float dimX, float dimY) {
		this.scaleX = dimX / this.sprite.getWidth();
		this.scaleY = dimY / this.sprite.getHeight();
	}
	
	protected TextureRegion loadTexture(String textureFilename) {
		if (!allTextures.containsKey(textureFilename)) {
			Texture t = new Texture(Gdx.files.internal("images/"+textureFilename), true);
			t.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
			TextureRegion region = new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight());
			allTextures.put(textureFilename, region);
			return region;
		} else {
			return allTextures.get(textureFilename);
		}
	}
	
	/**
	 * Set the texture to something different.
	 * 
	 * @param textureFilename
	 */
	private void setTexture() {
		if (!this.allTextures.containsKey(currentTextureName)) {
			this.loadTexture(currentTextureName);
		}
		if (this.sprite != null) {
			this.sprite.setRegion(this.allTextures.get(currentTextureName));
		}
	}
	
	public void addLOD(float scaleFactorMax, String textureFilename) {
		loadTexture(textureFilename);
		this.LevelOfDetailTextures.put(scaleFactorMax, textureFilename);
	}
	public void removeLOD(float scaleFactorMax) {
		this.LevelOfDetailTextures.remove(scaleFactorMax);
	}
	
	public boolean isHovered() {
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		int resX = Gdx.graphics.getWidth();
		int resY = Gdx.graphics.getHeight();
		
		Rectangle viewport = BioViz.singleton.currentCircuit.getViewBounds();
		
		float viewMouseX = (((float)mouseX / (float)resX) * viewport.width + viewport.x);
		float viewMouseY = -(((float)mouseY / (float)resY) * viewport.height + viewport.y);
		
		if (viewMouseX > BioViz.singleton.currentCircuit.xCoordInGates(this.x) - 0.5f && viewMouseX < BioViz.singleton.currentCircuit.xCoordInGates(this.x) + 0.5f &&
			viewMouseY > BioViz.singleton.currentCircuit.yCoordInGates(this.y) - 0.5f && viewMouseY < BioViz.singleton.currentCircuit.yCoordInGates(this.y) + 0.5f) {
			return true;
		}
		return false;
	}
	
	protected void update() {		
		float transitionProgress = Math.max(0, Math.min(1, (float)(new Date().getTime() - colorTransitionStartTime) / (float)(colorTransitionEndTime - colorTransitionStartTime)));
		float totalProgress = (float)(-(Math.pow((transitionProgress - 1), 4)) + 1);
		
		currentColor = this.originColor.cpy().mul(1 - totalProgress).add(this.targetColor.cpy().mul(totalProgress));
	}

	public Color getColor() {
		return targetColor.cpy();
	}

	public void setColor(Color color) {
		if (!this.targetColor.equals(color)) {
			//this.color = color;
			originColor = this.currentColor;
			this.targetColor = color;
			Date d = new Date();
			this.colorTransitionStartTime = d.getTime(); 
			this.colorTransitionEndTime = d.getTime() + colorTransitionDuration;
		}
	}
}
