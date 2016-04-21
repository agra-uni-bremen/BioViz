package de.bioviz.parser;


import de.bioviz.parser.generated.Bio;
import de.bioviz.parser.generated.BioLexerGrammar;
import de.bioviz.structures.Biochip;
import de.bioviz.ui.BioViz;

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
 * @author Oliver Keszocze
 */
public class BioParser  {

    static private Logger logger = LoggerFactory.getLogger(BioParser.class);


    public static Biochip parseFile(final File file, BioViz viz) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.toURI())));
        } catch (IOException e) {
            logger.error("Failed to parse file \"{}\".", file);
            return null;
        }
        return parse(content, viz);
    }

    public static Biochip parse(final String inputString, BioViz viz) {

        logger.trace("Parsing file of length {}",inputString.length());

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
                for (String msg : errorListener.getErrors()) {
                    logger.error(msg);
                }
                return null;
            }
            else {
                ParseTreeWalker walker = new ParseTreeWalker();
                // Walk the tree created during the parse, trigger callbacks
                BioParserListener listener = new BioParserListener(viz);
                walker.walk(listener, tree);
                return listener.getBiochip();
            }
        } catch (Exception e) {
            logger.error("Failed to parse file");
            e.printStackTrace();
            return null;
        }
    }

}
