package de.bioviz.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * @author Oliver Keszöcze
 */
public class TextureManager {

	private HashMap<TextureE, TextureRegion> textures;

	private HashMap<TextureE, String> textureFileNames;

	private String textureFolder;

	TextureManager(String folder) {
		textureFolder = folder;
	}


	public TextureRegion getTexture(TextureE texture) {


		if (textures.containsKey(texture)) {
			return textures.get(texture);
		}
		else {

			if (!textureFileNames.containsKey(texture)) {
				throw new RuntimeException(
						"Texture " + texture + " has no associated filename");
			}

			String textureFilename = textureFolder + "/" +
									 textureFileNames.get(texture);

			Texture t = new Texture(Gdx.files.internal(textureFilename), true);
			t.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
			TextureRegion region = new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight());
			textures.put(texture, region);
			return region;
		}
	}

}
