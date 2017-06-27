/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU
 * General Public License along with BioViz. If not, see <http://www.gnu.org/licenses/>.
 */

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
 * @author Oliver Keszocze
 *         <p/>
 *         This class manages texture themes. One can specify a folder in which
 *         the png files that are loaded as textures are stored. These images
 *         are thenn either loaded on demand or returned from the cache.
 */
public class TextureManager {
	/**
	 * Static logger for the whole class.
	 */
	static Logger logger = LoggerFactory.getLogger(TextureManager.class);

	/**
	 * Maps the texture types (e.g. sink) to the corresponding textures
	 */
	private HashMap<TextureE, TextureRegion> textures = new HashMap<>();

	/**
	 * Maps the texture types (e.g. sink) the file names the corresponding
	 * texture is stored in
	 */
	private HashMap<TextureE, String> textureFileNames = new HashMap<>();

	/**
	 * The folder that contains the texture files. This string is prepended to
	 * the file names stored in the textureFileNames map. Changing this String
	 * to something different switches the theme (also right now, only the
	 * default theme is available).
	 */
	private String textureFolder;


	/**
	 * The folder that contains the folders with the textures. This string is
	 * prepended to the String made by concatenating the textureFolder variable
	 * with a name taken from the textureFileNames map.
	 */
	private final String baseFolder = "images";


	/**
	 * TextureManager loading the theme at the specified location.
	 *
	 * @param folder The name of the folder containing the theme, relative
	 *               to the assets/images folder
	 *               The location is relative to the assets/images folder.
	 * @warning The folder name must not begin or end with a slash!
	 */
	TextureManager(final String folder) {
		setFolder(folder);
	}

	/**
	 * TextureManager loading the default theme.
	 */
	TextureManager() {
		this("default");
	}

	/**
	 * Tells the manager where to find the textures (i.e. png images).
	 * <p/>
	 * The location is relative to the assets/images folder.
	 * <p/>
	 * Every time this method is called, all previously loaded textures are
	 * flushed.
	 *
	 * @param folder The name of the folder containing the theme, relative to
	 *               the     assets/images folder
	 * @note The name of the png files must match the names of the values
	 * specified in the TextureE enum
	 * @warning The folder name must not begin or end with a slash!
	 */
	public void setFolder(final String folder) {
		textures.clear();

		textureFolder = folder;
		for (final TextureE t : TextureE.values()) {
			textureFileNames.put(t, baseFolder + "/" + textureFolder + "/" +
					t.toString() + ".png");
		}
	}


	/**
	 * Returns a FileHandle for the specified texture.
	 *
	 * @param texture The texture whose file is to be retrieved
	 * @return FileHandle to the file storing the texture
	 */
	public FileHandle getFileHandle(final TextureE texture) {
		logger.debug("retrieving file handle for " + texture + ": " +
				getFullTextureFilename(texture));
		return Gdx.files.internal(getFullTextureFilename(texture));
	}


	/**
	 * Returns the texture for the given texture name.
	 *
	 * @param texture The name of the texture to receive
	 * @return The Texture(Region) of the requested texture
	 * <p/>
	 * The texture is loaded if not already in memory.
	 */
	public TextureRegion getTexture(final TextureE texture) {


		if (textures.containsKey(texture)) {
			return textures.get(texture);
		} else {

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

	/**
	 * Returns the path to the file the provided texture is stored in.
	 *
	 * @param texture The texture whose corresponding file is requested
	 * @return full path to the texture file (a png file)
	 */
	private String getFullTextureFilename(final TextureE texture) {
		return baseFolder + "/" + textureFolder + "/" + texture + ".png";
	}

	/**
	 * Disposes all TextureRegions.
	 *
	 * All regions that are  stored in this.textures are disposed.
	 */
	void dispose() {
		for (final TextureRegion tr : this.textures.values()) {
			tr.getTexture().dispose();
		}
	}
}
