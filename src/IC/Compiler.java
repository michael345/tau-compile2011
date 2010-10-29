package IC;

import java.io.*;

import IC.Parser.Lexer;
import IC.Parser.sym;

import java_cup.runtime.Symbol;

public class Compiler {
    public static void main(String[] args) {
        Symbol currToken;
        try { 
            FileReader txtFile = new FileReader(args[0]);
            Lexer lexer = new Lexer(txtFile);
            do {
                currToken = lexer.next_token();
                // do something with currToken
            } while (currToken.sym != sym.EOF);
        } catch (Exception e) {
            throw new RuntimeException("IO Error (brutal exit)" + e.toString());
        }

    }
}
