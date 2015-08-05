// Generated from /home/keszocze/proggs/BioViz/lib/parser/grammar/Bio.g4 by ANTLR 4.2.2
package de.bioviz.parser.generated;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Bio}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BioVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Bio#ypos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYpos(@NotNull Bio.YposContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#cellActuation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCellActuation(@NotNull Bio.CellActuationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#sinks}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSinks(@NotNull Bio.SinksContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#nets}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNets(@NotNull Bio.NetsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#endTiming}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndTiming(@NotNull Bio.EndTimingContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#bio}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBio(@NotNull Bio.BioContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#detectors}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetectors(@NotNull Bio.DetectorsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSource(@NotNull Bio.SourceContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#fluiddef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFluiddef(@NotNull Bio.FluiddefContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#fluidID}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFluidID(@NotNull Bio.FluidIDContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#dispenser}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDispenser(@NotNull Bio.DispenserContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#routes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoutes(@NotNull Bio.RoutesContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#pinActuation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPinActuation(@NotNull Bio.PinActuationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#cellActuations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCellActuations(@NotNull Bio.CellActuationsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#timeConstraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeConstraint(@NotNull Bio.TimeConstraintContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#ioport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIoport(@NotNull Bio.IoportContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#pinActuations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPinActuations(@NotNull Bio.PinActuationsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#net}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNet(@NotNull Bio.NetContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#dropToFluid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDropToFluid(@NotNull Bio.DropToFluidContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#dispensers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDispensers(@NotNull Bio.DispensersContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#fluids}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFluids(@NotNull Bio.FluidsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#xpos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpos(@NotNull Bio.XposContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#droplets}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDroplets(@NotNull Bio.DropletsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#sink}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSink(@NotNull Bio.SinkContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#timing}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiming(@NotNull Bio.TimingContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(@NotNull Bio.AssignmentContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#blockages}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockages(@NotNull Bio.BlockagesContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#pinID}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPinID(@NotNull Bio.PinIDContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget(@NotNull Bio.TargetContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#blockage}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockage(@NotNull Bio.BlockageContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#dropletID}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDropletID(@NotNull Bio.DropletIDContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#route}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoute(@NotNull Bio.RouteContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#grid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGrid(@NotNull Bio.GridContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#pinAssignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPinAssignments(@NotNull Bio.PinAssignmentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#beginTiming}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeginTiming(@NotNull Bio.BeginTimingContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#position}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPosition(@NotNull Bio.PositionContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#detector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetector(@NotNull Bio.DetectorContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#gridblock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGridblock(@NotNull Bio.GridblockContext ctx);

	/**
	 * Visit a parse tree produced by {@link Bio#detector_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetector_spec(@NotNull Bio.Detector_specContext ctx);
}