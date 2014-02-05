package de.dfki.revvisgdx;

public class Messages {
	public static String highlightBus = "Now highlighting bus $1";
	public static String highlightNoBus = "How highlighting no bus";
	
	public static String helpText =
			"b: highlight a bus\n"
			+ "c: colorize constant inputs\n"
			+ "C: Show/hide sub-circuits\n"
			+ "d: draw dark lines when used\n"
			+ "g: garbage lines color\n"
			+ "h: highlight hovered gate\n"
			+ "H: highlight hovered gate's moving rule\n"
			+ "m: show moving rule by gate color (red: left, green: right)\n"
			+ "M: show moving rule by line overlay\n"
			+ "n: neighbouring targets as groups (off / by variable / by bus)\n"
			+ "p: save screenshot\n"
			+ "s: square alignment\n"
			+ "S: show/hide gates\n"
			+ "u: colorize lines by usage\n"
			+ "v: draw vertical gate connectors\n"
			+ "w: toggle line width (full / usage / pixel)\n"
			+ "z: zoom to exactly 1 pixel / element (both axes)\n"
			+ "Z: zoom extents"
			+ "mouse wheel: zoom\n"
			+ "mouse wheel + shift: zoom horizontally\n"
			+ "mouse wheel + ctrl: zoom vertically\n"
			+ "left drag: move viewport\n"
			+ "\n"
			+ "1: set const/garbage preset\n"
			+ "2: set gates + usage preset";
}
