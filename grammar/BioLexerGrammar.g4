lexer grammar BioLexerGrammar;




Sinks: 'sinks';
Droplets: 'droplets';
PinAssignments: 'pin assignments';
Fluids: 'fluids';
Blockages: 'blockages';
Nets: 'nets';
Routes: 'routes';
Grid: 'grid';
Dispensers: 'dispensers';

CellActuations: 'cell actuations';
PinActuations: 'pin actuations';

Direction: 'L' | 'R' | 'U' | 'D' | 'N' | 'S' | 'E' | 'W';


END: 'end';

Integer: [0-9]+ ;
Identifier: [a-zA-Z]+ ;
Comment: '#' .*? Newlines -> skip;

Newlines: NEWLINE+;
NEWLINE: '\r'? '\n' ;
WS: [ \t]+ -> skip;


LParen: '(';
RParen: ')';

LBracket: '[';
RBracket: ']';

Comma: ',';
Asterisk: '*';
Arrow: '->' ;
Colon: ':' -> mode(ACTUATION);

mode ACTUATION;
ActuationVector: ('1'|'0'|'X')+ -> mode(DEFAULT_MODE);

// Antlr4 is annoying, I have to specify all lexer rules again as
// no sharing between modes seems possible
WhiteSpaceInActuationMode: [ \t]+ -> skip;
