package de.bioviz.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that collects error messages.
 * <p>
 * This class also serves as hack to check whether any parsing problems
 * occurred. Most likely there is a better way to do so.
 *
 * @author Oliver Keszocze
 * @note Due to the fact that this class stores state (i.e. the errors that
 * occurred during the parse), it does not make sense to re-use instances of
 * this class. There may be use cases for that but most likely it is a bug.
 */
public class BioErrorListener extends BaseErrorListener {

	/**
	 * @brief List storing all error messages thrown during a parse
	 */
	private ArrayList<String> errors;

	/**
	 * Calls the superconstructor and initializes an empty list of errors.
	 */
	public BioErrorListener() {
		super();
		errors = new ArrayList<>();
	}

	@Override
	public void syntaxError(
			final Recognizer<?, ?> recognizer,
			final Object offendingSymbol,
			final int line,
			final int charPositionInLine,
			final String msg,
			final RecognitionException e) {
		List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		String stackMsg = "rule stack: " + stack;
		String lineMsg = "line " + line + ":" + charPositionInLine + ": " +
						 msg;
		errors.add(lineMsg + " " + stackMsg);
	}

	/**
	 * Checks whether errors occured.
	 *
	 * @return true if errors were present, false otherwise
	 * @note It does not make sense to call this method prior to parsing
	 */
	protected boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * @return The list of errors, may be empty (but not null)
	 */
	protected ArrayList<String> getErrors() {
		return errors;
	}
}
