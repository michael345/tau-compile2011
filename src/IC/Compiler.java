package IC;

import java.io.*;
import IC.Parser.Lexer;
import IC.Parser.LexicalError;
import IC.Parser.Token;
import IC.Parser.sym;

public class Compiler {
    public static void main(String[] args) {
        Token currToken;
        if (args.length != 1) { 
            System.out.println("Requires exactly one argument: <filename>");
            return;
        }
        try { 
            FileReader txtFile = new FileReader(args[0]);
            Lexer lexer = new Lexer(txtFile);
            do {
                currToken = lexer.next_token();
                System.out.println(currToken);
                // do something with currToken
            } while (currToken.getId() != sym.EOF);
        }
        catch (LexicalError e) {
            e.printMessage();
            return;
        }
        catch (Exception e) {
            throw new RuntimeException("IO Error (brutal exit)" + e.toString());
        }
    }
}
