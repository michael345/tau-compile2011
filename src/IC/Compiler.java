package IC;

import java.io.*;

import java_cup.runtime.Symbol;
import IC.AST.PrettyPrinter;
import IC.AST.Program;
import IC.Parser.*;

public class Compiler {
        
        public static boolean isPrint(String[] args) {
            for (int i = 0; i < args.length; i++) { 
                if (args[i].compareTo("-print-ast") == 0) { 
                    return true;
                }
            }
            return false;
        }
        
        public static void main(String[] args) {
            if (args.length == 0 || args.length > 3) { 
                System.out.println("Input error - expected: java IC.Compiler <file.ic> [ -L</path/to/libic.sig> ] [ -print-ast ]");
            }
            FileReader textFile = null;
            boolean isPrint = isPrint(args);
            
            try {           
                textFile = new FileReader(args[0]);
                Lexer lexer = new Lexer(textFile);
                //LibraryParser parser = new LibraryParser(lexer);
                Parser parser = new Parser(lexer);
                
                parser.printTokens = false;
                
                Symbol parseSymbol = parser.parse();
                
                if (isPrint) {
                    Program prog = (Program) parseSymbol.value;
                    PrettyPrinter printer = new PrettyPrinter(args[0]);
                    String traverse = (String)printer.visit(prog);
                    System.out.println(traverse);
                }
                
                textFile.close();
                
            } catch (Exception e) {
                e.toString();
            }
        }

    }