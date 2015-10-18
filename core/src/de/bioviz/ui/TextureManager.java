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

	private HashMap<TextureE, TextureRegion> textures = new HashMap<>();

	private HashMap<TextureE, String> textureFileNames = new HashMap<>();

	private String textureFolder;

	TextureManager(String folder) {
		setFolder(folder);
	}

	public void setFolder(String folder) {
		textures.clear();

		textureFolder = folder;
		for (TextureE t : TextureE.values()) {
			textureFileNames.put(t,
								 textureFolder + "/" + t.toString() + ".png");
		}
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


			Texture t = new Texture(
					Gdx.files.internal(textureFileNames.get(texture)), true);
			t.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter
					.Linear);
			TextureRegion region =
					new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight());
			textures.put(texture, region);
			return region;
		}
	}

}
