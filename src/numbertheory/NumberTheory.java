/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package numbertheory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class NumberTheory {

    /**
     * @param args the command line arguments
     */
    
    // Map for all the possible digits
    public static Map<Character, Integer> digits = new HashMap<Character, Integer>();
    // Map used for converting back to letters
    public static Map<Integer, Character> letters = new HashMap<Integer, Character>();
    // Arrays for storing the different types of digits
    public static char[] digitsReal = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    // Arrays for storing the two numbers
    public static char[] X;
    public static char[] Y;
    // Variables for comparing the two numbers
    public static boolean XleY, XlY, XgY;
    // Variable for storing the current operation
    public static String OPERATION;
    // Variable for storing the final sign of the computation
    public static char SIGN = ' ';
    // Variables for storing the signs of X and Y
    public static char SIGN_X;
    public static char SIGN_Y;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        // Name of the input file i.e: "example.txt"
        String inputFile = "example.txt";
        
        // Used for reading one line at a time from the buffer
        String line = "";
        
        // Variable for storing each line of text from the input file
        // Will be using a hash map (line number, string)
        Map<Integer, String> map = new HashMap<Integer, String>();
        
        // Array for all the possible operations
        String[] operations = {"add", "subtract", "multiply", "karatsuba"};
        // Array for all the possible bases
        String[] base = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
        
        // Variable for storing the number of lines
        int i = 0;
        
        try {
            
            // Declare a new FileReader for the inputFile
            FileReader fileReader = new FileReader(inputFile);
            
            // We wrap the FileReader in a BufferedReader for later reading line by line
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // Cycle through all the lines in the inputFile
            while((line = bufferedReader.readLine()) != null) {
                
                // Used for debugging to print the input file to the console
                //System.out.println(line);
                
                // Trim extra whitespaces from the beginning/end of the string
                    line = line.trim();
                
                // Store each line in the hash map
                // IMPORTANT: Stores all the lines that are not comments (do not start with # symbol)
                if(line.length() > 0 && line.charAt(0) != '#') {    
                    
                    // Add the new line to the map
                    map.put(i, line);
                    
                    // Increse the value of i (to keep track of the number of lines)
                    i++;
                }
            }
            
            // Close the inputFile
            bufferedReader.close();
            
        } catch(FileNotFoundException e) {
            
            // Catch the FileNotFoundException and inform user
            System.out.println("File " + inputFile + " was not found.");
            
            // Give the user a small hint as to what might have gone wrong
            System.out.println("Check if the input file is placed in the same directory and please name it example.txt");
            
        } catch(IOException e) {
            
            // Catch the IOException and inform user
            System.out.println("There was an error while reading the " + inputFile + " file.");
        }
        
        // Run all the setup for the program
        buildDigitsMap();
        
        // Solve the given operations one-by-one
        for(int j = 0; j < i; j++) {
            String radix = map.get(j).substring(8);
            String operation = map.get(j+1).substring(1,map.get(j+1).length()-1);
            String xNumber = map.get(j+2).substring(4);
            String yNumber = map.get(j+3).substring(4);
            
            // Convert the base from string to int
            int b = Integer.parseInt(radix);
            
            // Update the global operation
            OPERATION = operation;
            
            // Convert xNumber to an array of digits (in reverse order)
            // IMPORTANT: The array will have on position 0 the number of digits
            X = getDigits(xNumber);
            // Read the sign for X
            SIGN_X = (xNumber.charAt(0) == '-') ? '-' : '+';
            
            // Convert yNumber to an array of digits (in reverse order)
            // IMPORTANT: The array will have on position 0 the number of digits
            Y = getDigits(yNumber);
            // Read the sign for Y
            SIGN_Y = (yNumber.charAt(0) == '-') ? '-' : '+';
            
            // Compare X and Y so that X > Y (if not interchange the two vectors)
            // IMPORTANT: Here we also need to consider the special cases
            // i.e: add two numbers, the second one is negative means changing the operation to subtraction ?!
            compareXY();
            // Compute the sign of the final operation
            computeSign();
            
            switch(OPERATION) {
                case "add":
                    System.out.println("add");
                    add(X, Y, b);
                    break;
                case "subtract":
                    System.out.println("subtract");
                    subtract(X, Y, b);
                    break;
                case "multiply":
                    System.out.println("multiply");
                    multiply(X, Y, b);
                    break;    
                case "karatsuba":
                    //System.out.println("karatsuba");
                    break;
                default:
                    System.out.println("This is an invalid operation.");
            }
            
            // Skip to the next operation that needs to be computed
            j += 3;
        }
    }
    
    // Function for splitting numbers in one digit array
    // IMPORTANT: The array which is returned will contain the orginal digits in reverse order
    public static char[] getDigits(String number) {
        
        // Array for storing the digits
        char[] X;
        // Variable for storing the stop position in the number string
        int temp = 0;
        
        if(number.charAt(0) == '-') {
            X = new char[number.length()-1];
            temp = 1;
        } else {
            X = new char[number.length()];
            temp = 0;
        }
        
        // Index used to create the new array
        int j = 0;
        
        for(int i = number.length(); i > temp; i--) {
            
            X[j++] = number.charAt(i-1);
        }
        
        return X;
    }
    
    // Function for building the digits map; it maps each digit to its value
    // Future reference example: digits.get("b") will return 11
    public static void buildDigitsMap() {
        
        for(int i = 0; i < digitsReal.length; i++) {
            
            // Map each digit to a specific value
            digits.put(digitsReal[i], i);
            // Map each value to a specific letter
            letters.put(i, digitsReal[i]);
        }
    }
    
    public static void compareXY() {
        
        int i = X.length-1, j = 0;
        int x,y;
        // XleY is X <= Y; XlY is X < Y; XgY is X > Y
        XleY = XlY = XgY = false;
        
        if(X.length == Y.length) {
            x = digits.get(X[i]);
            y = digits.get(Y[i]);
            while(x == y && i > 0) {
                i -= 1;
                x = digits.get(X[i]);
                y = digits.get(Y[i]);
            }
            if(x < y) {
                changeArrays();
                XleY = XlY = true;
            }
            if(i == 0) {
                XleY = XlY = true;
            }
        } else if(X.length < Y.length) {
            changeArrays();
            XlY = true;
        } else {
            XgY = true;
        }
    }
    
    public static void changeArrays() {
        // Change the two arrays such that X > Y
        char[] Z = X;
        X = Y;
        Y = Z;
    }
    
    public static void computeSign() {
        // Program enters this function call when x <= y
        // IMPORTANT: See table below code to check for sign conversion
        // i.e: SIGN = '-'
        switch(OPERATION) {
            case "add":
                if(SIGN_X == '+' && SIGN_Y == '+') {
                    SIGN = '+';
                }
                if(SIGN_X == '+' && SIGN_Y == '-' && !XlY) {
                    SIGN = '+';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '+' && SIGN_Y == '-' &&  XlY) {
                    SIGN = '-';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '-' && SIGN_Y == '+' &&  XgY) {
                    SIGN = '-';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '-' && SIGN_Y == '+' && !XgY) {
                    SIGN = '+';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '-' && SIGN_Y == '-') {
                    SIGN = '-';
                }
                break;
            case "subtract":
                if(SIGN_X == '+' && SIGN_Y == '+' && !XlY) {
                    SIGN = '+';
                }
                if(SIGN_X == '+' && SIGN_Y == '+' &&  XlY) {
                    SIGN = '-';
                }
                if(SIGN_X == '+' && SIGN_Y == '-') {
                    SIGN = '+';
                    OPERATION = "add";
                }
                if(SIGN_X == '-' && SIGN_Y == '+') {
                    SIGN = '-';
                    OPERATION = "add";
                }
                if(SIGN_X == '-' && SIGN_Y == '-' && !XgY) {
                    SIGN = '+';
                }
                if(SIGN_X == '-' && SIGN_Y == '-' && XgY) {
                    SIGN = '-';
                }
                break;
            case "multiply":
                if(SIGN_X == '-' || SIGN_Y == '-') {
                    SIGN = '-';
                }
                if(SIGN_X == '-' && SIGN_Y == '-') {
                    SIGN = '+';
                }
                break;
            default:
                SIGN = '+';
        }
    }
    
    // Function for adding two numbers
    public static void add(char[] A, char[] B, int b) {
        
        int i, t = 0;
        ArrayList<Integer> X = new ArrayList<>();
        ArrayList<Character> Y = new ArrayList<>();
        int x, y;
        
        for(i = 0; (i < A.length) || (i < B.length) || (t != 0); i++, t/=b) {
            x = (i < A.length) ? digits.get(A[i]) : 0;
            y = (i < B.length) ? digits.get(B[i]) : 0;
            t += x + y;
            X.add(i, t % b);
        }
        
        // Update the new number of digits
        i -= 1;
        
        // Convert the number back to a char array and print to console
        // IMPORTANT: Now we also convert bigger digits to letters
        computeResult(X, i);
    }
    
    // Function for subtracting two numbers
    public static void subtract(char[] A, char[] B, int b) {
        
        int i, t = 0;
        ArrayList<Integer> X = new ArrayList<>();
        ArrayList<Character> Y = new ArrayList<>();
        int x, y;
        
        // IMPORTANT: A.length > B.length (arrange the two numbers before the function call)
        for(i = 0; i < A.length; i++) {
            x = digits.get(A[i]);
            y = (i < B.length) ? digits.get(B[i]) : 0;
            
            x -= y + t;
            t = (x < 0) ? 1 : 0;
            x += t * b;
            
            // Add the value to the new array list
            X.add(i, x);
        }
        
        while(X.size() > 1 && X.get(X.size()-1) == 0) {
            X.remove(X.size()-1);
        }
        
        // Convert the number back to a char array and print to console
        // IMPORTANT: Now we also convert bigger digits to letters
        computeResult(X, X.size()-1);
    }
    
    // Function for multiplying two numbers
    public static void multiply(char[] A, char[] B, int b) {
        
        ArrayList<Integer> X = new ArrayList<>();
        int i = 0,j = 0,t = 0;
        int x, y;
        
        for(i = 0; i < A.length; i++) {
            x = (i < A.length) ? digits.get(A[i]) : 0;
            for(j = 0; (j < B.length) || (t != 0); j++, t /= b) {
                y = (j < B.length) ? digits.get(B[j]) : 0;
                
                if(X.size() - 1 >= i+j) {
                    t += X.get(i+j) + x * y;
                    if(i+j <= X.size() - 1)
                        X.set(i+j, t % b);
                    else
                        X.add(i+j, t % b);
                } else {
                    t += x * y;
                    X.add(X.size(), t % b);
                }
            }
        }
        
        computeResult(X, X.size()-1);
    }
    
    // Function for multiplying two numbers using Karatsuba
    public static void karatsuba(char[] A, char[] B, int b) {
        // Declare two arraylists for each of the two numbers
        ArrayList<Character> X_hi = new ArrayList<>();
        ArrayList<Character> X_lo = new ArrayList<>();
        ArrayList<Character> Y_hi = new ArrayList<>();
        ArrayList<Character> Y_lo = new ArrayList<>();
        
        
        // We calculate the length of the two numbers
        // IMPORTANT: The two numbers have to be of equal length
        int length = A.length;
        // We calculate the first half of the number
        int hi = length / 2;
        // We calculate the second half of the number
        int lo = length - hi;
        
        // Split the first number in two halves
        
        // Split the second number in two halves
    }
    
    // Function for converting the computed array list back to chars
    // This function also prints the result to the console
    public static void computeResult(ArrayList<Integer> X, int i) {
        
        ArrayList<Character> Y = new ArrayList<>();
        
        for(int j = i; j >= 0; j--) {
            // Get the value of the new digit
            int temp = X.get(j);
            // Convert it back using the map
            char digit = letters.get(temp);
            
            // NOTE: When adding the chars to the new array we also revert the order
            // We will have the correct orientation of digits after this loop
            Y.add(digit);
        }
        
        // !! TO-DO: Multiply and Karatsuba
        // Print the correct sign (now works for add/subtract)
        if(SIGN != ' ' && SIGN != '+') {
            System.out.print(SIGN);
            // Revert the sign back to its original state
            SIGN = ' ';
        }
        
        // Return the result i.e: print the array Y to the console
        // IMPORTANT: The array Y has the digits in the correct order
        for(int j = 0; j < Y.size(); j++) {
            System.out.print(Y.get(j));
        }
        System.out.println();
    }
}

/*
*/

/* 
// Table that shows which is the final sign of the computation \\
// | op  | x | y | op' | sign |
// | add | + | + | add |   +  |
// | add | + | - | sub |  +/- |     x > y --> (sign = -) OR x <= y --> (sign = +)
// | add | - | + | sub |  +/- |     x >= y --> (sign = +) OR x < y --> (sign = -)
// | add | - | - | add |   -  |
// | sub | + | + | sub |  +/- |     x >= y --> (sign = +) OR x < y --> (sign = -)
// | sub | + | - | add |   +  |
// | sub | - | + | add |   -  |
// | sub | - | - | sub |  +/- |     x > y --> (sign = -) OR x <= y --> (sign = +)
*/
