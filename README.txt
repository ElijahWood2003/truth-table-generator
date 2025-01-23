---Written by Elijah Wood in December 2023---
Text truth table generator written in Java. 
Functions:
    - Automatically adds new variables as you write them in statements.
    - Parses statements to check for valid / invalid statements
    - Outputs the truth table once any number of statements have been added
    - Arrows "<--" in the truth table to indicate all statements in this row are true

---Example Output---
Valid expressions: ~ v ^ [var] () = x >
negation; or; and; variables; parentheses; iff; xor; if then 

Write statements using needed variables; press enter when done: 
(PvQ) ^ Dfsf
Please input a valid statement.
(PvQ)^(~Z)
Statement added;   # of statements: 1;   # of variables: 3;
(~Z^~P)
Statement added;   # of statements: 2;   # of variables: 3;


   P   |   Q   |   Z   |   (PvQ)^(~Z)   |   (~Z^~P)   |   All Statements True
----------------------------------------------------------
   T   |   T   |   T   |       F        |      F      |
   F   |   T   |   T   |       F        |      F      |
   T   |   F   |   T   |       F        |      F      |
   F   |   F   |   T   |       F        |      F      |
   T   |   T   |   F   |       T        |      F      |
   F   |   T   |   F   |       T        |      T      |  <--
   T   |   F   |   F   |       T        |      F      |
   F   |   F   |   F   |       F        |      T      |