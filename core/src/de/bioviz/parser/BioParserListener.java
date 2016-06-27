package de.bioviz.parser;


import de.bioviz.parser.generated.Bio;
import de.bioviz.parser.generated.Bio.BioContext;
import de.bioviz.parser.generated.Bio.BlockageContext;
import de.bioviz.parser.generated.Bio.CellActuationContext;
import de.bioviz.parser.generated.Bio.DispenserContext;
import de.bioviz.parser.generated.Bio.DropletIDContext;
import de.bioviz.parser.generated.Bio.FluidIDContext;
import de.bioviz.parser.generated.Bio.FluiddefContext;
import de.bioviz.parser.generated.Bio.GridblockContext;
import de.bioviz.parser.generated.Bio.MixerIDContext;
import de.bioviz.parser.generated.Bio.PinActuationContext;
import de.bioviz.parser.generated.Bio.PinIDContext;
import de.bioviz.parser.generated.Bio.PositionContext;
import de.bioviz.parser.generated.Bio.RouteContext;
import de.bioviz.parser.generated.Bio.SourceContext;
import de.bioviz.parser.generated.Bio.TimeConstraintContext;
import de.bioviz.parser.generated.Bio.TimeRangeContext;
import de.bioviz.parser.generated.Bio.TimingContext;
import de.bioviz.parser.generated.BioBaseListener;
import de.bioviz.structures.ActuationVector;
import de.bioviz.structures.AreaAnnotation;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Detector;
import de.bioviz.structures.Direction;
import de.bioviz.structures.Dispenser;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.FluidicConstraintViolation;
import de.bioviz.structures.Mixer;
import de.bioviz.structures.Net;
import de.bioviz.structures.Pin;
import de.bioviz.structures.Point;
import de.bioviz.structures.Range;
import de.bioviz.structures.Rectangle;
import de.bioviz.structures.Sink;
import de.bioviz.structures.Source;
import de.bioviz.util.Pair;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * This class implements a Listener for the BioParser.
 *
 * @author Oliver Keszocze?, Jannis Stoppe?, Maximilian Luenert
 */
class BioParserListener extends BioBaseListener {
	/**
	 * The logger instance.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(BioParserListener.class);

	/**
	 * Stores all the parsed rectangles used to define the cells of the chip.
	 */
	private ArrayList<Rectangle> rectangles = new ArrayList<>();

	/**
	 * Stores all the parsed droplets.
	 */
	private ArrayList<Droplet> droplets = new ArrayList<>();

	/**
	 * Stores the maxX coordinate.
	 */
	private int maxX = 0;

	/**
	 * Stores the maxY coordinate.
	 */
	private int maxY = 0;

	/**
	 * Stores the number of grids that were defined in the input file.
	 */
	private int nGrids = 0;

	/**
	 * Stores all parsed fluidTypes.
	 */
	private HashMap<Integer, String> fluidTypes = new HashMap<>();

	/**
	 * Stores alls parsed nets.
	 */
	private ArrayList<Net> nets = new ArrayList<>();

	/**
	 * Stores a connection from dropletIds to FluidTypes.
	 */
	private HashMap<Integer, Integer> dropletIDsToFluidTypes = new HashMap<>();

	/**
	 * Stores all parsed sinks.
	 */
	private ArrayList<Pair<Point, Direction>> sinks = new ArrayList<>();

	/**
	 * Stores all parsed dispensers.
	 */
	private ArrayList<Pair<Integer, Pair<Point, Direction>>> dispensers =
			new ArrayList<>();

	/**
	 * Stores all parsed blockages.
	 */
	private ArrayList<Pair<Rectangle, Range>> blockages = new ArrayList<>();

	/**
	 * Stores all parsed detectors.
	 */
	private ArrayList<Detector> detectors = new ArrayList<>();

	/**
	 * Stores all parsed pin assignments.
	 */
	private HashMap<Integer, Pin> pins = new HashMap<>();

	/**
	 * Stores all parsed pinActuations.
	 */
	private HashMap<Integer, ActuationVector> pinActuations = new HashMap<>();

	/**
	 * Stores all parsed cellActuations.
	 */
	private HashMap<Point, ActuationVector> cellActuations = new HashMap<>();

	/**
	 * Stores all parsed areaAnnotations.
	 */
	private ArrayList<AreaAnnotation> areaAnnotations = new ArrayList<>();

	/**
	 * Stores all parsed mixers.
	 */
	private ArrayList<Mixer> mixers = new ArrayList<>();


