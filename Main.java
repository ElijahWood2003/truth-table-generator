import java.util.Scanner;

/*
    TruthTable: Input of the given valid expressions with variables in a valid logic statement;
                press enter twice when you are done inputting statements to print truth table
    - validates statements, tells the user if they are invalid
    - automatically adds variables to the truth table
    - formats the truth table so it is readable for the user
    - highlights lines with all True statements
 */

public class Main {
    public static void main (String[] args){

        // TruthTable class
        TruthTable tt = new TruthTable();

        // other vars
        Scanner input = new Scanner(System.in);
        boolean cont = false;
        boolean add;
        String output;

        // outputting the list of valid expressions
        System.out.println("Valid expressions: ~ v ^ [var] () = x >\n" +
                "negation; or; and; variables; parentheses; iff; xor; if then \n");

        System.out.println("Write statements using needed variables; press enter when done: ");

        // getting statements being used for TT
        while(!cont){
            output = input.nextLine();

            // if the output is empty then continue
            if(output.compareTo("") == 0){
                cont = true;
            }

            // otherwise check if the output is a valid statement and let the user know if it has been added
            else {
                add = tt.addState(output);

                if(add){
                    System.out.println("Statement added;   # of statements: " + tt.sSize() + ";   # of variables: " + tt.vSize() + ";");
                }
                else {
                    System.out.println("Please input a valid statement.");
                }

            }
        }

        // closing the scanner
        input.close();

        System.out.println();

        // printing the final truth table
        tt.printTable();
        
    }
}
