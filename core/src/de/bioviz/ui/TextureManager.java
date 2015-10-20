package de.bioviz.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Oliver Keszöcze
 * @brief Texture cache class.
 * <p>
 * This class manages texture themes. One can specify a folder in which the png
 * files that are loaded as textures are stored. These images are thenn either
 * loaded on demand or returned from the cache.
 */
public class TextureManager {

	private HashMap<TextureE, TextureRegion> textures = new HashMap<>();

	private HashMap<TextureE, String> textureFileNames = new HashMap<>();

	private String textureFolder;

	private final String baseFolder = "images";

	static Logger logger = LoggerFactory.getLogger(TextureManager.class);

	/**
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the
	 * 		assets/images folder
	 * @brief TextureManager loading the theme at the specified location.
	 * <p>
	 * The location is relative to the assets/images folder.
	 * @warning The folder name must not begin or end with a slash!
	 */
	TextureManager(String folder) {
		setFolder(folder);
	}

	/**
	 * @brief TextureManager loading the default theme
	 */
	TextureManager() {
		this("default");
	}

	/**
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the
	 * 		assets/images folder
	 * @brief Tells the manager where to find the textures (i.e. png images).
	 * <p>
	 * The location is relative to the assets/images folder.
	 * <p>
	 * Every time this method is called, all previously loaded textures are
	 * flushed.
	 * @note The name of the png files must match the names of the values
	 * specified in the TextureE enum
	 * @warning The folder name must not begin or end with a slash!
	 */
	public void setFolder(String folder) {
		textures.clear();

		textureFolder = folder;
		for (TextureE t : TextureE.values()) {
			textureFileNames.put(t, getFullTextureFilename(t));
		}
	}


	/**
	 * @brief Returns a FileHandle for the specified texture
	 * @param texture The texture whose file is to be retrieved
	 * @return FileHandle to the file storing the texture
	 */
	public FileHandle getFileHandle(TextureE texture) {
		logger.debug("retrieving file handle for " + texture + ": " + getFullTextureFilename(texture));
		return Gdx.files.internal(getFullTextureFilename(texture));
	}


	/**
	 * @param texture
	 * 		The name of the texture to receive
	 * @return The Texture(Region) of the requested texture
	 * @brief Returns the texture for the given texture name.
	 * <p>
	 * The texture is loaded if not already in memory.
	 */
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
	
	private String getFullTextureFilename(TextureE texture) {
		return baseFolder + "/" + textureFolder + "/" +
				texture +
				".png";
	}

}
