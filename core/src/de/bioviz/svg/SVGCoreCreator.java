package de.bioviz.svg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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
	 * @param folder The name of the folder containing the theme, relative to the assets
	 *               folder
	 * @brief Tells the manager where to find the svgs (i.e. .svg images).
	 * <p>
	 * The location is relative to the assets folder.
	 * <p>
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
	public String getSVGCode(TextureE type) {
		String svgCoreFile = baseFolder + "/" + svgCoreFolder + "/" + type + ".plain.svg";

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
	 * TODO Better error handling.
	 *
	 * @param type        The type of the core.
	 * @param fillColor   The fill color.
	 * @param strokeColor The stroke color.
	 * @return String containing svg core data.
	 */
	public String getSVGCode(TextureE type, Color fillColor, Color strokeColor) {
		String uncoloredCore = getSVGCode(type);
		String coloredCore = "";

		DocumentBuilderFactory factory =	DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		ByteArrayInputStream input;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();

			input = new ByteArrayInputStream(uncoloredCore.getBytes("UTF-8"));
			doc = builder.parse(input);

			doc.getDocumentElement().normalize();

			if(fillColor != null) {
				logger.debug("Trying to change the fillColor to {}.", fillColor.toString().substring(0, 6));
				// set new id for this node
				NodeList gList = doc.getElementsByTagName("g");
				if (gList.getLength() > 0) {
					Node node = gList.item(0);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element elem = (Element) node;
						elem.setAttribute("id", type.toString() + "-" + fillColor.toString().substring(0, 6));
					}
				} else {
					logger.debug("There was no group in the svg code.");
				}
			}
			if(strokeColor != null) {
				logger.debug("Trying to change the strokeColor to {}.", strokeColor.toString().substring(0, 6));
			}

			if(fillColor != null || strokeColor != null) {
				switch (type) {
					case Dispenser:
					case Sink:
					case Detector:
					case Start:
					case Blockage:
					case GridMarker:
					case Target:
						NodeList rectList = doc.getElementsByTagName("rect");
						if (rectList.getLength() > 0) {
							Node node = rectList.item(0);
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element elem = (Element) node;
								String style = elem.getAttribute("style");
								String styleAfter = "";
								for (String split : style.split(";")) {
									String styleType = split.split(":")[0];
									if (styleType.equals("fill") && fillColor != null) {
										styleAfter += styleType + ":#" + fillColor.toString().substring(0, 6) + ";";
									} else if (styleType.equals("stroke") && strokeColor != null) {
										styleAfter += styleType + ":#" + strokeColor.toString().substring(0, 6) + ";";
									} else {
										styleAfter += split + ";";
									}
								}

								elem.setAttribute("style", styleAfter);
							}
						}
					case Droplet:
						NodeList pathList = doc.getElementsByTagName("path");
						if (pathList.getLength() > 0){
							Node node = pathList.item(0);
							if(node.getNodeType() == Node.ELEMENT_NODE){
								Element elem = (Element) node;
								String style = elem.getAttribute("style");
								String styleAfter = "";
								for (String split : style.split(";")) {
									String styleType = split.split(":")[0];
									if (styleType.equals("fill") && fillColor != null) {
										styleAfter += styleType + ":#" + fillColor.toString().substring(0, 6) + ";";
									} else if (styleType.equals("stroke") && strokeColor != null) {
										styleAfter += styleType + ":#" + strokeColor.toString().substring(0, 6) + ";";
									} else {
										styleAfter += split + ";";
									}
								}

								elem.setAttribute("style", styleAfter);
							}
						}
						break;
					case AdjacencyMarker:
						break;
					case StepMarker:
						NodeList stepMarkerPathList = doc.getElementsByTagName("path");
						if (stepMarkerPathList.getLength() > 0) {
							Node node = stepMarkerPathList.item(0);
							logger.debug("Current Element: {}", node.getNodeName());
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element elem = (Element) node;
								logger.debug("Attribute: {}", elem.getAttribute("style"));
								String style = elem.getAttribute("style");
								String styleAfter = "";
								for (String split : style.split(";")) {
									String styleType = split.split(":")[0];
									if (styleType.equals("fill") && fillColor != null) {
										styleAfter += styleType + ":#" + fillColor.toString().substring(0, 6) + ";";
									} else if (styleType.equals("stroke") && strokeColor != null) {
										styleAfter += styleType + ":#" + strokeColor.toString().substring(0, 6) + ";";
									} else {
										styleAfter += split + ";";
									}
								}

								elem.setAttribute("style", styleAfter);
							}
						}
						break;
				}
			}

			coloredCore = getGroupFromDocument(doc);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return coloredCore;
	}

	private String getGroupFromDocument(Document doc) throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc.getElementsByTagName("g").item(0));
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		logger.debug("svg: {}", writer.toString());

		return writer.toString();
	}


}
