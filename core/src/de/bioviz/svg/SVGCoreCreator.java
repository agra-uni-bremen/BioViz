package de.bioviz.svg;

import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Maximilian Luenert
 */
public class SVGCoreCreator {

	private static Logger logger = LoggerFactory.getLogger(SVGCoreCreator.class);

	private String svgCoreFolder = "";

	private String baseFolder = "images";

	/**
	 * Creates a new SVGCoreCreator.
	 */
	public SVGCoreCreator() {

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
		svgCoreFolder = folder;
	}

	/**
	 * Return the svg core without color.
	 *
	 * @param type The type of the core.
	 * @return
	 */
	public String createSVGCore(SVGE type) {
		String svgCoreFile = baseFolder + "/" + svgCoreFolder + "/" + type + ".core";

		logger.debug("Loading SVG core for {}", svgCoreFile);

		Path svgCoreFilePath = Gdx.files.internal(svgCoreFile).file().toPath();
		String svgCore = "";
		try {
			svgCore = new String(Files.readAllBytes(svgCoreFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return svgCore;
	}

	/**
	 * Returns a string containing the svg core data with the given fill and stroke color.
	 *
	 * @param type        The type of the core.
	 * @param fillColor   The fill color.
	 * @param strokeColor The stroke color.
	 * @return String containing svg core data.
	 */
	public String createSVGCore(SVGE type, Color fillColor, Color strokeColor) {
		return "";
	}


}
