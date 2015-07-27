parser grammar Bio;


options { tokenVocab=BioLexerGrammar; }


bio: (grid|nets|routes|pinActuations|cellActuations|blockages|pinAssignments|fluids|droplets|Newlines)+;

// Definition of the grid

grid : Grid  Newlines (gridblock Newlines)+ END ;
gridblock: position position;


// Definition of droplet movements
routes : Routes Newlines (route Newlines)+ END;
route: dropletID timeConstraint? position+;


// Definition of actuation vectors
//
pinActuations: PinActuations Newlines+ (pinActuation Newlines+)+ END ;
pinActuation: pinID Colon ActuationVector;


cellActuations: CellActuations Newlines (cellActuation Newlines)+ END;
cellActuation: position Colon ActuationVector;

nets: Nets Newlines (net Newlines)+ END;
net: source (Comma source)* Arrow target;
source: position timeConstraint?;
target: position timeConstraint?;

// Definition of blockages
blockages: Blockages Newlines (blockage Newlines)+ END;
blockage: position position timing?;
timing: LParen beginTiming Comma endTiming RParen;
beginTiming: Integer | Asterisk;
endTiming: Integer | Asterisk;

// Definition of fluid types
//
// Here you define what type of fluid (e.g. blood, serum) a certain
// fluid ID is
fluids: Fluids  Newlines (fluiddef Newlines)+ END;
fluiddef: Integer Identifier;


// Pin assignment
pinAssignments: PinAssignments Newlines (assignment Newlines)+ END;
assignment: position pinID;


// Mapping of droplet IDs to fluid IDs
droplets: Droplets Newlines (dropToFluid Newlines)+ END;
dropToFluid: dropletID fluidID;



// Common parser rules
dropletID: Integer;
fluidID: Integer;
pinID: Integer;
position: LParen xpos Comma ypos RParen;
xpos: Integer;
ypos: Integer;
timeConstraint: LBracket Integer RBracket;


