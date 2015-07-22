package de.dfki.bioviz.parser;

import de.agra.dmfb.bioparser.antlr.Bio;
import de.agra.dmfb.bioparser.antlr.BioBaseListener;
import de.dfki.bioviz.structures.Biochip;
import de.dfki.bioviz.structures.Rectangle;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;

import static de.agra.dmfb.bioparser.antlr.Bio.*;

public class BioParserListener extends BioBaseListener {

    private Biochip chip;
    private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
    private int maxX=0;
    private int maxY=0;
    private int nGrids = 0;

    @Override
    public void enterGrid(GridContext ctx) {
        ++nGrids;
    }

    @Override
    public void enterGridblock(GridblockContext ctx) {

        System.out.println(ctx.getChildCount());
        System.out.println(ctx.getChild(0));
        System.out.println(ctx.getChild(1));

        PositionContext pos1 = (PositionContext)ctx.getChild(0);
        PositionContext pos2 = (PositionContext)ctx.getChild(1);

        int x1 = java.lang.Integer.parseInt(pos1.xpos().getText());
        int y1 = java.lang.Integer.parseInt(pos1.ypos().getText());
        int x2 = java.lang.Integer.parseInt(pos2.xpos().getText());
        int y2 = java.lang.Integer.parseInt(pos2.ypos().getText());

        maxX = Math.max(Math.max(x1,x2),maxX);
        maxY = Math.max(Math.max(y1,y2),maxY);

        rectangles.add(new Rectangle(x1,y1,x2,y2));

        super.enterGridblock(ctx);
    }

    @Override
    public void exitBio(BioContext ctx) {
        // TODO generate the grid
    }
}
