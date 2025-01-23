import java.util.ArrayList;
import java.lang.Math;


// Handles all vars/statements in the truth table; is able to output the truth table
public class TruthTable {
    // vars = ArrayList of variables to be used in the TT
    ArrayList<String> vars = new ArrayList<>();

    // statements = ArrayList of statements to be compared in the TT
    ArrayList<String> statements = new ArrayList<>();

    // ~ V ^ [var] () = x >
    char[] validEvaluators = {'v', '^', '=', 'x', '>'};

    char[] validTools = {'v', '^', '(', ')', '~', '=', 'x', '>'};

    // default constructor
    TruthTable(){
    }

    // returns vars size
    public int vSize(){
        return vars.size();
    }

    // returns statements size
    public int sSize(){
        return statements.size();
    }

    // adds var into arrayList if it is a valid single char
    // returns true if it adds it; false otherwise
    public boolean addVar(String var){
        // A = 65; Z = 90; A-Z is in between
        int key = java.awt.event.KeyEvent.getExtendedKeyCodeForChar(var.charAt(0));

        // making sure the variable is within the range of letters; not equal to 'x' or 'v' since they are evaluators
        if(var.length() == 1 && key <= 122 && key >= 65 && key != 86 && key != 88){

            // making sure the var is not already within the list of known vars
            for(int i = 0; i < vars.size(); i++){
                if(var.charAt(0) == vars.get(i).charAt(0)){
                    return false;
                }
            }

            // otherwise adding the var in and returning true
            vars.add(var);
            return true;
        }
        return false;
    }

    // parses the statement for variables to add to the list of vars temporarily
    // add statements into arrayList; returns true if it is a valid statement and has been added
    // if it is an invalid statement the variables which were added are removed
    // valid statement:   ~(PvQ)^R
    // invalid statement: ~()PQ
    public boolean addState(String state){
        char[] cState;
        cState = state.toCharArray();

        // this var will keep track of how many we have added, so if the statement returns false we know how many to remove
        int addedVars = 0;

        // adding vars into the arraylist for vars
        for(int i = 0; i < cState.length; i++){
            if(addVar(state.substring(i, i + 1))){
                addedVars++;
            }
        }

        // checking whether the statement is valid
        if(validState(cState)){
            statements.add(state);
            return true;
        }

        // removing the variables we added if it is invalid
        while(addedVars > 0){
            vars.remove(vars.size() - 1);
            addedVars--;
        }

        return false;
    }

    public void printTable(){
        // SBC = total number of spaces between vars
        int SBC = 8;
        int stateChars = 0;

        // T = true when there should be a T in table; F when otherwise
        // o = string of this;
        boolean T;
        String o;

        // this will be true when ALL statements in a row are T
        boolean truth;

        // printing top line with vars
        for(int i = 0; i < vars.size(); i++){
            System.out.print("   " + vars.get(i) + "   |");
        }

        // printing top line with statements; adding the length of statements into stateChars
        for(int i = 0; i < statements.size(); i++){
            System.out.print("   " + statements.get(i) + "   |");
            stateChars += statements.get(i).length();
        }
        System.out.print("   All Statements True");
        System.out.println();

        // second line; auto-prints correct number of lines for spacing based on num of vars/statements
        for(int i = 0; i < vars.size()*SBC + stateChars + statements.size()*7 + 3; i++){
            System.out.print("-");
        }
        System.out.println();

        // size of height is 2^vars
        for(int h = 0; h < Math.pow(2,vars.size()); h++){

            // if this remains true for an entire row we know every statement returned true
            truth = true;

            // index for every var
            for(int i = 0; i < vars.size(); i++){

                // i = index = horizontal index
                // h = height = vertical index
                // 2^index is the number of T's and F's in a "cluster" for every index
                // thus we take the floor of the height/2^index; if it is even we have a T; odd means F
                T = ( Math.floor((h/(Math.pow(2, i)) % 2)) == 0 );

                // setting o based on T
                if(T){
                    o = "T";
                }
                else {
                    o = "F";
                }

                // printing "T" or "F" in the table
                System.out.print("   " + o + "   |");
            }

            // index for every statement
            for(int i = 0; i < statements.size(); i++){

                // getting the correct value based on which statement we have
                // it is based on the index i and the height h since we need to know the values of the variables within the statement
                T = evaluateState(statements.get(i).toCharArray(), h);

                // setting o based on T; if T is false then we set truth to false
                if(T){
                    o = "T";
                }
                else {
                    truth = false;
                    o = "F";
                }

                // adding in the correct amount of spaces based on the length of each statement
                int n = (statements.get(i).length() + 5) / 2;
                System.out.format("%1$"+n+"s", "");
                System.out.print(o);

                // if the statements have an even number of chars this adds 1 extra space on the right
                System.out.format("%1$"+(n+((statements.get(i).length() + 1) % 2))+"s", "");
                System.out.print("|");
            }

            // if every statement was T in the entire row then this marks it
            if(truth){
                System.out.print("  <--");
            }

            System.out.println();
        }
    }

