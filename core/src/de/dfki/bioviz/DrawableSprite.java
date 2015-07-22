package de.dfki.bioviz;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This is a wrapper for the 2d drawing methods. 
 * 
 * @author jannis
 *
 */
public abstract class DrawableSprite implements Drawable {

	protected Sprite sprite;
	private HashMap<String, TextureRegion> allTextures = new HashMap<>();
	public Color color = Color.WHITE.cpy();
	private HashMap<Float, String> LevelOfDetailTextures = new HashMap<>();
	private String defaultTextureName;
	
	public float x = 0, y = 0, scaleX = 1, scaleY = 1, rotation = 0;
	
	/**
	 * This constructor checks if the given texture has been loaded before and does so
	 * if that's not the case. A sprite is initialized accordingly.
	 * 
	 * @param textureFilename the texture to use
	 */
	public DrawableSprite(String textureFilename, float sizeX, float sizeY) {
		defaultTextureName = textureFilename;
		TextureRegion region = loadTexture(textureFilename);
		Texture currentTexture = region.getTexture();
		sprite = new Sprite(region);
		sprite.setSize(sizeX, sizeY);
		sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight()/2f);
		sprite.setPosition(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
	}
	
	public DrawableSprite(String textureFilename) {
		this(textureFilename, 1, 1);
	}
	
	public void draw() {
	
		// if LOD is set, enable LOD calculation and set
		// sprite accordingly
		if (this.LevelOfDetailTextures.size() > 0) {
			float bestLODFactor = 0;
			boolean foundLOD = false;
			for (Float factor : LevelOfDetailTextures.keySet()) {
				if (factor >= this.scaleX && factor >= bestLODFactor) {
					bestLODFactor = factor;
					foundLOD = true;
				}
			}
			if (foundLOD)
				this.setTexture(LevelOfDetailTextures.get(bestLODFactor));
			else
				this.setTexture(defaultTextureName);
		}
		this.sprite.setPosition(x-sprite.getWidth()/2f, y-sprite.getHeight()/2f);
		this.sprite.setScale(scaleX, scaleY);
		this.sprite.setRotation(rotation);
		this.sprite.setColor(color);
		this.sprite.draw(BioViz.singleton.batch);
	}
	
	public void setDimensions(float dimX, float dimY) {
		this.scaleX = dimX / this.sprite.getWidth();
		this.scaleY = dimY / this.sprite.getHeight();
	}
	
	private TextureRegion loadTexture(String textureFilename) {
		Texture t = new Texture(Gdx.files.internal(textureFilename));
		t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion region = new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight());
		allTextures.put(textureFilename, region);
		return region;
	}
	
	/**
	 * Set the texture to something different.
	 * 
	 * @param textureFilename
	 */
	public void setTexture(String textureFilename) {
		if (!this.allTextures.containsKey(textureFilename)) {
			this.loadTexture(textureFilename);
		}
		this.sprite.setRegion(this.allTextures.get(textureFilename));
	}
	
	public void addLOD(float scaleFactorMax, String textureFilename) {
		loadTexture(textureFilename);
		this.LevelOfDetailTextures.put(scaleFactorMax, textureFilename);
	}
}
