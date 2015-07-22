parser grammar Bio;

options { tokenVocab=BioLexerGrammar; }


bio: (grid|routes|pinActuations|cellActuations|blockages|pinAssignments|fluids|droplets|NEWLINE)+;

// Definition of the grid

grid : Grid  NEWLINE+ (gridblock NEWLINE+)+ END ;
gridblock: position position;


// Definition of droplet movements
routes : Routes NEWLINE (route NEWLINE)+ END;
route: dropletID starttime? position+;


// Definition of actuation vectors
//
pinActuations: PinActuations NEWLINE+ (pinActuation NEWLINE+)+ END ;
pinActuation: pinID Colon ActuationVector;


cellActuations: CellActuations NEWLINE (cellActuation NEWLINE)+ END;
cellActuation: position Colon ActuationVector;

nets: Nets NEWLINE (net NEWLINE)+ END;
net: source (Comma source)* Arrow target;
source: position starttime?;
target: position timingConstraint?;
timingConstraint: LBracket Integer RBracket;

// Definition of blockages
blockages: Blockages NEWLINE (blockage NEWLINE)+ END;
blockage: position position timing?;
timing: LParen beginTiming Comma endTiming RParen;
beginTiming: Integer | Asterisk;
endTiming: Integer | Asterisk;

// Definition of fluid types
//
// Here you define what type of fluid (e.g. blood, serum) a certain
// fluid ID is
fluids: Fluids  NEWLINE (fluiddef NEWLINE)+ END;
fluiddef: Integer Identifier;


// Pin assignment
pinAssignments: PinAssignments NEWLINE (assignment NEWLINE)+ END;
assignment: position pinID;


// Mapping of droplet IDs to fluid IDs
droplets: Droplets NEWLINE (dropToFluid NEWLINE)+ END;
dropToFluid: dropletID fluidID;



// Common parser rules
dropletID: Integer;
fluidID: Integer;
pinID: Integer;
position: LParen xpos Comma ypos RParen;
xpos: Integer;
ypos: Integer;
starttime: LBracket Integer RBracket;

 // Lexer rules
//Integer: [0-9]+ ;
//Identifier: [a-zA-Z]+ ;
//Comment: '#' .*? '\r'? '\n' -> skip;
//NEWLINE: '\r'? '\n' ;
//WS: [ \t]+ -> skip;

