package de.bioviz.parser;


import de.bioviz.parser.generated.Bio;
import de.bioviz.parser.generated.Bio.BlockageContext;
import de.bioviz.parser.generated.Bio.PositionContext;
import de.bioviz.parser.generated.Bio.TimeConstraintContext;
import de.bioviz.parser.generated.Bio.DropletIDContext;
import de.bioviz.parser.generated.Bio.FluidIDContext;
import de.bioviz.parser.generated.Bio.PinIDContext;
import de.bioviz.parser.generated.Bio.SourceContext;
import de.bioviz.parser.generated.Bio.DispenserContext;
import de.bioviz.parser.generated.Bio.TimingContext;
import de.bioviz.parser.generated.Bio.GridblockContext;
import de.bioviz.parser.generated.Bio.FluiddefContext;
import de.bioviz.parser.generated.Bio.PinActuationContext;
import de.bioviz.parser.generated.Bio.CellActuationContext;
import de.bioviz.parser.generated.Bio.RouteContext;
import de.bioviz.parser.generated.Bio.BioContext;
import de.bioviz.parser.generated.Bio.MixerIDContext;
import de.bioviz.parser.generated.Bio.TimeRangeContext;

import de.bioviz.parser.generated.BioBaseListener;
import de.bioviz.structures.*;
import de.bioviz.util.Pair;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("Convert2Diamond")
public class BioParserListener extends BioBaseListener {
	private static Logger logger = LoggerFactory.getLogger(BioParserListener.class);

	private ArrayList<Rectangle> rectangles = new ArrayList<>();
	private ArrayList<Droplet> droplets = new ArrayList<>();
	private int maxX = 0;
	private int maxY = 0;
	private int nGrids = 0;

	private Biochip chip;
	private HashMap<Integer, String> fluidTypes = new HashMap<>();
	private ArrayList<Net> nets = new ArrayList<>();
	private HashMap<Integer, Integer> dropletIDsToFluidTypes = new HashMap<>();
	private ArrayList<Pair<Point, Direction>> sinks = new ArrayList<>();
	private ArrayList<Pair<Integer, Pair<Point, Direction>>> dispensers = new ArrayList<>();
	private ArrayList<Pair<Rectangle, Range>> blockages = new ArrayList<>();
	private ArrayList<Detector> detectors = new ArrayList<>();
	private HashMap<Integer, Pin> pins = new HashMap<>();
	private HashMap<Integer, ActuationVector> pinActuations = new HashMap<>();
	private HashMap<Point, ActuationVector> cellActuations = new HashMap<>();
	private ArrayList<Mixer> mixers = new ArrayList<Mixer>();


	public Biochip getBiochip() {
		return chip;
	}

	private int getTimeConstraint(TimeConstraintContext ctx) {
		return Integer.parseInt(ctx.Integer().getText());
	}

	private Point getPosition(PositionContext ctx) {
		Integer x = Integer.parseInt(ctx.xpos().getText()) - 1;
		Integer y = Integer.parseInt(ctx.ypos().getText()) - 1;
		return new Point(x, y);
	}

	private int getDropletID(DropletIDContext ctx) {
		return Integer.parseInt(ctx.Integer().getText());
	}


	// TODO remove this code dublicity!
	private int getFluidID(FluidIDContext ctx) {
		if (ctx == null) {
			return 0;
		} else {
			return Integer.parseInt(ctx.Integer().getText());
		}
	}

	private int getPinID(PinIDContext ctx) {
		if (ctx == null) {
			return 0;
		} else {
			return Integer.parseInt(ctx.Integer().getText());
		}
	}

	private int getMixerID(MixerIDContext ctx) {
		if (ctx == null) {
			return 0;
		} else {
			return Integer.parseInt(ctx.Integer().getText());
		}
	}

