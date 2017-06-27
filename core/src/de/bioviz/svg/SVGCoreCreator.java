package de.bioviz.svg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.ui.DrawableDroplet;
import de.bioviz.ui.DrawableField;
import de.bioviz.ui.TextureE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author Maximilian Luenert
 */
class SVGCoreCreator {

	/**
	 * logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(SVGCoreCreator.class);

	/**
	 * the length of the color string without alpha.
	 */
	private static final int COLOR_DIGITS = 6;

	/**
	 * path of the svg core folder.
	 */
	private String svgCoreFolder = "";

	/**
	 * path of the image folder.
	 */
	private String baseFolder = "images";

	/**
	 * Creates a new SVGCoreCreator.
	 */
	SVGCoreCreator(Document doc) {
		// default constructor
		this.doc = doc;
	}

	/**
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the assets
	 * 		folder
	 * @brief Tells the manager where to find the svgs (i.e. .svg images).
	 * <p>
	 * The location is relative to the assets folder.
	 * <p>
	 * @warning The folder name must not begin or end with a slash!
	 */
	void setFolder(final String folder) {
		svgCoreFolder = folder;
	}

	/**
	 * Sets the Document to use.
	 *
	 * @param doc The Document to use
	 */
	void setDocument(final Document doc) {
		this.doc = doc;
	}

	private Document doc;

	/**
	 * Return the svg core without color.
	 *
	 * @param type
	 * 		The type of the core.
	 * @return the svg code for the given type as a string
	 */
	private String getSVGFileAsString(final TextureE type) {

		String svgCoreFile =
				baseFolder + "/" + svgCoreFolder + "/" + type + ".plain.svg";


		LOGGER.debug("[SVG] Loading SVG core for {}", svgCoreFile);

		FileHandle svgCoreFilePath = Gdx.files.internal(svgCoreFile);
		return svgCoreFilePath.readString();
	}

	/**
	 * Returns a string containing the svg core data with the given fill and
	 * stroke color.
	 *
	 * @param type
	 * 		The type of the core.
	 * @param fillColor
	 * 		The fill color.
	 * @param strokeColor
	 * 		The stroke color.
	 * @return String containing svg core data.
	 */
	Element getSVGCode(final TextureE type, final Color fillColor,
							 final Color strokeColor) {
		String uncoloredCore = getSVGFileAsString(type);

		if(uncoloredCore == null){
			LOGGER.error("[SVG] Could not load SVG core file.");
			return null;
		}

		Element coloredCore = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		ByteArrayInputStream input;

		try {
			builder = factory.newDocumentBuilder();

			input = new ByteArrayInputStream(uncoloredCore.getBytes("UTF-8"));
			doc = builder.parse(input);

			doc.getDocumentElement().normalize();

			// every svg should contain a group which contains the needed
			// elements
			Node group = null;
			NodeList gList = doc.getElementsByTagName("g");
			if (gList.getLength() > 0) {
				group = gList.item(0);
			} else {
				LOGGER.debug("[SVG] There was no group in the svg code.");
				return "";
			}

			if (fillColor != null) {
				String fillColorStr = SVGUtils.colorToSVG(fillColor);
				LOGGER.debug("[SVG] Changing fillColor to {}.",
							 fillColorStr);
				// set new id for this node if there is a fillColor
				if (group.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) group;
					elem.setAttribute("id", SVGUtils.generateColoredID(
							type.toString(),
							fillColor));
				}
			}
			if (strokeColor != null) {
				String strokeColorStr = SVGUtils.colorToSVG(strokeColor);
				LOGGER.debug("[SVG] Changing strokeColor to {}.",
							 strokeColorStr);
			}

			if (fillColor != null || strokeColor != null) {
				switch (type) {
					case Dispenser:
					case Sink:
					case Detector:
					case Start:
					case Target:
					case GridMarker:
					case Heater:
					case Magnet:
						// change fillColor of the rectangle
						setStyleForElement((Element) group, "rect",
										   fillColor, strokeColor);
						break;
					case Blockage:
						// change fillColor of the rectangle
						setStyleForElement((Element) group, "rect",
										   fillColor, strokeColor);
						// change fillColor of the circle
						setStyleForElement((Element) group, "circle",
										   fillColor, strokeColor);

						break;
					case Droplet:
						// change fillColor of the first path element
						setStyleForElement((Element) group, "path",
										   fillColor, strokeColor);
						break;
					case AdjacencyMarker:
						break;
					case StepMarker:
						// change fillColor of the first path element
						setStyleForElement((Element) group, "path",
										   fillColor, strokeColor);
						break;
					default:
						break;
				}
			}

			coloredCore = getGroupFromDocument(doc);

		} catch (final UnsupportedEncodingException e) {
			LOGGER.error("[SVG] XML uses unknown encoding.");
			LOGGER.error(e.getMessage());
		} catch (final IOException e) {
			LOGGER.error("[SVG] Could not read the xml.");
			LOGGER.error(e.getMessage());
		} catch (final SAXException e) {
			LOGGER.error("[SVG] Error while parsing xml.");
			LOGGER.error(e.getMessage());
		} catch (final ParserConfigurationException e) {
			LOGGER.error("[SVG] Error in xml parser configuration.");
			LOGGER.error(e.getMessage());
		}

