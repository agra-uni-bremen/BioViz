lexer grammar BioLexerGrammar;

Integer: [0-9]+ ;
Identifier: [a-zA-Z]+ ;
Comment: '#' .*? '\r'? '\n' -> skip;
NEWLINE: '\r'? '\n' ;
WS: [ \t]+ -> skip;

END: 'end' ;//-> mode(DEFAULT_MODE);
PinActuations: 'pin actuations' ;//-> mode(ACTUATION);
CellActuations: 'cell'? 'actuations' ;//-> mode(ACTUATION);


//mode ACTUATION;
ActuationVector: ('1'|'0'|'X')+;
