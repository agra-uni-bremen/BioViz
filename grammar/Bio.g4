grammar Bio;
import CommonLexerRules;






// Definition of the grid

grid : 'grid'  NEWLINE (gridblock NEWLINE)+ 'end' ;
gridblock: position position;
position: '(' ID ',' ID ')';


// Definition of droplet movements
routes : 'routes' NEWLINE (route NEWLINE)+ 'end';
route: ID ('[' ID ']')? position+;



// Definition of fluid types

fluids: 'fluids'  NEWLINE (fluiddef NEWLINE)+ 'end';
fluiddef: ID Identifier;

 // Lexer rules
ID: [0-9]+ ;
Identifier: [a-zA-Z]+ ;
NEWLINE: '\r'? '\n' ;
WS: [ \t] -> skip;
