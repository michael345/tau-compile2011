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
        
        public static boolean shouldParseLibrary(String[] args) {
            for (int i = 0; i < args.length; i++) { 
                if (args[i].startsWith("-L")) { 
                    return true;
                }
            }
            return false;
        }
        
        public static String getSignature(String[] args) { 
            for (int i = 0; i < args.length; i++) { 
                if (args[i].startsWith("-L")) { 
                    return args[i].substring(2, args[i].length());
                }
            }
            return ""; // program will never reach here
        }
        
        public static void main(String[] args) {
            if (args.length == 0 || args.length > 3) { 
                System.out.println("Input error - expected: java IC.Compiler <file.ic> [ -L</path/to/libic.sig> ] [ -print-ast ]");
            }
            FileReader icTextFile = null;
            FileReader libSigTextFile = null;
            boolean isPrint = isPrint(args);
            boolean parseLibrary = shouldParseLibrary(args);
            String signaturePath = "";
            if (parseLibrary) {
                signaturePath = getSignature(args);
            }
            
            try {           
                icTextFile = new FileReader(args[0]);
                Lexer icLexer = new Lexer(icTextFile);
                Parser icParser = new Parser(icLexer);
                icParser.printTokens = false;
                Symbol parseSymbol = icParser.parse();
                
                if (parseLibrary) { 
                    libSigTextFile = new FileReader(signaturePath);
                    Lexer libSigLexer = new Lexer(libSigTextFile);
                    LibraryParser libSigParser = new LibraryParser(libSigLexer);
                    libSigParser.printTokens = false;
                    Symbol libParseSymbol = libSigParser.parse(); 
                    if (isPrint) {  
                        Program lib = (Program) libParseSymbol.value;
                        PrettyPrinter printer = new PrettyPrinter(signaturePath);
                        String traverse = (String)printer.visit(lib);
                        System.out.println(traverse);
                    }
                }
              
                if (isPrint) {
                    Program prog = (Program) parseSymbol.value;
                    PrettyPrinter printer = new PrettyPrinter(args[0]);
                    String traverse = (String)printer.visit(prog);
                    System.out.println(traverse);
                }
                
                icTextFile.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }