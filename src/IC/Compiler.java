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
import IC.SemanticAnalyser.TypeTableConstructor;
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
            Type classType = new ClassType(new ICClass(5,"A",new LinkedList<Field>(),new LinkedList<Method>()),1);
            Type intType = new IntType(2);
            Type arrayType1 = new ArrayType(classType,3);
            Type arrayType2 = new ArrayType(arrayType1,4);
            Type voidType = new VoidType(5);
            Type [] paramTypes = new Type[2];
            paramTypes[0] = intType;
            paramTypes[1] = arrayType2;
            Type methodType1 = new MethodType(paramTypes,voidType,6);
            System.out.println(classType);
            System.out.println(intType);
            System.out.println(arrayType2.toString());
            System.out.println(arrayType1.toString());
            System.out.println(methodType1);
        }
        
        public static void testTypeTable() { 
            Type classType = new ClassType(new ICClass(5,"A",new LinkedList<Field>(),new LinkedList<Method>()),1);
            Type intType = new IntType(2);
            Type arrayType1 = new ArrayType(classType,3);
            Type arrayType2 = new ArrayType(arrayType1,4);
            Type voidType = new VoidType(5);
            Type [] paramTypes = new Type[2];
            paramTypes[0] = intType;
            paramTypes[1] = arrayType2;
            Type methodType1 = new MethodType(paramTypes,voidType,6);
            System.out.println(classType);
            System.out.println(intType);
            System.out.println(arrayType2.toString());
            System.out.println(arrayType1.toString());
            System.out.println(methodType1);
            
            TypeTable table = new TypeTable();
            table.primitiveType(intType);
            table.classType(new ICClass(5,"A",new LinkedList<Field>(),new LinkedList<Method>()));
            ICClass b = new ICClass(6, "B", "A", new LinkedList<Field>(),new LinkedList<Method>());
            table.classType(b);
            table.printTable();
            
        } 
        
        
        
        public static void main(String[] args) {
            //testTypeTable();
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
                    if (!parseSuccessful(signaturePath, libParseSymbol)) {
                    	return;
                    }
                    Program lib = (Program) libParseSymbol.value;
                    TypeTableConstructor ttc = new TypeTableConstructor(signaturePath);
                    TypeTable tt = (TypeTable)ttc.visit(lib);
                    tt.printTable();
                }
                if (!parseSuccessful(args[0], parseSymbol)) {
                	return;
                }
                Program lib = (Program) parseSymbol.value;
                TypeTableConstructor ttc = new TypeTableConstructor(signaturePath);
                TypeTable tt = (TypeTable)ttc.visit(lib);
                tt.printTable();
                System.out.println(tt.isLegalHeirarchy());
                
                
                icTextFile.close();
            }catch (SyntaxError se) { 
                System.exit(-1);
            }  
            
            
            catch (Exception e) {
               System.exit(-1);
            }
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