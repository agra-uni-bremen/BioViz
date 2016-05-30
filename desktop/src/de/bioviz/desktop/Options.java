package de.bioviz.desktop;

import org.kohsuke.args4j.Option;

import java.io.File;

/**
 * Contains all options that can be accessed via the command line.
 *
 * @author Oliver Keszocze
 */
public class Options {

	@Option(name = "-c", aliases = "--check",
			usage = "check .bio file for errors",
			metaVar = "file")
	public File check;

	@Option(name = "-h", aliases = "--help", usage = "print help message")
	public boolean help = false;

	@Option(name = "-a", aliases = "--authors", usage = "print authors")
	public boolean authors = false;

	@Option(name = "--version", usage = "print version")
	public boolean version = false;


}
