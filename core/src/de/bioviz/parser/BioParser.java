package de.bioviz.parser;


import de.bioviz.parser.generated.Bio;
import de.bioviz.parser.generated.BioLexerGrammar;
import de.bioviz.structures.Biochip;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Static class providing a parser for BioGram files.
 * @author Oliver Keszocze
 */
public final class BioParser {

	/**
	 * The class-wide logger.
	 */
	private static Logger logger = LoggerFactory.getLogger(BioParser.class);

	/**
	 * Empty constructor to prevent instantiation.
	 */
	private BioParser() {	}


	/**
	 * Parses a file in BioGram format.
	 * @param file File to parse into a biochip
	 * @return Biochip as described in the file, null if not parsable
	 */
	public static Biochip parseFile(final File file) {
		String content;
		try {
			content = new String(Files.readAllBytes(Paths.get(file.toURI())));
		} catch (final IOException e) {
			logger.error("Failed to parse file \"{}\".", file);
			return null;
		}
		return parse(content);
	}

	/**
	 * Parses a String in BioGram format.
	 * @param inputString String containing a BioGram description.
	 * @return Biochip as described in the String, null if not parsable
	 */
	public static Biochip parse(final String inputString) {

		logger.trace("Parsing file of length {}", inputString.length());

		try {
			ANTLRInputStream input = new ANTLRInputStream(inputString);
			BioLexerGrammar lexer = new BioLexerGrammar(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			Bio parser = new Bio(tokens);
			parser.removeErrorListeners();
			BioErrorListener errorListener = new BioErrorListener();
			parser.addErrorListener(errorListener);
			ParseTree tree = parser.bio(); // parse everything

			if (errorListener.hasErrors()) {
				for (final String msg : errorListener.getErrors()) {
					logger.error(msg);
				}
				return null;
			}
			else {
				ParseTreeWalker walker = new ParseTreeWalker();
				// Walk the tree created during the parse, trigger callbacks
				BioParserListener listener = new BioParserListener();
				walker.walk(listener, tree);
				return listener.getBiochip();
			}
		} catch (final Exception e) {
			logger.error("Failed to parse file");
			e.printStackTrace();
			return null;
		}
	}

}
