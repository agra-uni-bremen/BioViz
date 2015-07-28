package de.dfki.bioviz.parser;

import de.agra.dmfb.bioparser.antlr.Bio;
import de.agra.dmfb.bioparser.antlr.Bio.*;
import de.agra.dmfb.bioparser.antlr.BioBaseListener;
import de.dfki.bioviz.structures.*;
import de.dfki.bioviz.util.Pair;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
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
	private int maxX = 0;
	private int maxY = 0;
	private int nGrids = 0;

	private Biochip chip;
	private HashMap<Integer, String> fluidTypes = new HashMap<Integer, String>();
	private ArrayList<Net> nets = new ArrayList<Net>();
	private HashMap<Integer, Integer> dropletIDsToFluidTypes = new HashMap<>();
	private ArrayList<Pair<Point, Direction>> sinks = new ArrayList<>();
	private ArrayList<Pair<Integer,Pair<Point,Direction>>> dispensers= new ArrayList<>();


	public Biochip getBiochip() {
		return chip;
	}

	private int getTimeConstraint(TimeConstraintContext ctx) {
		return Integer.parseInt(ctx.Integer().getText()) - 1;
	}

	private Point getPositionContext(PositionContext ctx) {
		Integer x = Integer.parseInt(ctx.xpos().getText()) - 1;
		Integer y = Integer.parseInt(ctx.ypos().getText()) - 1;
		return new Point(x, y);
	}

	private int getDropletID(DropletIDContext ctx) {
		return Integer.parseInt(ctx.Integer().getText());
	}

	private int getFluidID(FluidIDContext ctx) {
		if (ctx == null) {
			return 0;
		} else {
			return Integer.parseInt(ctx.Integer().getText());
		}
	}

	private Source getSource(SourceContext ctx) {
		Point pos = getPositionContext(ctx.position());
		int id = getDropletID(ctx.dropletID());
		if (ctx.timeConstraint() != null) {
			return new Source(id, pos, getTimeConstraint(ctx.timeConstraint()));
		} else {
			return new Source(id, pos);
		}
	}


	@Override
	public void enterDispenser(@NotNull DispenserContext ctx) {
		int fluidID = getFluidID(ctx.fluidID());

		Pair<Point, Direction> dispenser = getIOPort(ctx.ioport());
		if (dispenser != null) {
			updateMaxDimension(dispenser.first());
			dispensers.add(new Pair(fluidID, dispenser));
		} else {
			logger.error("Skipping definition of dispenser");
		}

	}

	@Nullable
	private Direction getDirection(String dir) {
		if (dir.equals("N") || dir.equals("U")) {
			return Direction.NORTH;
		}
		if (dir.equals("E") || dir.equals("R")) {
			return Direction.EAST;
		}
		if (dir.equals("S") || dir.equals("D")) {
			return Direction.SOUTH;
		}
		if (dir.equals("W") || dir.equals("L")) {
			return Direction.WEST;
		}

		logger.error("Could not parse \"{}\" as direction.");
		return null;
	}

	private Pair<Point, Direction> getIOPort(IoportContext ctx) {
		Point pos = getPositionContext(ctx.position());
		Direction dir = getDirection(ctx.Direction().getText());
		if (dir == null) {
			return null;
		}

		return new Pair(pos, dir);
	}

	private void updateMaxDimension(Point p) {
		maxX = Math.max(p.first() + 1, maxX);
		maxY = Math.max(p.second() + 1, maxY);
	}

	private void updateMaxDimension(Point p1, Point p2) {
		updateMaxDimension(p1);
		updateMaxDimension(p2);
	}

	@Override
	public void enterGrid(GridContext ctx) {
		++nGrids;
	}

	@Override
	public void enterSink(@NotNull SinkContext ctx) {
		Pair<Point, Direction> sinkDef = getIOPort(ctx.ioport());
		if (sinkDef != null) {
			updateMaxDimension(sinkDef.first());
			sinks.add(sinkDef);
		} else {
			logger.error("Skipping definition of sink");
		}

	}

	@Override
	public void enterDropToFluid(@NotNull DropToFluidContext ctx) {
		int dropID = getDropletID(ctx.dropletID());
		int fluidID = getFluidID(ctx.fluidID());
		dropletIDsToFluidTypes.put(dropID, fluidID);
	}

	@Override
	public void enterNet(@NotNull Bio.NetContext ctx) {
		Point target = getPositionContext(ctx.target().position());

		ArrayList<Source> sources = new ArrayList<Source>();

		for (ParseTree child : ctx.children) {
			if (child instanceof SourceContext) {
				sources.add(getSource((SourceContext) child));
				logger.debug("Found source child {}", (SourceContext) child);
			}
		}

		nets.add(new Net(sources, target));

	}


	@Override
	public void enterGridblock(GridblockContext ctx) {


		Point p1 = getPositionContext((PositionContext) ctx.getChild(0));
		Point p2 = getPositionContext((PositionContext) ctx.getChild(1));

		updateMaxDimension(p1, p2);

		rectangles.add(new Rectangle(p1, p2));

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
			drop.addPosition(i + offset, p.first(), p.second());
		}
		droplets.add(drop);

	}

	@Override
	public void exitBio(BioContext ctx) {

		chip = new Biochip(maxX, maxY);

		for (Rectangle rect : rectangles) {
			for (Point cell : rect.positions()) {
				chip.enableFieldAt(cell.first(), cell.second());
			}
		}

		if (nGrids > 1) {
			logger.warn("There were {} grid definitions in the file. The cells were merged", nGrids);
		}

		droplets.forEach(chip::addDroplet);
		chip.addFluidTypes(fluidTypes);
		chip.addNets(nets);
		dropletIDsToFluidTypes.forEach(chip::addDropToFluid);
		sinks.forEach(sink -> {
			Point p = sink.first();
			chip.field[p.first()][p.second()].setSink(sink.second());
		});


		dispensers.forEach(dispenser -> {
			int fluidID=dispenser.first();
			Point p = dispenser.second().first();
			Direction dir = dispenser.second().second();
			chip.field[p.first()][p.second()].setDispenser(fluidID,dir);
		});

	}
}
