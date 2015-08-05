// Generated from /home/keszocze/proggs/BioViz/lib/parser/grammar/BioLexerGrammar.g4 by ANTLR 4.2.2
package de.bioviz.parser.generated;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BioLexerGrammar extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Sinks=1, Droplets=2, PinAssignments=3, Fluids=4, Blockages=5, Nets=6, 
		Routes=7, Grid=8, Dispensers=9, Detectors=10, CellActuations=11, PinActuations=12, 
		Direction=13, END=14, Integer=15, Identifier=16, Comment=17, Newlines=18, 
		NEWLINE=19, WS=20, LParen=21, RParen=22, LBracket=23, RBracket=24, Comma=25, 
		Asterisk=26, Arrow=27, Colon=28, ActuationVector=29, WhiteSpaceInActuationMode=30;
	public static final int ACTUATION = 1;
	public static String[] modeNames = {
		"DEFAULT_MODE", "ACTUATION"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'sinks'", "'droplets'", "'pin assignments'", "'fluids'", "'blockages'", 
		"'nets'", "'routes'", "'grid'", "'dispensers'", "'detectors'", "'cell actuations'", 
		"'pin actuations'", "Direction", "'end'", "Integer", "Identifier", "Comment", 
		"Newlines", "NEWLINE", "WS", "'('", "')'", "'['", "']'", "','", "'*'", 
		"'->'", "':'", "ActuationVector", "WhiteSpaceInActuationMode"
	};
	public static final String[] ruleNames = {
		"Sinks", "Droplets", "PinAssignments", "Fluids", "Blockages", "Nets", 
		"Routes", "Grid", "Dispensers", "Detectors", "CellActuations", "PinActuations", 
		"Direction", "END", "Integer", "Identifier", "Comment", "Newlines", "NEWLINE", 
		"WS", "LParen", "RParen", "LBracket", "RBracket", "Comma", "Asterisk", 
		"Arrow", "Colon", "ActuationVector", "WhiteSpaceInActuationMode"
	};


	public BioLexerGrammar(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BioLexerGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2 \u0102\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\20\6\20\u00bd\n\20\r\20\16\20\u00be\3\21\6\21\u00c2"+
		"\n\21\r\21\16\21\u00c3\3\22\3\22\7\22\u00c8\n\22\f\22\16\22\u00cb\13\22"+
		"\3\22\3\22\3\22\3\22\3\23\6\23\u00d2\n\23\r\23\16\23\u00d3\3\24\5\24\u00d7"+
		"\n\24\3\24\3\24\3\25\6\25\u00dc\n\25\r\25\16\25\u00dd\3\25\3\25\3\26\3"+
		"\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3"+
		"\35\3\35\3\35\3\35\3\36\6\36\u00f6\n\36\r\36\16\36\u00f7\3\36\3\36\3\37"+
		"\6\37\u00fd\n\37\r\37\16\37\u00fe\3\37\3\37\3\u00c9\2 \4\3\6\4\b\5\n\6"+
		"\f\7\16\b\20\t\22\n\24\13\26\f\30\r\32\16\34\17\36\20 \21\"\22$\23&\24"+
		"(\25*\26,\27.\30\60\31\62\32\64\33\66\348\35:\36<\37> \4\2\3\7\b\2FGN"+
		"NPPTUWWYY\3\2\62;\4\2C\\c|\4\2\13\13\"\"\4\2\62\63ZZ\u0108\2\4\3\2\2\2"+
		"\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2"+
		"\2\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2"+
		"\2\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2"+
		"\2\2(\3\2\2\2\2*\3\2\2\2\2,\3\2\2\2\2.\3\2\2\2\2\60\3\2\2\2\2\62\3\2\2"+
		"\2\2\64\3\2\2\2\2\66\3\2\2\2\28\3\2\2\2\2:\3\2\2\2\3<\3\2\2\2\3>\3\2\2"+
		"\2\4@\3\2\2\2\6F\3\2\2\2\bO\3\2\2\2\n_\3\2\2\2\ff\3\2\2\2\16p\3\2\2\2"+
		"\20u\3\2\2\2\22|\3\2\2\2\24\u0081\3\2\2\2\26\u008c\3\2\2\2\30\u0096\3"+
		"\2\2\2\32\u00a6\3\2\2\2\34\u00b5\3\2\2\2\36\u00b7\3\2\2\2 \u00bc\3\2\2"+
		"\2\"\u00c1\3\2\2\2$\u00c5\3\2\2\2&\u00d1\3\2\2\2(\u00d6\3\2\2\2*\u00db"+
		"\3\2\2\2,\u00e1\3\2\2\2.\u00e3\3\2\2\2\60\u00e5\3\2\2\2\62\u00e7\3\2\2"+
		"\2\64\u00e9\3\2\2\2\66\u00eb\3\2\2\28\u00ed\3\2\2\2:\u00f0\3\2\2\2<\u00f5"+
		"\3\2\2\2>\u00fc\3\2\2\2@A\7u\2\2AB\7k\2\2BC\7p\2\2CD\7m\2\2DE\7u\2\2E"+
		"\5\3\2\2\2FG\7f\2\2GH\7t\2\2HI\7q\2\2IJ\7r\2\2JK\7n\2\2KL\7g\2\2LM\7v"+
		"\2\2MN\7u\2\2N\7\3\2\2\2OP\7r\2\2PQ\7k\2\2QR\7p\2\2RS\7\"\2\2ST\7c\2\2"+
		"TU\7u\2\2UV\7u\2\2VW\7k\2\2WX\7i\2\2XY\7p\2\2YZ\7o\2\2Z[\7g\2\2[\\\7p"+
		"\2\2\\]\7v\2\2]^\7u\2\2^\t\3\2\2\2_`\7h\2\2`a\7n\2\2ab\7w\2\2bc\7k\2\2"+
		"cd\7f\2\2de\7u\2\2e\13\3\2\2\2fg\7d\2\2gh\7n\2\2hi\7q\2\2ij\7e\2\2jk\7"+
		"m\2\2kl\7c\2\2lm\7i\2\2mn\7g\2\2no\7u\2\2o\r\3\2\2\2pq\7p\2\2qr\7g\2\2"+
		"rs\7v\2\2st\7u\2\2t\17\3\2\2\2uv\7t\2\2vw\7q\2\2wx\7w\2\2xy\7v\2\2yz\7"+
		"g\2\2z{\7u\2\2{\21\3\2\2\2|}\7i\2\2}~\7t\2\2~\177\7k\2\2\177\u0080\7f"+
		"\2\2\u0080\23\3\2\2\2\u0081\u0082\7f\2\2\u0082\u0083\7k\2\2\u0083\u0084"+
		"\7u\2\2\u0084\u0085\7r\2\2\u0085\u0086\7g\2\2\u0086\u0087\7p\2\2\u0087"+
		"\u0088\7u\2\2\u0088\u0089\7g\2\2\u0089\u008a\7t\2\2\u008a\u008b\7u\2\2"+
		"\u008b\25\3\2\2\2\u008c\u008d\7f\2\2\u008d\u008e\7g\2\2\u008e\u008f\7"+
		"v\2\2\u008f\u0090\7g\2\2\u0090\u0091\7e\2\2\u0091\u0092\7v\2\2\u0092\u0093"+
		"\7q\2\2\u0093\u0094\7t\2\2\u0094\u0095\7u\2\2\u0095\27\3\2\2\2\u0096\u0097"+
		"\7e\2\2\u0097\u0098\7g\2\2\u0098\u0099\7n\2\2\u0099\u009a\7n\2\2\u009a"+
		"\u009b\7\"\2\2\u009b\u009c\7c\2\2\u009c\u009d\7e\2\2\u009d\u009e\7v\2"+
		"\2\u009e\u009f\7w\2\2\u009f\u00a0\7c\2\2\u00a0\u00a1\7v\2\2\u00a1\u00a2"+
		"\7k\2\2\u00a2\u00a3\7q\2\2\u00a3\u00a4\7p\2\2\u00a4\u00a5\7u\2\2\u00a5"+
		"\31\3\2\2\2\u00a6\u00a7\7r\2\2\u00a7\u00a8\7k\2\2\u00a8\u00a9\7p\2\2\u00a9"+
		"\u00aa\7\"\2\2\u00aa\u00ab\7c\2\2\u00ab\u00ac\7e\2\2\u00ac\u00ad\7v\2"+
		"\2\u00ad\u00ae\7w\2\2\u00ae\u00af\7c\2\2\u00af\u00b0\7v\2\2\u00b0\u00b1"+
		"\7k\2\2\u00b1\u00b2\7q\2\2\u00b2\u00b3\7p\2\2\u00b3\u00b4\7u\2\2\u00b4"+
		"\33\3\2\2\2\u00b5\u00b6\t\2\2\2\u00b6\35\3\2\2\2\u00b7\u00b8\7g\2\2\u00b8"+
		"\u00b9\7p\2\2\u00b9\u00ba\7f\2\2\u00ba\37\3\2\2\2\u00bb\u00bd\t\3\2\2"+
		"\u00bc\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf"+
		"\3\2\2\2\u00bf!\3\2\2\2\u00c0\u00c2\t\4\2\2\u00c1\u00c0\3\2\2\2\u00c2"+
		"\u00c3\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4#\3\2\2\2"+
		"\u00c5\u00c9\7%\2\2\u00c6\u00c8\13\2\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00cb"+
		"\3\2\2\2\u00c9\u00ca\3\2\2\2\u00c9\u00c7\3\2\2\2\u00ca\u00cc\3\2\2\2\u00cb"+
		"\u00c9\3\2\2\2\u00cc\u00cd\5&\23\2\u00cd\u00ce\3\2\2\2\u00ce\u00cf\b\22"+
		"\2\2\u00cf%\3\2\2\2\u00d0\u00d2\5(\24\2\u00d1\u00d0\3\2\2\2\u00d2\u00d3"+
		"\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\'\3\2\2\2\u00d5"+
		"\u00d7\7\17\2\2\u00d6\u00d5\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d8\3"+
		"\2\2\2\u00d8\u00d9\7\f\2\2\u00d9)\3\2\2\2\u00da\u00dc\t\5\2\2\u00db\u00da"+
		"\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00de"+
		"\u00df\3\2\2\2\u00df\u00e0\b\25\2\2\u00e0+\3\2\2\2\u00e1\u00e2\7*\2\2"+
		"\u00e2-\3\2\2\2\u00e3\u00e4\7+\2\2\u00e4/\3\2\2\2\u00e5\u00e6\7]\2\2\u00e6"+
		"\61\3\2\2\2\u00e7\u00e8\7_\2\2\u00e8\63\3\2\2\2\u00e9\u00ea\7.\2\2\u00ea"+
		"\65\3\2\2\2\u00eb\u00ec\7,\2\2\u00ec\67\3\2\2\2\u00ed\u00ee\7/\2\2\u00ee"+
		"\u00ef\7@\2\2\u00ef9\3\2\2\2\u00f0\u00f1\7<\2\2\u00f1\u00f2\3\2\2\2\u00f2"+
		"\u00f3\b\35\3\2\u00f3;\3\2\2\2\u00f4\u00f6\t\6\2\2\u00f5\u00f4\3\2\2\2"+
		"\u00f6\u00f7\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00f9"+
		"\3\2\2\2\u00f9\u00fa\b\36\4\2\u00fa=\3\2\2\2\u00fb\u00fd\t\5\2\2\u00fc"+
		"\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2"+
		"\2\2\u00ff\u0100\3\2\2\2\u0100\u0101\b\37\2\2\u0101?\3\2\2\2\f\2\3\u00be"+
		"\u00c3\u00c9\u00d3\u00d6\u00dd\u00f7\u00fe\5\b\2\2\4\3\2\4\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}