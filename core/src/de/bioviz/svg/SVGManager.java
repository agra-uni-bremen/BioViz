package de.bioviz.svg;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author Oliver Keszöcze
 * @brief Texture cache class.
 * <p>
 * This class manages texture themes. One can specify a folder in which the png
 * files that are loaded as textures are stored. These images are thenn either
 * loaded on demand or returned from the cache.
 */
public class SVGManager {

	private HashMap<SVGE, String> svgs = new HashMap<>();


	private String svgFolder;

	// TODO currently unused; use it after Jannis finally accepts some merge
	// requests
	private final String baseFolder = "";


	/**
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the assets
	 * 		folder
	 * @brief SVGManager loading the theme at the specified location.
	 * <p>
	 * The location is relative to the assets folder.
	 * @warning The folder name must not begin or end with a slash!
	 */
	public SVGManager(String folder) {
		setFolder(folder);
	}

	/**
	 * @brief SVGManager loading the default theme
	 */
	public SVGManager() {
		this("");
	}

	/**
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the assets
	 * 		folder
	 * @brief Tells the manager where to find the svgs (i.e. .svg images).
	 * <p>
	 * The location is relative to the assets folder.
	 * <p>
	 * Every time this method is called, all previously loaded textures are
	 * flushed.
	 * @note The name of the png files must match the names of the values
	 * specified in the TextureE enum
	 * @warning The folder name must not begin or end with a slash!
	 */
	public void setFolder(String folder) {
		svgs.clear();

		svgFolder = folder;
		for (SVGE s : SVGE.values()) {
			String svgCoreFile = baseFolder + "/" + svgFolder + "/" +
								 s +
								 ".core";
			Path svgCoreFilePath =
					Gdx.files.internal(svgCoreFile).file().toPath();

			try {
				String svgCore =
						new String(Files.readAllBytes(svgCoreFilePath));
				svgs.put(s, svgCore);
			} catch (IOException e) {
				// TODO log if stuff goes wrong
				e.printStackTrace();
			}


		}
	}


	public String getSVG(SVGE svg) {
		// TODO no error handling, let's see how far that gets us :|
		return svgs.get(svg);
	}

}
