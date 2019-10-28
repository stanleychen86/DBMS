import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {
	public static void main(String[] args) {

        Listener listener = new Listener(); // The listener connects the parser and DB Engine

        System.out.println("Relational Database!");
        Scanner sc = new Scanner(System.in);
        while(true){
			
			System.out.print(">");
			String input = sc.nextLine();
			if(input.isEmpty()){
				continue;
			}
			
            /*For each input line,create a new instance of ANTLRInputStream 
            and pass it to the Lexer. The lexer gets a stream of tokens and
            passes them to the parser. We then enter the parser and walk through
            it.*/
			GrammarLexer lexer = new GrammarLexer(new ANTLRInputStream(input));
		    CommonTokenStream tokens = new CommonTokenStream(lexer);
		    GrammarParser parser = new GrammarParser(tokens);
		    GrammarParser.ProgramContext programContext = parser.program();
		    ParseTreeWalker walker = new ParseTreeWalker();
		    walker.walk(listener, programContext);
		    
			
		}
    }
}