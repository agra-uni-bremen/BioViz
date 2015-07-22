package de.dfki.bioviz.parser;

import de.agra.dmfb.bioparser.antlr.Bio;
import de.agra.dmfb.bioparser.antlr.BioBaseListener;

import structures.Biochip;



public class BioParserListener extends BioBaseListener {

    private BioChip chip;

    @Override
    public void enterGrid(Bio.GridContext ctx) {
        System.out.println("Bin in einem Grid angekommen :)");
    }
}
