import java.util.Scanner;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        // Create a BufferedWriter for writing output to a file.
        BufferedWriter fout = new BufferedWriter(new FileWriter("output.txt"));
        try {
            if(0 < args.length) { // Input file taken as a command-line argument!
                File f = new File(args[0]);
                int i = 0;
                Scanner sc = new Scanner(f);
                while(sc.hasNextLine()) {
                    String st = sc.nextLine(); // Read each line from the input file
                    if (!st.equals("")) {
                        // Make a character stream for each line
                        CharStream input = CharStreams.fromString(st);
                        // Declare a lexer to get the tokens
                        GrammarLexer lex = new GrammarLexer(input);
                        // Create a token stream
                        CommonTokenStream tokens = new CommonTokenStream(lex);
                        // Make a parser from the token stream
                        GrammarParser parser = new GrammarParser(tokens);
                        // Begin parsing with the program rule first-this creates a parse tree.
                        ParseTree t = parser.program();
                        i++;
                        String s = String.valueOf(i);
                        // parser.getNumberOfSyntaxErrors = 0 means the input follows the grammar
                        if(parser.getNumberOfSyntaxErrors() == 0) {
                            fout.write("Line "+ s + " is valid.\n");
                        }
                        else {
                            fout.write("Line " + s + " is invalid.\n");
                        }
                    }
                }
            }
            else {
                System.out.println("No argument.");
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        finally {
            // Close BufferedWriter
            fout.close();
        }
    }
}