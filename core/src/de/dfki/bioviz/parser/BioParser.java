package de.dfki.bioviz.parser;


import de.agra.dmfb.bioparser.antlr.Bio;
import de.agra.dmfb.bioparser.antlr.BioBaseListener;
import de.agra.dmfb.bioparser.antlr.BioLexerGrammar;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by keszocze on 21.07.15.
 */
public class BioParser  {


    public static void parse(final String file) {
        try {
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(file));
            BioLexerGrammar lexer = new BioLexerGrammar(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Bio parser = new Bio(tokens);
            ParseTree tree = parser.bio(); // parse everything

            ParseTreeWalker walker = new ParseTreeWalker();
            // Walk the tree created during the parse, trigger callbacks
            walker.walk(new BioParserListener(), tree);
            System.out.println(tree.toStringTree(parser));

        } catch (Exception e) {
            // ignore the stupid exception :)
        }
    }

}
