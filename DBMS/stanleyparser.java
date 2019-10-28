import java.util.Scanner;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class stanleyparser {
    public static void main(String[] args) throws IOException {
        BufferedWriter fout = new BufferedWriter(new FileWriter("output.txt"));
        try {
            if(0 < args.length) {
                File f = new File(args[0]);
                int i = 0;
                Scanner sc = new Scanner(f);
                while(sc.hasNextLine()) {
                    String st = sc.nextLine();
                    if (!st.equals("")) {
                        CharStream input = CharStreams.fromString(st);
                        GrammarLexer lex = new GrammarLexer(input);
                        CommonTokenStream tokens = new CommonTokenStream(lex);
                        GrammarParser parser = new GrammarParser(tokens);
                        ParseTree t = parser.program();
                        // Moved this up here because it happens in both the if and else blocks
                        i++;
                        String s = String.valueOf(i);
                        if(parser.getNumberOfSyntaxErrors() == 0) {
                            System.out.println("Line "+ s + " is valid.");
                            fout.write("Line "+ s + " is valid.\n");
                        }
                        else {
                            System.out.println("Line " + s + " is invalid.");
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
            fout.close();
        }
    }
}