    // returns a char list based on the original in the given range
    // if either indices are out of bounds returns the original list
    // NOTE: this is inclusive of both indices
    public char[] copyRange(char[] original, int from, int to){
        // out of bounds checks
        if(from < 0 || to >= original.length){
            return original;
        }

        // putting new list in
        char[] nList = new char[to - from + 1]; // + 1 since we include the indices at both sides
        for(int i = 0; i < nList.length; i++){
            nList[i] = original[from + i];
        }

        return nList;
    }

    // indexOf compares an ArrayList of type string and a char, returning the index of char in string
    // this assumes the ArrayList is filled with single character strings (aka vars list)
    // returns -1 if not found
    public int indexOf(ArrayList<String> var, char c){
        for(int i = 0; i < var.size(); i++){
            if(var.get(i).charAt(0) == c){
                return i;
            }
        }
        return -1;
    }

    // given a statement, return its boolean expression at any height in the TT
    // valid expressions: ~ v ^ [var] () = x >
    // negation; or; and; variables; parentheses; iff; xor; if then
    private boolean evaluateState(char[] state, int h){
        // fundamentally it finds the closest evaluator to the end of the array that is outside of parentheses
        // and splits the array into two parts: before the evaluator and after, and then recursively calls
        // this method on both sides of the evaluator and returns back up once it has singled out variables
        // LSE = least significant evaluator; we are trying to find this to recurse (split both sides)

        // index = the index of the LSE we will split
        int index = 0;

        // i = the index of the variable in the vars list in the base cases
        int i;
        boolean T;

        // base case 1: single var being returned
        if(state.length == 1){
            i = indexOf(vars, state[0]);
            T = ( Math.floor((h/(Math.pow(2, i)) % 2)) == 0 );

            return T;
        }

        // base case 2: the negation of a single var being returned
        else if(state.length == 2){
            i = indexOf(vars, state[1]);
            T = ( Math.floor((h/(Math.pow(2, i)) % 2)) == 0 );

            return !T;
        }

        // finding the index where we need to split the evaluator
        // case 1: no parentheses at the end (simpler since the LSE will always be either 2 or 3 back from the end)
        // if it is 3 back from the end, it means our current index will be on a negation, we will fix this later
        if(state[state.length - 1] != ')'){
            index = state.length - 2;
        }

        // case 2: parentheses at the end
        else {
            // going backwards until we find the index of the parentheses opening, then we know index of LSE will be this parentheses minus 1
            // the index will equal -1 when there are parentheses on both ends -> [(PvQ)]
            // the index will equal 0 when there are parentheses on both ends and a negation at the very start -> [~(PvQ)]
            int count = 1;
            for(int j = state.length - 2; j >= 0; j--) {

                // if we find another closed parentheses add 1 to the count
                if (state[j] == ')') {
                    count++;
                }

                // if we find an open parentheses subtract 1 from the count
                else if (state[j] == '(') {
                    count--;
                }

                // when the count hits 0 set the index and break
                if (count == 0) {
                    index = j - 1;
                    break;
                }
            }

        }

        // index may == -1; if so we need to remove the parentheses at both ends and recurse
        // (this means we have unnecessary parentheses)
        if(index == -1) {
            return evaluateState(copyRange(state, 1, state.length - 2), h);
        }

        // index may == 0; this means there are parentheses on each end AND a negation over the entire thing
        // return the same as above except the negation of it
        else if(index == 0){
            return !evaluateState(copyRange(state, 2, state.length - 2), h);
        }

        // if the index is currently set to a negation, subtract 1 to get to the evaluator
        if(state[index] == '~'){
            index--;
        }

        // now we know our index = the LSE, so we split both sides and recurse
        // evaluator 1: [^] AND
        if(state[index] == '^'){
            return (evaluateState(copyRange(state, 0, index - 1), h)
                    && evaluateState(copyRange(state, index + 1, state.length - 1), h));
        }

        // evaluator 2: [v] OR
        else if(state[index] == 'v'){
            return (evaluateState(copyRange(state, 0, index - 1), h)
                    || evaluateState(copyRange(state, index + 1, state.length - 1), h));
        }

        // evaluator 3: [x] XOR
        else if(state[index] == 'x'){
            return (!evaluateState(copyRange(state, 0, index - 1), h)
                    && evaluateState(copyRange(state, index + 1, state.length - 1), h))
                    ||
                    (evaluateState(copyRange(state, 0, index - 1), h)
                            && !evaluateState(copyRange(state, index + 1, state.length - 1), h));
        }

        // evaluator 4: [=] iff
        else if(state[index] == '='){
            return (evaluateState(copyRange(state, 0, index - 1), h)
                    == evaluateState(copyRange(state, index + 1, state.length - 1), h));
        }

        // evaluator 5: [>] if then
        else if (state[index] == '>'){
            return (!evaluateState(copyRange(state, 0, index - 1), h)
                    || evaluateState(copyRange(state, index + 1, state.length - 1), h));
        }

        // given the statement should always be valid it should never reach this point, but just in case
        else {
            throw new IndexOutOfBoundsException();
        }
    }

