lexer grammar BioLexerGrammar;

@lexer::members {
    public static final int COMMENT = 1;
    public static final int ANNOTATION = 2;
}

Sinks: 'sinks';
Droplets: 'droplets';
PinAssignments: 'pin assignments';
Fluids: 'fluids';
Blockages: 'blockages';
Nets: 'nets';
Routes: 'routes';
Grid: 'grid';
Dispensers: 'dispensers';
Detectors: 'detectors';
Mixers: 'mixers';

CellActuations: 'cell actuations';
PinActuations: 'pin actuations';

Direction: 'L' | 'R' | 'U' | 'D' | 'N' | 'S' | 'E' | 'W';


END: 'end';

Integer: [0-9]+ ;
Identifier: [a-zA-Z]+ ;
Annotation: '#!' .*? Newlines -> channel(ANNOTATION);
Comment: '#' .*? Newlines -> channel(COMMENT);

Newlines: NEWLINE+;
NEWLINE: '\r'? '\n' ;
WS: [ \t]+ -> skip;


LParen: '(';
RParen: ')';

LBracket: '[';
RBracket: ']';
Dash: '-'+;

Comma: ',';
Asterisk: '*';
Arrow: '->' ;
Colon: ':' -> mode(ACTUATION);

mode ACTUATION;
ActuationVector: ('1'|'0'|'X')+ -> mode(DEFAULT_MODE);

// Antlr4 is annoying, I have to specify all lexer rules again as
// no sharing between modes seems possible
WhiteSpaceInActuationMode: [ \t]+ -> skip;
