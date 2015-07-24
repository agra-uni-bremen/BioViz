package de.dfki.bioviz.parser;

import de.agra.dmfb.bioparser.antlr.Bio;
import de.agra.dmfb.bioparser.antlr.Bio.GridContext;
import de.agra.dmfb.bioparser.antlr.Bio.PositionContext;
import de.agra.dmfb.bioparser.antlr.Bio.GridblockContext;
import de.agra.dmfb.bioparser.antlr.Bio.RouteContext;
import de.agra.dmfb.bioparser.antlr.BioBaseListener;
import de.agra.dmfb.bioparser.antlr.Bio.BioContext;
import de.dfki.bioviz.structures.Biochip;
import de.dfki.bioviz.structures.Droplet;
import de.dfki.bioviz.structures.Rectangle;
import org.antlr.v4.runtime.misc.Pair;


import java.util.ArrayList;
import java.util.List;


public class BioParserListener extends BioBaseListener {

    private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
    private ArrayList<Droplet> droplets = new ArrayList<Droplet>();
    private int maxX=0;
    private int maxY=0;
    private int nGrids = 0;

    private Biochip chip;

    public Biochip getBiochip() {
        return chip;
    }

    @Override
    public void enterGrid(GridContext ctx) {
        ++nGrids;
    }

    @Override
    public void enterGridblock(GridblockContext ctx) {


        PositionContext pos1 = (PositionContext)ctx.getChild(0);
        PositionContext pos2 = (PositionContext)ctx.getChild(1);

        int x1 = Integer.parseInt(pos1.xpos().getText())-1;
        int y1 = Integer.parseInt(pos1.ypos().getText())-1;
        int x2 = Integer.parseInt(pos2.xpos().getText())-1;
        int y2 = Integer.parseInt(pos2.ypos().getText())-1;

        maxX = Math.max(Math.max(x1+1,x2+1),maxX);
        maxY = Math.max(Math.max(y1+1,y2+1),maxY);

        rectangles.add(new Rectangle(x1,y1,x2,y2));

        super.enterGridblock(ctx);
    }

    @Override
    public void enterRoute(RouteContext ctx) {
        int dropletID = Integer.parseInt(ctx.dropletID().getText());
        int offset = 0;
        if (ctx.starttime() != null) {
        	offset = Integer.parseInt(ctx.starttime().Integer().getText()) - 1;
        }
        Droplet drop = new Droplet(dropletID);
        List<PositionContext> positions = ctx.position();

        for (int i = 0; i < positions.size(); i++) {
            PositionContext pos = positions.get(i);
            int x = Integer.parseInt(pos.xpos().getText())-1;
            int y = Integer.parseInt(pos.ypos().getText())-1;
            drop.addPosition(i + offset,x,y);
        }
        droplets.add(drop);

    }

    @Override
    public void exitBio(BioContext ctx) {
        // TODO issue warning if more than one grid has been parsed

        chip = new Biochip(maxX, maxY);

        for(Rectangle rect: rectangles) {
            for (Pair<Integer,Integer> cell: rect.positions()) {
                chip.enableFieldAt(cell.a, cell.b);
            }
        }

        droplets.forEach(chip::addDroplet);
    }
}