    // checks rules of valid statements; returns true if given statement is valid; false otherwise
    public boolean validState(char[] state){
        /*    Rules of valid statements:
            1. Every char must be within a list of valid symbols
            2. Every negation precedes a variable or open parentheses
            3. Every variable and closed parentheses has either an evaluator or a closed parentheses following it
            4. Every open parentheses has either a negation, variable, or open parentheses following it
            5. Every open/closed parentheses has its adjacent pair and vice versa
         */


        int VALID_VARIABLES = vars.size();

        // Valid Symbols list consists of valid tools and valid variables
        char[] vSymbols = new char[validTools.length + VALID_VARIABLES];

        // placing each char within the list
        for(int i = 0; i < validTools.length; i++){
            vSymbols[i] = validTools[i];
        }
        for(int i = validTools.length; i < VALID_VARIABLES + validTools.length; i++){
            vSymbols[i] = vars.get(i - validTools.length).toCharArray()[0];
        }

        boolean t;
        int pCount = 0;     // this var will count parentheses

        // iterating through the statement to validate it
        for(int i = 0; i < state.length; i++){

            // Rule 1: each char in the given statement is a valid symbol
            t = false;
            for(int j = 0; j < vSymbols.length; j++){
                if(state[i] == vSymbols[j]){
                    t = true;
                    break;
                }
            }
            if(!t){
                return false;
            }

            // Rule 2: every negation precedes a variable or open parentheses; we are trying to find it to be false
            if(state[i] == '~' && ((i == state.length - 1) || (state[i + 1] != '(' && !validVar(state[i + 1])))){
                return false;
            }

            // Rule 3: every variable and closed parentheses has either an evaluator or a closed parentheses following it
            if((validVar(state[i]) || state[i] == ')') && i != state.length - 1 && !validEval(state[i + 1]) && state[i + 1] != ')'){
                return false;
            }

            // Rule 4: every open parentheses has either a negation, variable, or open parentheses following it
            if(state[i] == '(' && (i == state.length - 1 || (!validVar(state[i + 1]) && state[i + 1] != '~' && state[i + 1] != '('))){
                return false;
            }

            // Rule 5: every closed parentheses has its open adjacent pair and vice versa
            // everytime we find a close parentheses subtract 1 from pCount
            // everytime we find an open parentheses add 1 to pCount
            // return false if pCount != 0 after the entire statement has been checked
            if(state[i] == ')'){
                pCount--;
                // if pCount is ever < 0 we had a closed parentheses before open
                if(pCount < 0){
                    return false;
                }
            }
            else if(state[i] == '('){
                pCount++;
            }
        }

        return pCount == 0;
    }


    // checks whether the given char is within the list of variables
    public boolean validVar(char c){
        for(int i = 0; i < vars.size(); i++){
            if(c == vars.get(i).toCharArray()[0]){
                return true;
            }
        }
        return false;
    }


    // checks whether the given char is within the list of evaluators
    public boolean validEval(char c){
        for(int i = 0; i < validEvaluators.length; i++){
            if(c == validEvaluators[i]){
                return true;
            }
        }
        return false;
    }
}