	/**
	 * The biochip that will be filled with the stuff that was parsed.
	 */
	private Biochip chip;

	/**
	 * Stores the textual representation of all errors that were found during
	 * the parsing process.
	 */
	private List<String> errors;

	/**
	 * Empty constructor.
	 */
	BioParserListener() {

	}

	/**
	 * Gets all the errors.
	 *
	 * @return List of String containing all error messages.
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Gets the parsed biochip.
	 *
	 * @return parsed biochip.
	 */
	public Biochip getBiochip() {
		return chip;
	}

	/**
	 * Parses the given TimeConstraintContext.
	 *
	 * @param ctx The TimeConstraintContext
	 * @return int value of the timeConstraint
	 */
	private int getTimeConstraint(final TimeConstraintContext ctx) {
		return Integer.parseInt(ctx.Integer().getText());
	}

	/**
	 * Parses a given PositionContext.
	 *
	 * @param ctx The positionContext
	 * @return Point object for the position
	 */
	private Point getPosition(final PositionContext ctx) {
		Integer x = Integer.parseInt(ctx.xpos().getText());
		Integer y = Integer.parseInt(ctx.ypos().getText());
		return new Point(x, y);
	}

	/**
	 * Parses the given dropletIDContext.
	 *
	 * @param ctx The DropletIdContext
	 * @return int value of the dropletId
	 */
	private int getDropletID(final DropletIDContext ctx) {
		return Integer.parseInt(ctx.Integer().getText());
	}


	/**
	 * Parses the given fluidIdContext.
	 *
	 * @param ctx The fluidIdContext
	 * @return int value of the fluidId
	 * 					or 0 if ctx is null
	 */
	// TODO remove this code duplicity!
	private int getFluidID(final FluidIDContext ctx) {
		if (ctx == null) {
			return 0;
		} else {
			return Integer.parseInt(ctx.Integer().getText());
		}
	}

	/**
	 * Parses the given PinIdContext.
	 * @param ctx The PinIdContext
	 * @return int value of the PinId or
	 * 					0 if ctx is null
	 */
	private int getPinID(final PinIDContext ctx) {
		if (ctx == null) {
			return 0;
		} else {
			return Integer.parseInt(ctx.Integer().getText());
		}
	}

	/**
	 * Parses the given MixerIdContext.
	 *
	 * @param ctx The MixerIdContext
	 * @return int value of the parsed mixerId
	 * 					or 0 if ctx is null
	 */
	private int getMixerID(final MixerIDContext ctx) {
		if (ctx == null) {
			return 0;
		} else {
			return Integer.parseInt(ctx.Integer().getText());
		}
	}

	/**
	 * Parses the given SourceContext.
	 *
	 * @param ctx The SourceContext
	 * @return Source object
	 */
	private Source getSource(final SourceContext ctx) {
		Point pos = getPosition(ctx.position());
		int id = getDropletID(ctx.dropletID());
		if (ctx.timeConstraint() != null) {
			return new Source(id, pos, getTimeConstraint(ctx.timeConstraint()));
		} else {
			return new Source(id, pos);
		}
	}

	/**
	 * Parses the given AreaAnnotationContext.
	 *
	 * @param ctx The AreaAnnotationContext
	 * @return AreaAnnotation object
	 */
	private AreaAnnotation getAreaAnnotation(final Bio.AreaAnnotationContext
																							 ctx) {
		Point pos1 = getPosition(ctx.position(0));
		Point pos2 = pos1;
		if (ctx.position().size() > 1) {
			pos2 = getPosition(ctx.position(1));
		}
		Rectangle rect = new Rectangle(pos1, pos2);

		return new AreaAnnotation(rect, ctx.AreaAnnotationText().getText());
	}

	@Override
	public void enterDispenser(@NotNull final DispenserContext ctx) {
		int fluidID = getFluidID(ctx.fluidID());

		Pair<Point, Direction> dispenser = getIOPort(ctx.ioport());
		if (dispenser != null) {
			updateMaxDimension(dispenser.fst);
			dispensers.add(new Pair<>(fluidID, dispenser));
		} else {
			logger.error("Skipping definition of dispenser");
		}

	}

