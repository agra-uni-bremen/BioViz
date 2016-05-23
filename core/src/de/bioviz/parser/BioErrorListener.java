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
 *
 * This class also serves as hack to check whether any parsing problems occurred.
 * Most likely there is a better way to do so.
 *
 * @note Due to the fact that this class stores state (i.e. the errors that occurred during
 * the parse), it does not make sense to re-use instances of this class. There may be use
 * cases for that but most likely it is a bug.
 *
 * @author Oliver Keszocze
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
        errors = new ArrayList<String>();
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
        Collections.reverse(stack);
        String stackMsg = "rule stack: "+stack;
        String lineMsg = "line "+line+":"+charPositionInLine+": "+msg;
        //System.err.println(stackMsg);
        //System.err.println(lineMsg);
        errors.add(lineMsg + " " + stackMsg);
    }

    /**
     * @brief Checks whether errors occured
     * @note It does not make sense to call this method prior to parsing
     * @return true if errors were present, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     *
     * @return The list of errors, may be empty (but not null)
     */
    public ArrayList<String> getErrors() {
        return errors;
    }
}
