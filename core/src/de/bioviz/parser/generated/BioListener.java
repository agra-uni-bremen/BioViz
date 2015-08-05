// Generated from /home/keszocze/proggs/BioViz/lib/parser/grammar/Bio.g4 by ANTLR 4.2.2
package de.bioviz.parser.generated;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Bio}.
 */
public interface BioListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Bio#ypos}.
	 * @param ctx the parse tree
	 */
	void enterYpos(@NotNull Bio.YposContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#ypos}.
	 * @param ctx the parse tree
	 */
	void exitYpos(@NotNull Bio.YposContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#cellActuation}.
	 * @param ctx the parse tree
	 */
	void enterCellActuation(@NotNull Bio.CellActuationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#cellActuation}.
	 * @param ctx the parse tree
	 */
	void exitCellActuation(@NotNull Bio.CellActuationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#sinks}.
	 * @param ctx the parse tree
	 */
	void enterSinks(@NotNull Bio.SinksContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#sinks}.
	 * @param ctx the parse tree
	 */
	void exitSinks(@NotNull Bio.SinksContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#nets}.
	 * @param ctx the parse tree
	 */
	void enterNets(@NotNull Bio.NetsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#nets}.
	 * @param ctx the parse tree
	 */
	void exitNets(@NotNull Bio.NetsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#endTiming}.
	 * @param ctx the parse tree
	 */
	void enterEndTiming(@NotNull Bio.EndTimingContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#endTiming}.
	 * @param ctx the parse tree
	 */
	void exitEndTiming(@NotNull Bio.EndTimingContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#bio}.
	 * @param ctx the parse tree
	 */
	void enterBio(@NotNull Bio.BioContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#bio}.
	 * @param ctx the parse tree
	 */
	void exitBio(@NotNull Bio.BioContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#detectors}.
	 * @param ctx the parse tree
	 */
	void enterDetectors(@NotNull Bio.DetectorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#detectors}.
	 * @param ctx the parse tree
	 */
	void exitDetectors(@NotNull Bio.DetectorsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#source}.
	 * @param ctx the parse tree
	 */
	void enterSource(@NotNull Bio.SourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#source}.
	 * @param ctx the parse tree
	 */
	void exitSource(@NotNull Bio.SourceContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#fluiddef}.
	 * @param ctx the parse tree
	 */
	void enterFluiddef(@NotNull Bio.FluiddefContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#fluiddef}.
	 * @param ctx the parse tree
	 */
	void exitFluiddef(@NotNull Bio.FluiddefContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#fluidID}.
	 * @param ctx the parse tree
	 */
	void enterFluidID(@NotNull Bio.FluidIDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#fluidID}.
	 * @param ctx the parse tree
	 */
	void exitFluidID(@NotNull Bio.FluidIDContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#dispenser}.
	 * @param ctx the parse tree
	 */
	void enterDispenser(@NotNull Bio.DispenserContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#dispenser}.
	 * @param ctx the parse tree
	 */
	void exitDispenser(@NotNull Bio.DispenserContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#routes}.
	 * @param ctx the parse tree
	 */
	void enterRoutes(@NotNull Bio.RoutesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#routes}.
	 * @param ctx the parse tree
	 */
	void exitRoutes(@NotNull Bio.RoutesContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#pinActuation}.
	 * @param ctx the parse tree
	 */
	void enterPinActuation(@NotNull Bio.PinActuationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#pinActuation}.
	 * @param ctx the parse tree
	 */
	void exitPinActuation(@NotNull Bio.PinActuationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#cellActuations}.
	 * @param ctx the parse tree
	 */
	void enterCellActuations(@NotNull Bio.CellActuationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#cellActuations}.
	 * @param ctx the parse tree
	 */
	void exitCellActuations(@NotNull Bio.CellActuationsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#timeConstraint}.
	 * @param ctx the parse tree
	 */
	void enterTimeConstraint(@NotNull Bio.TimeConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#timeConstraint}.
	 * @param ctx the parse tree
	 */
	void exitTimeConstraint(@NotNull Bio.TimeConstraintContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#ioport}.
	 * @param ctx the parse tree
	 */
	void enterIoport(@NotNull Bio.IoportContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#ioport}.
	 * @param ctx the parse tree
	 */
	void exitIoport(@NotNull Bio.IoportContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#pinActuations}.
	 * @param ctx the parse tree
	 */
	void enterPinActuations(@NotNull Bio.PinActuationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#pinActuations}.
	 * @param ctx the parse tree
	 */
	void exitPinActuations(@NotNull Bio.PinActuationsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#net}.
	 * @param ctx the parse tree
	 */
	void enterNet(@NotNull Bio.NetContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#net}.
	 * @param ctx the parse tree
	 */
	void exitNet(@NotNull Bio.NetContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#dropToFluid}.
	 * @param ctx the parse tree
	 */
	void enterDropToFluid(@NotNull Bio.DropToFluidContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#dropToFluid}.
	 * @param ctx the parse tree
	 */
	void exitDropToFluid(@NotNull Bio.DropToFluidContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#dispensers}.
	 * @param ctx the parse tree
	 */
	void enterDispensers(@NotNull Bio.DispensersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#dispensers}.
	 * @param ctx the parse tree
	 */
	void exitDispensers(@NotNull Bio.DispensersContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#fluids}.
	 * @param ctx the parse tree
	 */
	void enterFluids(@NotNull Bio.FluidsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#fluids}.
	 * @param ctx the parse tree
	 */
	void exitFluids(@NotNull Bio.FluidsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#xpos}.
	 * @param ctx the parse tree
	 */
	void enterXpos(@NotNull Bio.XposContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#xpos}.
	 * @param ctx the parse tree
	 */
	void exitXpos(@NotNull Bio.XposContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#droplets}.
	 * @param ctx the parse tree
	 */
	void enterDroplets(@NotNull Bio.DropletsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#droplets}.
	 * @param ctx the parse tree
	 */
	void exitDroplets(@NotNull Bio.DropletsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#sink}.
	 * @param ctx the parse tree
	 */
	void enterSink(@NotNull Bio.SinkContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#sink}.
	 * @param ctx the parse tree
	 */
	void exitSink(@NotNull Bio.SinkContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#timing}.
	 * @param ctx the parse tree
	 */
	void enterTiming(@NotNull Bio.TimingContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#timing}.
	 * @param ctx the parse tree
	 */
	void exitTiming(@NotNull Bio.TimingContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(@NotNull Bio.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(@NotNull Bio.AssignmentContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#blockages}.
	 * @param ctx the parse tree
	 */
	void enterBlockages(@NotNull Bio.BlockagesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#blockages}.
	 * @param ctx the parse tree
	 */
	void exitBlockages(@NotNull Bio.BlockagesContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#pinID}.
	 * @param ctx the parse tree
	 */
	void enterPinID(@NotNull Bio.PinIDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#pinID}.
	 * @param ctx the parse tree
	 */
	void exitPinID(@NotNull Bio.PinIDContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#target}.
	 * @param ctx the parse tree
	 */
	void enterTarget(@NotNull Bio.TargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#target}.
	 * @param ctx the parse tree
	 */
	void exitTarget(@NotNull Bio.TargetContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#blockage}.
	 * @param ctx the parse tree
	 */
	void enterBlockage(@NotNull Bio.BlockageContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#blockage}.
	 * @param ctx the parse tree
	 */
	void exitBlockage(@NotNull Bio.BlockageContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#dropletID}.
	 * @param ctx the parse tree
	 */
	void enterDropletID(@NotNull Bio.DropletIDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#dropletID}.
	 * @param ctx the parse tree
	 */
	void exitDropletID(@NotNull Bio.DropletIDContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#route}.
	 * @param ctx the parse tree
	 */
	void enterRoute(@NotNull Bio.RouteContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#route}.
	 * @param ctx the parse tree
	 */
	void exitRoute(@NotNull Bio.RouteContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#grid}.
	 * @param ctx the parse tree
	 */
	void enterGrid(@NotNull Bio.GridContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#grid}.
	 * @param ctx the parse tree
	 */
	void exitGrid(@NotNull Bio.GridContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#pinAssignments}.
	 * @param ctx the parse tree
	 */
	void enterPinAssignments(@NotNull Bio.PinAssignmentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#pinAssignments}.
	 * @param ctx the parse tree
	 */
	void exitPinAssignments(@NotNull Bio.PinAssignmentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#beginTiming}.
	 * @param ctx the parse tree
	 */
	void enterBeginTiming(@NotNull Bio.BeginTimingContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#beginTiming}.
	 * @param ctx the parse tree
	 */
	void exitBeginTiming(@NotNull Bio.BeginTimingContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#position}.
	 * @param ctx the parse tree
	 */
	void enterPosition(@NotNull Bio.PositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#position}.
	 * @param ctx the parse tree
	 */
	void exitPosition(@NotNull Bio.PositionContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#detector}.
	 * @param ctx the parse tree
	 */
	void enterDetector(@NotNull Bio.DetectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#detector}.
	 * @param ctx the parse tree
	 */
	void exitDetector(@NotNull Bio.DetectorContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#gridblock}.
	 * @param ctx the parse tree
	 */
	void enterGridblock(@NotNull Bio.GridblockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#gridblock}.
	 * @param ctx the parse tree
	 */
	void exitGridblock(@NotNull Bio.GridblockContext ctx);

	/**
	 * Enter a parse tree produced by {@link Bio#detector_spec}.
	 * @param ctx the parse tree
	 */
	void enterDetector_spec(@NotNull Bio.Detector_specContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bio#detector_spec}.
	 * @param ctx the parse tree
	 */
	void exitDetector_spec(@NotNull Bio.Detector_specContext ctx);
}