	/**
	 * Parses a String as a Direction.
	 *
	 * @param dir String resembling a direction
	 * @return Direction object or null on error
	 */
	@Nullable
	private Direction getDirection(final String dir) {
		if ("N".equals(dir) || "U".equals(dir)) {
			return Direction.NORTH;
		}
		if ("E".equals(dir) || "R".equals(dir)) {
			return Direction.EAST;
		}
		if ("S".equals(dir) || "D".equals(dir)) {
			return Direction.SOUTH;
		}
		if ("W".equals(dir) || "L".equals(dir)) {
			return Direction.WEST;
		}

		logger.error("Could not parse  \"{}\" as direction.");
		return null;
	}

	/**
	 * Parses a given IoportContext.
	 *
	 * @param ctx The IoportContext
	 * @return Pair with Point and Direction
	 */
	private Pair<Point, Direction> getIOPort(final Bio.IoportContext ctx) {
		Point pos = getPosition(ctx.position());
		Direction dir = getDirection(ctx.Direction().getText());
		if (dir == null) {
			return null;
		}

		return new Pair<>(pos, dir);
	}

	/**
	 * Updates the MaxDimension values. It compares the given Point with the
	 * stored maxX and maxY values.
	 *
	 * @param p A point with the coordinates to test.
	 */
	private void updateMaxDimension(final Point p) {
		maxX = Math.max(p.fst + 1, maxX);
		maxY = Math.max(p.snd + 1, maxY);
	}

	/**
	 * Updates the max dimensions with two points.
	 *
	 * @param p1 The first point
	 * @param p2 The second point
	 */
	private void updateMaxDimension(final Point p1, final Point p2) {
		updateMaxDimension(p1);
		updateMaxDimension(p2);
	}

	/**
	 * Updates the count of parsed grids.
	 *
	 * @param ctx A GridContext
	 */
	@Override
	public void enterGrid(final Bio.GridContext ctx) {
		++nGrids;
	}

	/**
	 * Parses a given SinkContext.
	 *
	 * @param ctx The SinkContext
	 */
	@Override
	public void enterSink(@NotNull final Bio.SinkContext ctx) {
		Pair<Point, Direction> sinkDef = getIOPort(ctx.ioport());
		if (sinkDef != null) {
			updateMaxDimension(sinkDef.fst);
			sinks.add(sinkDef);
		} else {
			logger.error("Skipping definition of sink");
		}

	}

	/**
	 * Parses a given AssignmentContext.
	 *
	 * @param ctx The AssignmentContext
	 */
	@Override
	public void enterAssignment(@NotNull final Bio.AssignmentContext ctx) {
		Point pos = getPosition(ctx.position());
		int pinID = getPinID(ctx.pinID());

		if (pins.containsKey(pinID)) {
			pins.get(pinID).cells.add(pos);
		} else {
			pins.put(pinID, new Pin(pinID, pos));
		}
	}

