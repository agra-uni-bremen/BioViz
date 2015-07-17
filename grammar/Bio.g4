grammar Bio;
import CommonLexerRules;



bio : grid ;

grid : 'grid' NEWLINE (block NEWLINE)+ 'end' NEWLINE ;

block : position ' '+ position ;
position: '(' INTEGER ',' INTEGER ')' ;


