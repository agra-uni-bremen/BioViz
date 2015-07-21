grammar Bio;

options { tokenVocab=BioLexerGrammar; }


bio: (grid|routes|pinActuations|cellActuations|blockages|pinAssignments|fluids|droplets)+;

// Definition of the grid

grid : 'grid'  NEWLINE (gridblock NEWLINE)+ END ;
gridblock: position position;


// Definition of droplet movements
routes : 'routes' NEWLINE (route NEWLINE)+ END;
route: dropletID starttime? position+;


// Definition of actuation vectors
//
pinActuations: 'pin actuations' NEWLINE (pinActuation NEWLINE)+ END ;
pinActuation: pinID actuationVector;


cellActuations: 'cell'? 'actuations' NEWLINE (cellActuation NEWLINE)+ END;
cellActuation: position actuationVector;
actuationVector: ('1'|'0'|'X')+;

nets: 'nets' NEWLINE (net NEWLINE)+ END;
net: source (',' source)* '->' target;
source: position starttime?;
// target position with possible timing constraint
target: position timingConstraint?;
timingConstraint: '[' Integer ']';

// Definition of blockages
blockages: 'blockages' NEWLINE (blockage NEWLINE)+ END;
blockage: position position timing?;
timing: '(' beginTiming ',' endTiming ')';
beginTiming: Integer | '*';
endTiming: Integer | '*';

// Definition of fluid types
//
// Here you define what type of fluid (e.g. blood, serum) a certain
// fluid ID is
fluids: 'fluids'  NEWLINE (fluiddef NEWLINE)+ END;
fluiddef: Integer Identifier;


// Pin assignment
pinAssignments: 'pin assignments' NEWLINE (assignment NEWLINE)+ END;
assignment: position pinID;


// Mapping of droplet IDs to fluid IDs
droplets: 'droplets' NEWLINE (dropToFluid NEWLINE)+ END;
dropToFluid: dropletID fluidID;



// Common parser rules
dropletID: Integer;
fluidID: Integer;
pinID: Integer;
position: '(' xpos ',' ypos ')';
xpos: Integer;
ypos: Integer;
starttime: '[' Integer ']';

 // Lexer rules
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
