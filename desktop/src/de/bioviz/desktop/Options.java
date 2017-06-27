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

package de.bioviz.desktop;

import org.kohsuke.args4j.Option;

import java.io.File;

/**
 * Contains all options that can be accessed via the command line.
 *
 * @author Oliver Keszocze
 */
public class Options {

	/**
	 * This option selects if a file should be checked.
	 * If selected the file option is required.
	 */
	@Option(name = "-c", aliases = "--check",
			usage = "check .bio file for errors",
			metaVar = "file")
	public File check;

	/**
	 * This option selects if the usage should be printed.
	 */
	@Option(name = "-h", aliases = "--help", usage = "print help message")
	public boolean help = false;

	/**
	 * This options selects if the names of the authors should be printed on
	 * startup.
	 */
	@Option(name = "-a", aliases = "--authors", usage = "print authors")
	public boolean authors = false;

	/**
	 * This options selects if the version should be printed on startup.
	 */
	@Option(name = "--version", usage = "print version")
	public boolean version = false;

	/**
	 * This option allows to pass a file path to load on startup.
	 */
	@Option(name = "-f", aliases = "--file", usage = "Open .bio file", metaVar
			= "files")
	public File file;

}
