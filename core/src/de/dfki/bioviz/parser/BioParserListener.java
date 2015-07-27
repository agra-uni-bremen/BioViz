package de.dfki.bioviz.parser;

import de.agra.dmfb.bioparser.antlr.Bio;
import de.agra.dmfb.bioparser.antlr.Bio.FluiddefContext;
import de.agra.dmfb.bioparser.antlr.Bio.GridContext;
import de.agra.dmfb.bioparser.antlr.Bio.PositionContext;
import de.agra.dmfb.bioparser.antlr.Bio.GridblockContext;
import de.agra.dmfb.bioparser.antlr.Bio.RouteContext;
import de.agra.dmfb.bioparser.antlr.BioBaseListener;
import de.agra.dmfb.bioparser.antlr.Bio.BioContext;
import de.dfki.bioviz.structures.*;

import de.dfki.bioviz.util.Pair;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BioParserListener extends BioBaseListener {
    private static Logger logger = LoggerFactory.getLogger(BioParserListener.class);

    private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
    private ArrayList<Droplet> droplets = new ArrayList<Droplet>();
    private int maxX=0;
    private int maxY=0;
    private int nGrids = 0;

    private Biochip chip;
    private HashMap<Integer,String> fluidTypes = new HashMap<Integer,String>();
	private ArrayList<Net> nets;





	public HashMap<Integer, String> getFluidTypes() {
		return fluidTypes;
	}
    public Biochip getBiochip() {
        return chip;
    }

	private int getTimeConstraint(Bio.TimeConstraintContext ctx) {
		return Integer.parseInt(ctx.Integer().getText())-1;
	}
	private Point getPositionContext(PositionContext ctx) {
		Integer x = Integer.parseInt(ctx.xpos().getText())-1;
		Integer y = Integer.parseInt(ctx.ypos().getText())-1;
		return new Point(x,y);
	}

    @Override
    public void enterGrid(GridContext ctx) {
        ++nGrids;
    }

	@Override
	public void enterNet(@NotNull Bio.NetContext ctx) {
		Point target = getPositionContext(ctx.target().position());

		ArrayList<Pair<Integer,Point>> sources;

		for (ParseTree child: ctx.children) {
			if (child instanceof Bio.SourceContext) {
				logger.debug("Found source child {}",(Bio.SourceContext)child);
			}
		}

	}

    @Override
    public void enterGridblock(GridblockContext ctx) {


		Point p1 = getPositionContext((PositionContext)ctx.getChild(0));
		Point p2 = getPositionContext((PositionContext)ctx.getChild(1));

        maxX = Math.max(Math.max(p1.first()+1,p2.first()+1),maxX);
        maxY = Math.max(Math.max(p1.second()+1,p2.second()+1),maxY);

        rectangles.add(new Rectangle(p1,p2));

        super.enterGridblock(ctx);
    }

    @Override
    public void enterFluiddef(@NotNull FluiddefContext ctx) {
        int fluidID = Integer.parseInt(ctx.Integer().getText());
        String fluid = ctx.Identifier().getText();
        fluidTypes.put(fluidID, fluid);
    }




    @Override
    public void enterRoute(RouteContext ctx) {
        int dropletID = Integer.parseInt(ctx.dropletID().getText());
        int offset = 0;
        if (ctx.timeConstraint() != null) {
        	offset = getTimeConstraint(ctx.timeConstraint());
        }
        Droplet drop = new Droplet(dropletID);
        List<PositionContext> positions = ctx.position();

        for (int i = 0; i < positions.size(); i++) {
            PositionContext pos = positions.get(i);
			Point p = getPositionContext(pos);
            drop.addPosition(i + offset,p.first(),p.second());
        }
        droplets.add(drop);

    }

    @Override
    public void exitBio(BioContext ctx) {
        // TODO issue warning if more than one grid has been parsed

        chip = new Biochip(maxX, maxY);

        for(Rectangle rect: rectangles) {
            for (Point cell: rect.positions()) {
                chip.enableFieldAt(cell.first(), cell.second());
            }
        }

        if (nGrids>1) {
            logger.warn("There were {} grid definitions in the file. The cells were merged",nGrids);
        }

        droplets.forEach(chip::addDroplet);

		chip.addFluidTypes(fluidTypes);
    }
}
