grammar Bio;
import CommonLexerRules;



bio : (grid|fluids|NEWLINE)+ ;

grid : 'grid'  NEWLINE (gridblock NEWLINE)+ 'end' ;
fluids: 'fluids'  NEWLINE (fluiddef NEWLINE)+ 'end';
droplet_positions: 'droplet positions' NEWLINE (drop_movement NEWLINE)+ 'end';


drop_movement: ID spacing (ID spacing)? position+;

fluiddef: ID spacing Identifier;


spacing: ( ' '|'\t')+ ;
optspacing: (' '|'\t')*;

gridblock: position spacing position;
position: '(' ID ',' ID ')' optspacing;


