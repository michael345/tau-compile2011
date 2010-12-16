package IC;

import java.io.FileReader;
import java.util.LinkedList;

import java_cup.runtime.Symbol;
import IC.AST.Field;
import IC.AST.ICClass;
import IC.AST.Method;
import IC.AST.PrettyPrinter;
import IC.AST.Program;
import IC.Parser.Lexer;
import IC.Parser.LibraryParser;
import IC.Parser.Parser;
import IC.Parser.SyntaxError;
import IC.SemanticAnalyser.ArrayType;
import IC.SemanticAnalyser.ClassType;
import IC.SemanticAnalyser.IntType;
import IC.SemanticAnalyser.MethodType;
import IC.SemanticAnalyser.Type;
import IC.SemanticAnalyser.TypeTable;
import IC.SemanticAnalyser.VoidType;

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
        
        public static void testToString() {
            Type classType = new ClassType(new ICClass(5,"A",new LinkedList<Field>(),new LinkedList<Method>()));
            Type intType = new IntType();
            Type arrayType1 = new ArrayType(classType);
            Type arrayType2 = new ArrayType(arrayType1);
            Type voidType = new VoidType();
            Type [] paramTypes = new Type[2];
            paramTypes[0] = intType;
            paramTypes[1] = arrayType2;
            Type methodType1 = new MethodType(paramTypes,voidType);
            System.out.println(classType);
            System.out.println(intType);
            System.out.println(arrayType2.toString());
            System.out.println(arrayType1.toString());
            System.out.println(methodType1);
        }
        
        public void testTypeTable() { 
            TypeTable table = new TypeTable();
            table.printTable();
            
            
        } 
        
        
        
        public static void main(String[] args) {
            testToString();
//            if (args.length == 0 || args.length > 3) { 
//                System.out.println("Input error - expected: java IC.Compiler <file.ic> [ -L</path/to/libic.sig> ] [ -print-ast ]");
//            }
//            
//            FileReader icTextFile = null;
//            FileReader libSigTextFile = null;
//            boolean isPrint = isPrint(args);
//            boolean parseLibrary = shouldParseLibrary(args);
//            String signaturePath = "";
//            if (parseLibrary) {
//                signaturePath = getSignature(args);
//            }
//            
//            try {           
//                icTextFile = new FileReader(args[0]);
//                Lexer icLexer = new Lexer(icTextFile);
//                Parser icParser = new Parser(icLexer);
//                icParser.printTokens = false;
//                Symbol parseSymbol = icParser.parse();
//                if (parseLibrary) { 
//                    libSigTextFile = new FileReader(signaturePath);
//                    Lexer libSigLexer = new Lexer(libSigTextFile);
//                    LibraryParser libSigParser = new LibraryParser(libSigLexer);
//                    libSigParser.printTokens = false;
//                    Symbol libParseSymbol = libSigParser.parse(); 
//                    if (!parseSuccessful(signaturePath, libParseSymbol)) {
//                    	return;
//                    }
//                    if (isPrint) {  
//                        Program lib = (Program) libParseSymbol.value;
//                        PrettyPrinter printer = new PrettyPrinter(signaturePath);
//                        String traverse = (String)printer.visit(lib);
//                        System.out.println(traverse);
//                    }
//                }
//                if (!parseSuccessful(args[0], parseSymbol)) {
//                	return;
//                }
//                
//                
//              
//                if (isPrint) {
//                    Program prog = (Program) parseSymbol.value;
//                    PrettyPrinter printer = new PrettyPrinter(args[0]);
//                    String traverse = (String)printer.visit(prog);
//                    System.out.println(traverse);
//                }
//                
//                icTextFile.close();
//            }catch (SyntaxError se) { 
//                System.exit(-1);
//            }  
//            
//            
//            catch (Exception e) {
//               System.exit(-1);
//            }
        }

		private static boolean parseSuccessful(String path, Symbol parseSymbol) {
			if (parseSymbol == null) {
				System.out.println("Error parsing " + path + ".");
				return false;
			}
			else {
				System.out.println("Parsed " + path + " successfully.");
				return true;
			}
		}

    }