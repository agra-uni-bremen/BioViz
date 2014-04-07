package de.dfki.revvisgdx;

import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DrawableSprite implements Drawable {

	private Sprite sprite;
	private HashMap<String, Texture> allTextures = new HashMap<String, Texture>();
	public Color color = Color.BLACK.cpy();
	
	public float x = 0, y = 0, scaleX = 1, scaleY = 1;
	
	public DrawableSprite(String textureFilename) {
		TextureRegion region = loadTexture(textureFilename);
		Texture currentTexture = region.getTexture();
		sprite = new Sprite(region);
		sprite.setSize(currentTexture.getWidth(), currentTexture.getHeight());
		sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight()/2f);
		sprite.setPosition(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
	}
	
	public void draw() {
		this.sprite.setPosition(x-sprite.getWidth()/2f, y-sprite.getHeight()/2f);
		this.sprite.setScale(scaleX, scaleY);
		this.sprite.setColor(color);
		this.sprite.draw(RevVisGDX.singleton.batch);
	}
	
	public void setDimensions(float dimX, float dimY) {
		this.scaleX = dimX / this.sprite.getWidth();
		this.scaleY = dimY / this.sprite.getHeight();
	}
	
	private TextureRegion loadTexture(String textureFilename) {
		Texture t = new Texture(Gdx.files.internal(textureFilename));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight());
		allTextures.put(textureFilename, t);
		return region;
	}
	
	public void setTexture(String textureFilename) {
		if (!this.allTextures.containsKey(textureFilename))
			this.loadTexture(textureFilename);
		this.sprite.setRegion(this.allTextures.get(textureFilename));
	}
}
