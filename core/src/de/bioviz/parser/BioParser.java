package de.bioviz.parser;


import de.agra.dmfb.bioparser.antlr.Bio;
import de.agra.dmfb.bioparser.antlr.BioLexerGrammar;
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
 * Created by keszocze on 21.07.15.
 */
public class BioParser  {

    static private Logger logger = LoggerFactory.getLogger(BioParser.class);


    public static Biochip parseFile(final File file) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.toURI())));
        } catch (IOException e) {
            logger.error("Failde to parse file \"{}\".", file);
            return null;
        }
        return parse(content);
    }

    public static Biochip parse(final String inputString) {
        try {
            ANTLRInputStream input = new ANTLRInputStream(inputString);
            BioLexerGrammar lexer = new BioLexerGrammar(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Bio parser = new Bio(tokens);
            ParseTree tree = parser.bio(); // parse everything

            ParseTreeWalker walker = new ParseTreeWalker();
            // Walk the tree created during the parse, trigger callbacks
            BioParserListener listener = new BioParserListener();
            walker.walk(listener, tree);
            return listener.getBiochip();

        } catch (Exception e) {
            e.printStackTrace();
            // TODO do something with this exception
            // ignore the stupid exception :)
        }
        return null;
    }

}