	private Source getSource(SourceContext ctx) {
		Point pos = getPosition(ctx.position());
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
			updateMaxDimension(dispenser.first);
			dispensers.add(new Pair<Integer, Pair<Point, Direction>>(fluidID, dispenser));
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

	private Pair<Point, Direction> getIOPort(Bio.IoportContext ctx) {
		Point pos = getPosition(ctx.position());
		Direction dir = getDirection(ctx.Direction().getText());
		if (dir == null) {
			return null;
		}

		return new Pair<Point, Direction>(pos, dir);
	}

	private void updateMaxDimension(Point p) {
		maxX = Math.max(p.first + 1, maxX);
		maxY = Math.max(p.second + 1, maxY);
	}

	private void updateMaxDimension(Point p1, Point p2) {
		updateMaxDimension(p1);
		updateMaxDimension(p2);
	}

	@Override
	public void enterGrid(Bio.GridContext ctx) {
		++nGrids;
	}

	@Override
	public void enterSink(@NotNull Bio.SinkContext ctx) {
		Pair<Point, Direction> sinkDef = getIOPort(ctx.ioport());
		if (sinkDef != null) {
			updateMaxDimension(sinkDef.first);
			sinks.add(sinkDef);
		} else {
			logger.error("Skipping definition of sink");
		}

	}

	@Override
	public void enterAssignment(@NotNull Bio.AssignmentContext ctx) {
		Point pos = getPosition(ctx.position());
		int pinID = getPinID(ctx.pinID());

		if (pins.containsKey(pinID)) {
			pins.get(pinID).cells.add(pos);
		} else {
			pins.put(pinID, new Pin(pinID, pos));
		}
	}

	@Override
	public void enterDetector(@NotNull Bio.DetectorContext ctx) {
		Point pos = getPosition(ctx.position());
		int duration = 0;
		int fluidType = 0;
		Bio.Detector_specContext spec = ctx.detector_spec();
		if (spec != null) {
			duration = getTimeConstraint(spec.timeConstraint());
			if (spec.fluidID() == null) {
				fluidType = getFluidID(spec.fluidID());
			}
		}

		detectors.add(new Detector(pos, duration, fluidType));
	}

	@Override
	public void enterDropToFluid(@NotNull Bio.DropToFluidContext ctx) {
		int dropID = getDropletID(ctx.dropletID());
		int fluidID = getFluidID(ctx.fluidID());
		dropletIDsToFluidTypes.put(dropID, fluidID);
	}

	@Override
	public void enterNet(@NotNull Bio.NetContext ctx) {
		Point target = getPosition(ctx.target().position());

		ArrayList<Source> sources = new ArrayList<Source>();

		ctx.children.stream().filter(child -> child instanceof Bio.SourceContext).forEach(child -> {
			sources.add(getSource((Bio.SourceContext) child));
			logger.trace("Found source child {}", child);
		});

		nets.add(new Net(sources, target));

	}

	@Override
	public void enterBlockage(@NotNull BlockageContext ctx) {
		Point p1 = getPosition((PositionContext) ctx.getChild(0));
		Point p2 = getPosition((PositionContext) ctx.getChild(1));
		Rectangle rect = new Rectangle(p1, p2);
		Range timing = getTiming((TimingContext) ctx.getChild(2));

		logger.trace("Found blockage {} with timing {}", rect, timing);

		blockages.add(new Pair<Rectangle, Range>(rect, timing));
	}

	private Range getTiming(TimingContext ctx) {
		if (ctx == null) {
			return new Range(Range.DONTCARE, Range.DONTCARE);
		} else {
			int begin = Range.DONTCARE;
			int end = Range.DONTCARE;

			TerminalNode beginTerm = ctx.beginTiming().Integer();
			TerminalNode endTerm = ctx.endTiming().Integer();
			if (beginTerm != null) {
				begin = Integer.parseInt(beginTerm.getText());
			}
			if (endTerm != null) {
				end = Integer.parseInt(endTerm.getText());
			}
			return new Range(begin, end);
		}
	}

	@Override
	public void enterGridblock(GridblockContext ctx) {


		Point p1 = getPosition((PositionContext) ctx.getChild(0));
		Point p2 = getPosition((PositionContext) ctx.getChild(1));

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
	public void enterPinActuation(@NotNull PinActuationContext ctx) {
		int pinID = getPinID(ctx.pinID());
		ActuationVector actVec = new ActuationVector(ctx.ActuationVector().getText());
		pinActuations.put(pinID, actVec);

	}

	@Override
	public void enterCellActuation(@NotNull CellActuationContext ctx) {
		Point pos = getPosition(ctx.position());
		ActuationVector actVec = new ActuationVector(ctx.ActuationVector().getText());
		cellActuations.put(pos, actVec);
	}

	@Override
	public void enterRoute(RouteContext ctx) {
		int dropletID = Integer.parseInt(ctx.dropletID().getText());
		int spawnTime = 1;
		if (ctx.timeConstraint() != null) {
			spawnTime = getTimeConstraint(ctx.timeConstraint());
		}
		Droplet drop = new Droplet(dropletID,spawnTime);
		List<PositionContext> positions = ctx.position();

		for (int i = 0; i < positions.size(); i++) {
			PositionContext pos = positions.get(i);
			Point p = getPosition(pos);
			drop.addPosition(p);
		}
		droplets.add(drop);

	}

	Range getTimeRange(TimeRangeContext ctx) {
		Integer fst = Integer.parseInt(ctx.Integer(0).getText());
		Integer snd = Integer.parseInt(ctx.Integer(1).getText());
		logger.debug("Time range from {} and {}",ctx.Integer(0), ctx.Integer(1));

		return new Range(fst,snd);
	}

	@Override
	public void enterMixer(@NotNull Bio.MixerContext ctx) {

		int id = getMixerID(ctx.mixerID());
		Rectangle rect = new Rectangle(getPosition(ctx.position(0)),getPosition(ctx.position(1)));
		Range time = getTimeRange(ctx.timeRange());
		logger.debug("Received TimeRange {}", time);

		mixers.add(new Mixer(id,rect,time));

	}

	@Override
	public void exitBio(BioContext ctx) {

		chip = new Biochip();
		ArrayList<String> errors = new ArrayList<String>();

		for (Rectangle rect : rectangles) {
			for (Point cell : rect.positions()) {
				chip.addField(cell, new BiochipField(cell));
			}
		}

		if (nGrids > 1) {
			logger.warn("There were {} grid definitions in the file. The cells were merged", nGrids);
		}

		droplets.forEach(chip::addDroplet);
		errors.addAll(Validator.checkPathsForJumps(droplets));
		errors.addAll(Validator.checkPathsForPositions(droplets, chip.getAllCoordinates()));

		chip.addFluidTypes(fluidTypes);
		chip.addNets(nets);

		nets.forEach(net -> {
			Point target = net.target;
			net.sources.forEach(src -> {
				int drop = src.dropletID;
				chip.getFieldAt(target).target_ids.add(drop);
				chip.getFieldAt(src.startPosition).source_ids.add(drop);
			});
		});


		dropletIDsToFluidTypes.forEach(chip::addDropToFluid);



		sinks.forEach(sink -> {
			Point p = sink.first;
			Direction dir = sink.second;
			Point dirPoint = Point.pointFromDirection(dir);
			Point sinkPoint = p.add(dirPoint);
			BiochipField sinkField = new BiochipField(sinkPoint,dir);
			chip.addField(sinkPoint,sinkField);
		});


		dispensers.forEach(dispenser -> {
			int fluidID = dispenser.first;
			Point p = dispenser.second.first;
			Direction dir = dispenser.second.second;
			Point dirPoint = Point.pointFromDirection(dir);
			Point dispPoint = p.add(dirPoint);
			BiochipField dispField = new BiochipField(dispPoint, fluidID, dir);
			chip.addField(dispPoint, dispField);

		});

		for (Pair<Rectangle, Range> b : blockages) {
			Rectangle rect = b.first;
			Range rng = b.second;
			rect.positions().forEach(pos -> chip.getFieldAt(pos).attachBlockage(rng));
		}

		chip.blockages.addAll(blockages);

		detectors.forEach(det -> {
			Point pos = det.position();
			chip.getFieldAt(pos).setDetector(det);
		});
		chip.detectors.addAll(detectors);

		pins.values().forEach(pin -> {
			pin.cells.forEach(pos -> chip.getFieldAt(pos).pin = pin);
		});
		chip.pins.putAll(pins);

		chip.pinActuations.putAll(pinActuations);

		cellActuations.forEach((pos, vec) -> {
			chip.getFieldAt(pos).actVec = vec;
		});
		chip.cellActuations.putAll(cellActuations);

		errors.addAll(Validator.checkActuationVectorLenghts(cellActuations, pinActuations));

		chip.mixers.addAll(this.mixers);
		mixers.forEach(m -> {
			m.positions.positions().forEach(pos -> {
				logger.trace("Adding mixer {} to field {}", m, pos);
				chip.getFieldAt(pos).mixers.add(m);
			});
		});


		errors.forEach(s -> logger.error(s));

	}
}
