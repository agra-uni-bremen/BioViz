package de.bioviz.svg;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.ui.Colors;
import de.bioviz.ui.DrawableCircuit;
import de.bioviz.ui.DrawableField;

/**
 * @author Maximilian Luenert
 */
public class SVGUtils {

	private final static Color DIFF_COLOR = new Color(0.2f, 0.2f, 0.2f, 0);

	private final static SVGExportSettings SVG_EXPORT_SETTINGS =
			SVGExportSettings.getInstance();

	/**
	 * Converts a libGDX color to a SVG-usable format.
	 *
	 * What it basically does is to throw away the last to characters, i.e.
	 * the alpha channel.
	 *
	 * @param c Color to transform
	 * @return Color in format to be used by SVG
	 */
	public static String colorToSVG(final Color c) {
		return c.toString().substring(0, 6);
	}

	/**
	 * Gets the color of a net.
	 * @param net
	 * 			the net
	 * @return
	 * 			the color of the net or Color.DARK_GREY if the colorfulExport is off
	 */
	public static Color getNetColor(Net net){
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return net.getColor().buildGdxColor();
		} else {
			return Color.DARK_GRAY;
		}
	}

	/**
	 * Get the color for a lighter longNetIndicator.
	 *
	 * @param dropColor
	 * 			the droplet to get the base color
	 * @return
	 *			the color for the lighter arrowHead or Color.DARK_GREY if the
	 *			colorfulExport is off
	 */
	public static Color getLighterLongNetIndicatorColor(final Color dropColor){
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return dropColor.cpy().sub(DIFF_COLOR);
		}
		else {
			return Color.BLACK;
		}
	}

	/**
	 * Get the color for a darker longNetIndicator.
	 *
	 * @param dropColor
	 * 			the droplet to get the base color
	 * @return
	 * 			the color for the darker arrowHead or Color.DARK_GREY if the
	 *			colorfulExport is off
	 */
	public static Color getDarkerLongNetIndicatorColor(final Color dropColor) {
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return dropColor.cpy().add(DIFF_COLOR);
		}
		else {
			return Color.BLACK;
		}
	}

	/**
	 * Creates an ID consisting of a base part and the given color.
	 * @param baseName The part of thename in front of the '-'
	 * @param c The color that will be put after the '-'
	 * @return "<baseName>-<color>"
	 */
	public static String generateColoredID(final String baseName,
																				 final Color c) {
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return baseName + "-" + colorToSVG(c);
		} else {
			return baseName;
		}
	}

	/**
	 * Transforms a point to svgCoordinates.
	 *
	 * @param point the point to transform
	 * @return Point with SVG coordinates
	 */
	public static Point toSVGCoords(final Point point,
																	final DrawableCircuit circuit,
																	final int coordinateMultiplier) {
		int yCoord = -point.snd + circuit.getData().getMaxCoord().snd;
		int xCoord = point.fst;

		xCoord *= coordinateMultiplier;
		yCoord *= coordinateMultiplier;

		return new Point(xCoord, yCoord);
	}

	/**
	 * Returns the color of the field in an unhovered state.
	 *
	 * @param field the field
	 * @return the unhovered color
	 */
	public static Color getUnhoveredColor(DrawableField field){
		Color fieldCol = field.getColor().cpy();
		if(field.isHovered()){
			fieldCol = fieldCol.sub(Colors.HOVER_DIFF_COLOR);
		}
		return fieldCol;
	}
}
