/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.svg;

/**
 * @author malu
 *
 * Stores the export settings for the svg export.
 */
public final class SVGExportSettings {
	/** static svgExportSettings instance. */
	private static SVGExportSettings instance = null;

	/** export colored svg. */
	private boolean colorfulExport = false;
	/** export with information string. */
	private boolean informationString = false;
	/** export series of images for the entirety of the experiment. */
	private boolean exportSeries = false;

	/**
	 * private constructor.
	 */
	private SVGExportSettings() {
	}

	/**
	 * Get an instance of the svgExportSettings.
	 *
	 * @return the static instance
	 */
	public static SVGExportSettings getInstance() {
		if (instance == null) {
			instance = new SVGExportSettings();
		}
		return instance;
	}

	/**
	 * getter for colorfulExport.
	 * @return value of colorfulExport
	 */
	public boolean getColorfulExport() {
		return colorfulExport;
	}

	/**
	 * setter for colorfulExport.
	 * @param colorfulExport new value for colorfulExport
	 */
	public void setColorfulExport(final boolean colorfulExport) {
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
	public void setInformationString(final boolean informationString) {
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
	public void setExportSeries(final boolean exportSeries) {
		this.exportSeries = exportSeries;
	}
}
