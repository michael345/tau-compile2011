package IC;

import java.io.*;
import IC.Parser.Lexer;
import IC.Parser.Token;
import IC.Parser.sym;

public class Compiler {
    public static void main(String[] args) {
        Token currToken;
        try { 
            FileReader txtFile = new FileReader(args[0]);
            Lexer lexer = new Lexer(txtFile);
            do {
                currToken = lexer.next_token();
                System.out.println(currToken);
                // do something with currToken
            } while (currToken.getId() != sym.EOF);
        } catch (Exception e) {
            throw new RuntimeException("IO Error (brutal exit)" + e.toString());
        }

    }
}
