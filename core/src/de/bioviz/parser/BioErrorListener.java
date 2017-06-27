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