		return coloredCore;
	}

	/**
	 * Get the svg code for an arrowhead with the specified color.
	 *
	 * @param color
	 * 		the color for the arrowhead
	 * @param id
	 * 		the id for the arrowHead
	 * @return a svg marker string
	 */
	public Element getArrowHead(final String id, final Color
			color) {
		Element elem = doc.createElement("marker");
		elem.setAttribute("id", "TestNode");
		elem.setAttribute("markerWidth", String.valueOf(10));
		elem.setAttribute("markerHeight", String.valueOf(10));
		elem.setAttribute("refX", String.valueOf(7));
		elem.setAttribute("refY", String.valueOf(3));
		elem.setAttribute("orient", "auto");
		elem.setAttribute("markerUnits", "strokeWidth");
		elem.appendChild(doc.createTextNode(
				"<path d=\"M0,0 C 1,1 1,5 0,6 L9,3 z\" " +
						"fill=\"#" + SVGUtils.colorToSVG(color) + "\"/>"
		));

		return elem;

//		return "<marker id=\"" + id + "\" " +
//			   "markerWidth=\"10\" " + "markerHeight=\"10\" " +
//			   "refX=\"7\" " + "refY=\"3\"	" +
//			   "orient=\"auto\" markerUnits=\"strokeWidth\">\n\t<path " +
//			   "d=\"M0," +
//			   "0" +
//			   " C 1,1" +
//			   " 1,5 0,6 L9,3  z\" " +
//			   "fill=\"#" + SVGUtils.colorToSVG(color) + "\" />\n</marker>\n";
	}

	/**
	 * Returns a string representing a linear gradient definition with the
	 * given
	 * id, direction and colors.
	 *
	 * @param id
	 * 		of the resulting gradient
	 * @param dir
	 * 		direction of the resulting gradient
	 * @param color
	 * 		first color of the resulting gradient
	 * @return a string
	 */
	private Element getSVGLinearGradient(final String id, final GradDir dir,
									   final
									   Color
											   color) {

		int x1 = dir.hasOrientation(Point.WEST) ? 1 : 0;
		int x2 = dir.hasOrientation(Point.EAST) ? 1 : 0;

		int y1 = dir.hasOrientation(Point.NORTH) ? 1 : 0;
		int y2 = dir.hasOrientation(Point.SOUTH) ? 1 : 0;

		final int colorMult = 255;

		Element elem = doc.createElement("linearGradient");
		elem.setAttribute("id", id);
		elem.setAttribute("x1", String.valueOf(x1));
		elem.setAttribute("y1", String.valueOf(y1));
		elem.setAttribute("x2", String.valueOf(x2));
		elem.setAttribute("y2", String.valueOf(y2));

		Element stopElem = doc.createElement("stop");
		stopElem.setAttribute("offset", "0%");
		stopElem.setAttribute("style", "stop-color:rgb(" +
													color.r * colorMult +
		 								"," + color.g * colorMult +
										"," + color.b * colorMult + ");"
										);
		stopElem.setAttribute("stop-opacity", "0");

		Element stopElem2 = doc.createElement("stop");
		stopElem2.setAttribute("offset", "100%");
		stopElem2.setAttribute("style", "stop-color:rgb(" +
													color.r * colorMult +
										"," + color.g * colorMult +
										"," + color.b * colorMult +");"
						);
		stopElem2.setAttribute("stop-opacity", "0.8");

		elem.appendChild(stopElem);
		elem.appendChild(stopElem2);

		return elem;

//		String begin = "<linearGradient id=\"" + id + "\" x1=\"" + x1 + "\" " +
//					   "y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 +
//					   "\" >\n";
//		String offset1 = "<stop offset=\"0%\" " + "style=\"stop-color:rgb(" +
//
//						 color.r * colorMult + "," + color.g * colorMult +
//						 "," + color.b * colorMult + ");" +
//						 "stop-opacity:0\" />\n";
//		String offset2 = "<stop offset=\"100%\" style=\"stop-color:rgb(" +
//						 color.r * colorMult + "," + color.g * colorMult +
//						 "," + color.b * colorMult + ");" +
//						 "stop-opacity:0.8\" />\n";
//		String end = "</linearGradient>\n";
//		return begin + offset1 + offset2 + end;
	}

	/**
	 * Sets the style tag for the first tag occurrence.
	 *
	 * @param element
	 * 		xml top node
	 * @param tagName
	 * 		the name of the tag to modify
	 * @param fillColor
	 * 		the new fillColor
	 * @param strokeColor
	 * 		the new strokeColor
	 */
	private void setStyleForElement(final Element element,
									final	String tagName, final Color fillColor,
									final	Color strokeColor) {

		NodeList elements = element.getElementsByTagName(tagName);
		if (elements.getLength() > 0) {
			Node node = elements.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				String style = elem.getAttribute("style");
				StringBuilder styleAfter = new StringBuilder();
				for (final String split : style.split(";")) {
					String styleType = split.split(":")[0];
					if ("fill".equals(styleType) && fillColor != null) {
						styleAfter.append(styleType).append(":#").
								append(fillColor.toString().substring(0, COLOR_DIGITS)).
								append(";");
					} else if ("stroke".equals(styleType) &&
							   strokeColor != null) {
						styleAfter.append(styleType).append(":#").
								append(strokeColor.toString().substring(0, COLOR_DIGITS)).
								append(";");

					} else {
						styleAfter.append(split).append(";");
					}
				}

				elem.setAttribute("style", styleAfter.toString());
			}
		}
	}

	/**
	 * Create string from xml representation.
	 *
	 * @param doc
	 * 		The xml document.
	 * @return String representing the xml document or null if the
	 * transformation failed.
	 */
	private String getGroupFromDocument(final Document doc) {
		TransformerFactory tFactory;
		Transformer transformer;
		StringWriter writer;
		try {
			tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
										  "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc.getElementsByTagName("g").
					item(0));
			writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
		} catch (final TransformerException e) {
			LOGGER.error("[SVG] Creating string from XML failed.");
			return null;
		}

		return writer.toString();
	}

	/**
	 * Puts the svg code for a field into the given map.
	 *
	 * @param svgs
	 * 		the map
	 * @param field
	 * 		the field
	 */
	void appendFieldSVG(final Map<String, String> svgs,
							   final DrawableField
									   field) {

		final String key = SVGUtils.generateColoredID(field.getDisplayValues()
															  .getTexture()
															  .toString(),
													  SVGUtils
															  .getUnhoveredColor(
																	  field));
		// don't create the svg core code twice
		if (!svgs.containsKey(key)) {
			svgs.put(key,
					 getSVGCode(field.getDisplayValues().getTexture(),
								SVGUtils.getUnhoveredColor(field), null));
		}
	}

	/**
	 * Puts the svg code for a gradient into the given map.
	 *
	 * @param svgs
	 * 		the map
	 * @param net
	 * 		the net
	 * @param dir
	 * 		the direction of the gradient
	 */
	void appendGradSVG(final Map<String, String> svgs,
							  final Net net, final
							  GradDir dir) {
		final String key =
				SVGUtils.generateColoredID("Gradient-" + dir.toString(),
										   SVGUtils.getNetColor(net));
		if (!svgs.containsKey(key)) {
			svgs.put(key, getSVGLinearGradient(key, dir,
											   SVGUtils.getNetColor(net)));
		}
	}

	/**
	 * Puts the svg code for a droplet into the given map.
	 *
	 * @param svgs
	 * 		the map to insert into
	 * @param drop
	 * 		the droplet
	 */
	void appendDropletSVG(final Map<String, String> svgs,
								 final
								 DrawableDroplet
										 drop) {
		final String key =
				SVGUtils.generateColoredID("Droplet", drop.getColor());
		// don't create the svg core code twice
		if (!svgs.containsKey(key)) {
			svgs.put(key, getSVGCode(TextureE.Droplet, drop.getColor(), null));
		}
	}

	/**
	 * Puts the svg code for arrowheads into the given map.
	 *
	 * @param svgs
	 * 		the map
	 * @param dropColor
	 * 		the dropletColor
	 */
	void appendArrowheads(final Map<String, String> svgs,
								 final Color
										 dropColor) {
		final Color[] colors =
				{
						SVGUtils.getLighterLongNetIndicatorColor(dropColor),
						SVGUtils.getDarkerLongNetIndicatorColor(dropColor)
				};
		for (final Color color : colors) {
			final String key = SVGUtils.generateColoredID("ArrowHead", color);
			if (!svgs.containsKey(key)) {
				svgs.put(key, getArrowHead(key, color));
			}
		}
	}

	/**
	 * Puts the svg code for a sourceTargetArrowHead into the given map.
	 *
	 * @param svgs
	 * 		the map
	 */
	void appendSourceTargetArrowHead(final Map<String, String> svgs) {
		final Color color = Color.BLACK;
		final String key = SVGUtils.generateColoredID("ArrowHead", color);
		if (!svgs.containsKey(key)) {
			svgs.put(key, getArrowHead(key, color));
		}
	}

	/**
	 * Puts the svg code of a route into the given map.
	 *
	 * @param svgs
	 * 		the map to append to
	 * @param drop
	 * 		the droplet
	 */
	void appendRoute(final Map<String, String> svgs,
							final DrawableDroplet
									drop) {
		final Color routeColor = drop.route.getColor();
		final String key = SVGUtils.generateColoredID("StepMarker",
													  routeColor);
		if (!svgs.containsKey(key)) {
			svgs.put(key,
					 getSVGCode(TextureE.StepMarker, routeColor, null));
		}
	}

}

