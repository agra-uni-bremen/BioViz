grammar Bio;
import CommonLexerRules;



bio : (grid|fluids|NEWLINE)+ ;

grid : 'grid'  NEWLINE (gridblock NEWLINE)+ 'end' ;
fluids: 'fluids'  NEWLINE (fluiddef NEWLINE)+ 'end';

fluiddef: ID spacing Identifier;


spacing: ( ' '|'\t')+ ;
optspacing: (' '|'\t')*;

gridblock: position spacing position;
position: '(' ID ',' ID ')' optspacing;


