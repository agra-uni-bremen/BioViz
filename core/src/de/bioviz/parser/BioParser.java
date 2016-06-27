package de.bioviz.parser;


import de.bioviz.parser.generated.Bio;
import de.bioviz.parser.generated.BioLexerGrammar;
import de.bioviz.structures.Biochip;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Static class providing a parser for BioGram files.
 *
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
	private BioParser() { }


	/**
	 * Parses a file in BioGram format.
	 *
	 * @param file
	 * 		File to parse into a biochip
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
	 *
	 * @param inputString
	 * 		String containing a BioGram description.
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

			parseAnnotations(input);

			if (errorListener.hasErrors()) {
				for (final String msg : errorListener.getErrors()) {
					logger.error(msg);
				}
				return null;
			} else {
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

	/**
	 * Parses the annotations in a file.
	 * @param input an ANTLRInputStream
	 * @return A List of Strings containing the annotations.
	 */
	private static List<String> parseAnnotations(final ANTLRInputStream input) {
		BioLexerGrammar lexer = new BioLexerGrammar(input);
		// @keszocze this one is needed. I don't know why.
		lexer.reset();
		CommonTokenStream annotationStream = new CommonTokenStream(lexer,
				BioLexerGrammar.ANNOTATIONS);
		List<String> annoTokens = new ArrayList<>();

		// this one gets everything that is in the stream.
		annotationStream.getText();
		// now we can use size() to run over the tokens
		for (int i = 0; i < annotationStream.size(); i++) {
			Token token = annotationStream.get(i);
			// and check here if the token is on the right channel
			if (token.getChannel() == BioLexerGrammar.ANNOTATIONS) {
				logger.debug("Reading: " + token.getText());
				annoTokens.add(token.getText());
			}
		}

		return annoTokens;
	}

}
