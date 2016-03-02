package de.bioviz.svg;

/**
 * @author malu
 *
 * Stores the export settings for the svg export.
 */
public class SVGExportSettings {
	/** static svgExportSettings instance. */
	private static SVGExportSettings instance = null;

	/** export colored svg. */
	private boolean colorfulExport = false;
	/** export with information string. */
	private boolean informationString = false;
	/** export series of images for the entirety of the experiment. */
	private boolean exportSeries = false;

	/**
	 * Get an instance of the svgExportSettings.
	 *
	 * @return the static instance
	 */
	public static SVGExportSettings getInstance(){
		if(instance == null){
			instance = new SVGExportSettings();
		}
		return instance;
	}

	/**
	 * private constructor.
	 */
	private SVGExportSettings(){
	}

	/**
	 * getter for colorfulExport.
	 * @return value of colorfulExport
	 */
	public boolean getColorfulExport() {
		return colorfulExport;
	}

	/**
	 * setter for colorFullExport.
	 * @param colorFullExport new value for colorFullExport
	 */
	public void setColorfulExport(boolean colorfulExport) {
		this.colorfulExport = colorfulExport;
	}

	/**
	 * getter for informationString.
	 * @return value of informationString
	 */
	public boolean getInformationString() {
		return informationString;
	}

	/**
	 * setter for informationString.
	 * @param informationString new value for informationString
	 */
	public void setInformationString(boolean informationString) {
		this.informationString = informationString;
	}

	/**
	 * getter for exportSeries.
	 * @return value of exportSeries
	 */
	public boolean getExportSeries() {
		return exportSeries;
	}

	/**
	 * setter for exportSeries.
	 * @param exportSeries new value for exportSeries
	 */
	public void setExportSeries(boolean exportSeries) {
		this.exportSeries = exportSeries;
	}
}