	/**
	 * Parses a given DetectorContext.
	 *
	 * @param ctx The DetectorContext
	 */
	@Override
	public void enterDetector(@NotNull final Bio.DetectorContext ctx) {
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

	/**
	 * Parses a given DropToFluidContext.
	 *
	 * @param ctx The DropToFluidContext
	 */
	@Override
	public void enterDropToFluid(@NotNull final Bio.DropToFluidContext ctx) {
		int dropID = getDropletID(ctx.dropletID());
		int fluidID = getFluidID(ctx.fluidID());
		logger.debug("Adding droplet ID to fluid ID mapping: {} -> {}", dropID,
					 fluidID);
		dropletIDsToFluidTypes.put(dropID, fluidID);
	}

	/**
	 * Parses a NetContext.
	 *
	 * @param ctx The NetContext
	 */
	@Override
	public void enterNet(@NotNull final Bio.NetContext ctx) {
		Point target = getPosition(ctx.target().position());

		ArrayList<Source> sources = new ArrayList<>();

		ctx.children.stream().filter(
				child -> child instanceof Bio.SourceContext).forEach(child -> {
			sources.add(getSource((Bio.SourceContext) child));
			logger.trace("Found source child {}", child);
		});

		nets.add(new Net(sources, target));

	}

	/**
	 * Parses a given BlockageContext.
	 *
	 * @param ctx The BlockageContext
	 */
	@Override
	public void enterBlockage(@NotNull final BlockageContext ctx) {
		Point p1 = getPosition((PositionContext) ctx.getChild(0));
		Point p2 = getPosition((PositionContext) ctx.getChild(1));
		Rectangle rect = new Rectangle(p1, p2);
		Range timing = getTiming((TimingContext) ctx.getChild(2));

		logger.trace("Found blockage {} with timing {}", rect, timing);

		blockages.add(new Pair<>(rect, timing));
	}

	/**
	 * Parses a given TimingContext.
	 *
	 * @param ctx The TimingContext
	 * @return Range object from begin to end or
	 * 					DONTCARE to DONTCARE if ctx is null
	 */
	private Range getTiming(final TimingContext ctx) {
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

	/**
	 * Parses a given GridblockContext.
	 *
	 * @param ctx The GridblockContext
	 */
	@Override
	public void enterGridblock(final GridblockContext ctx) {


		Point p1 = getPosition((PositionContext) ctx.getChild(0));
		Point p2 = getPosition((PositionContext) ctx.getChild(1));

		updateMaxDimension(p1, p2);

		rectangles.add(new Rectangle(p1, p2));

		super.enterGridblock(ctx);
	}

	/**
	 * Parses a given FluiddefContext.
	 *
	 * @param ctx The FluiddefContext
	 */
	@Override
	public void enterFluiddef(@NotNull final FluiddefContext ctx) {
		int fluidID = Integer.parseInt(ctx.fluidID().getText());
		String fluid = ctx.Identifier().getText();
		logger.debug("Adding fluid identifier: {} -> {}", fluidID, fluid);
		fluidTypes.put(fluidID, fluid);
	}

	/**
	 * Parses a given PinActuationContext.
	 *
	 * @param ctx The PinActuationContext.
	 */
	@Override
	public void enterPinActuation(@NotNull final PinActuationContext ctx) {
		int pinID = getPinID(ctx.pinID());
		ActuationVector actVec =
				new ActuationVector(ctx.ActuationVector().getText());
		pinActuations.put(pinID, actVec);

	}

	/**
	 * Parses a given CellActuationContext.
	 *
	 * @param ctx The CellActuationContext
	 */
	@Override
	public void enterCellActuation(@NotNull final CellActuationContext ctx) {
		Point pos = getPosition(ctx.position());
		ActuationVector actVec =
				new ActuationVector(ctx.ActuationVector().getText());
		cellActuations.put(pos, actVec);
	}

	/**
	 * Parses a given RouteContext.
	 *
	 * @param ctx The RouteContext
	 */
	@Override
	public void enterRoute(final RouteContext ctx) {
		int dropletID = Integer.parseInt(ctx.dropletID().getText());
		int spawnTime = 1;
		if (ctx.timeConstraint() != null) {
			spawnTime = getTimeConstraint(ctx.timeConstraint());
		}
		Droplet drop = new Droplet(dropletID, spawnTime);
		List<PositionContext> positions = ctx.position();

		for (final PositionContext pos : positions) {
			Point p = getPosition(pos);
			drop.addPosition(p);
		}

		droplets.add(drop);

	}

	/**
	 * Parses a given TimeRangeContext.
	 *
	 * @param ctx The TimeRangeContext
	 * @return Range from start to end
	 */
	private Range getTimeRange(final TimeRangeContext ctx) {
		Integer fst = Integer.parseInt(ctx.Integer(0).getText());
		Integer snd = Integer.parseInt(ctx.Integer(1).getText());
		logger.debug("Time range from {} to {}", ctx.Integer(0),
					 ctx.Integer(1));

		return new Range(fst, snd);
	}

	/**
	 * Parses a given MixerContext.
	 *
	 * @param ctx The MixerContext
	 */
	@Override
	public void enterMixer(@NotNull final Bio.MixerContext ctx) {

		int id = getMixerID(ctx.mixerID());
		Rectangle rect = new Rectangle(getPosition(ctx.position(0)),
									   getPosition(ctx.position(1)));
		Range time = getTimeRange(ctx.timeRange());
		logger.debug("Received TimeRange {}", time);

		mixers.add(new Mixer(id, rect, time));

	}

	/**
	 * Parses a given AnnotationContext.
	 *
	 * @param ctx The AnnotationContext
	 */
	@Override
	public void enterAnnotations(@NotNull final Bio.AnnotationsContext ctx) {
		for (final Bio.AreaAnnotationContext areaCtx : ctx.areaAnnotation()) {
			areaAnnotations.add(getAreaAnnotation(areaCtx));
		}
	}

	/**
	 * Creates the BioChip when the parsing is done.
	 *
	 * @param ctx The BioContext
	 */
	@Override
	public void exitBio(final BioContext ctx) {

		chip = new Biochip();
		errors = new ArrayList<>();

		for (final Rectangle rect : rectangles) {
			for (final Point cell : rect.positions()) {
				chip.addField(cell, new BiochipField(cell, chip));
			}
		}

		if (nGrids > 1) {
			logger.warn(
					"There were {} grid definitions in the file. The cells " +
					"were merged",
					nGrids);
		}

		droplets.forEach(chip::addDroplet);
		errors.addAll(Validator.checkPathsForJumps(droplets));
		errors.addAll(Validator.checkPathsForPositions(
				droplets,
				chip.getAllCoordinates())
		);

		chip.addFluidTypes(fluidTypes);
		chip.addNets(nets);

		nets.forEach(net -> {
			Point target = net.getTarget();
			net.getSources().forEach(src -> {
				int dropID = src.dropletID;

				/*
				kind of weird code to set the net of a droplet. But this
				happens
				when the Java people think that they have a clever idea for
				'stream' when normal people would simply directly operate on
				lists, maps etc.
				 */
				Optional<Droplet> drop = droplets.stream().filter(
						it -> it.getID() == dropID).findFirst();
				drop.ifPresent(it -> it.setNet(net));

				chip.getFieldAt(target).targetIDs.add(dropID);
				chip.getFieldAt(src.startPosition).sourceIDs.add(dropID);
			});
		});


		dropletIDsToFluidTypes.forEach(chip::addDropToFluid);


		errors.addAll(Validator.checkSinkPositions(chip, sinks, true));
		sinks.forEach(sink -> {
			Point p = sink.fst;
			Direction dir = sink.snd;
			Point dirPoint = Point.pointFromDirection(dir);
			Point sinkPoint = p.add(dirPoint);
			Sink sinkField = new Sink(sinkPoint, dir, chip);
			chip.addField(sinkPoint, sinkField);
		});

		errors.addAll(Validator.checkDispenserPositions(chip,
														dispensers,
														true));
		dispensers.forEach(dispenser -> {
			int fluidID = dispenser.fst;
			Point p = dispenser.snd.fst;
			Direction dir = dispenser.snd.snd;
			Point dirPoint = Point.pointFromDirection(dir);
			Point dispPoint = p.add(dirPoint);
			Dispenser dispField =
					new Dispenser(dispPoint, fluidID, dir, chip);
			chip.addField(dispPoint, dispField);

		});

		for (final Pair<Rectangle, Range> b : blockages) {
			Rectangle rect = b.fst;
			Range rng = b.snd;
			rect.positions().forEach(
					pos -> chip.getFieldAt(pos).attachBlockage(rng));
		}

		chip.blockages.addAll(blockages);

		errors.addAll(Validator.checkPathForBlockages(chip));

		errors.addAll(
				Validator.checkForDetectorPositions(chip, detectors, true));
		// only valid detectors are left -> we can happily add them to the chip
		detectors.forEach(det -> {
			Point pos = det.position();
			chip.getFieldAt(pos).setDetector(det);
		});
		chip.detectors.addAll(detectors);


		pins.values().forEach(pin ->
			pin.cells.forEach(pos -> chip.getFieldAt(pos).pin = pin)
		);
		chip.pins.putAll(pins);
		errors.addAll(Validator.checkMultiplePinAssignments(pins.values()));
		chip.pinActuations.putAll(pinActuations);

		cellActuations.forEach((pos, vec) ->
			chip.getFieldAt(pos).actVec = vec
		);
		chip.cellActuations.putAll(cellActuations);

		errors.addAll(Validator.checkActuationVectorLengths(cellActuations,
															pinActuations));
		errors.addAll(Validator.checkCellPinActuationCompatibility(chip,
																   cellActuations,
																   pinActuations,
																   true));
		errors.addAll(Validator.checkCellPinActuationCompatibility(chip,
																   cellActuations,
																   pinActuations,
																   false));

		chip.mixers.addAll(this.mixers);
		mixers.forEach(m ->
			m.positions.positions().forEach(pos -> {
				logger.trace("Adding mixer {} to field {}", m, pos);
				chip.getFieldAt(pos).mixers.add(m);
			})
		);

		chip.areaAnnotations.addAll(this.areaAnnotations);
		areaAnnotations.forEach(a ->
			a.getPosition().positions().forEach(pos -> {
				logger.trace("Adding areaAnnotation {} to field {}", a, pos);
				chip.getFieldAt(pos).areaAnnotations.add(a);
			})
		);

		Set<FluidicConstraintViolation> badFields =
				chip.getAdjacentActivations();

		for (final FluidicConstraintViolation violation : badFields) {
			errors.add(violation.toString());
		}


		errors.forEach(logger::info);
		chip.errors.addAll(errors);

	}
}
