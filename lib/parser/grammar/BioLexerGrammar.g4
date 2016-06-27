lexer grammar BioLexerGrammar;


@lexer::members {
    public final static int ANNOTATIONS = 1;
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
Annotations: 'annotations';

CellActuations: 'cell actuations';
PinActuations: 'pin actuations';

Direction: 'L' | 'R' | 'U' | 'D' | 'N' | 'S' | 'E' | 'W';


END: 'end';

Integer: [0-9]+ ;
Identifier: [a-zA-Z]+ ;
Comment: '#' .*? Newlines -> channel(ANNOTATIONS);

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

LessThan : '<' -> mode(AREAANNOTATION);

mode ACTUATION;
ActuationVector: ('1'|'0'|'X')+ -> mode(DEFAULT_MODE);

mode AREAANNOTATION;
AreaAnnotationText: (Identifier|Integer|WS)+ -> mode(DEFAULT_MODE);

// Antlr4 is annoying, I have to specify all lexer rules again as
// no sharing between modes seems possible
WhiteSpaceInActuationMode: [ \t]+ -> skip;
