package IC;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;

import java_cup.runtime.Symbol;
import IC.AST.ASTNode;
import IC.AST.Field;
import IC.AST.ICClass;
import IC.AST.Method;
import IC.AST.PrettyPrinter;
import IC.AST.Program;
import IC.Parser.Lexer;
import IC.Parser.LexicalError;
import IC.Parser.LibraryParser;
import IC.Parser.Parser;
import IC.Parser.SyntaxError;
import IC.SemanticChecks.BreakContinueChecker;
import IC.SemanticChecks.ReturnAllPathsCheck;
import IC.SemanticChecks.ScopeChecker;
import IC.SemanticChecks.SingleMainCheck;
import IC.SemanticChecks.ThisChecker;
import IC.SemanticChecks.TypeChecker;
import IC.SymbolTables.SymbolTable;
import IC.SymbolTables.SymbolTableConstructor;
import IC.TYPE.ArrayType;
import IC.TYPE.ClassType;
import IC.TYPE.IntType;
import IC.TYPE.MethodType;
import IC.TYPE.Type;
import IC.TYPE.TypeTable;
import IC.TYPE.TypeTableConstructor;
import IC.TYPE.VoidType;
import IC.lir.LirProgram;
import IC.lir.LirTranslator;

public class Compiler {
        
        public static boolean isPrintAst(String[] args) {
            for (int i = 0; i < args.length; i++) { 

                if (args[i].compareTo("-print-ast") == 0) { 

                    return true;
                }
            }
            return false;
        }
        
        public static boolean isDumpSymTable(String[] args) { 
            for (int i = 0; i < args.length; i++) { 
                if (args[i].compareTo("-dump-symtab") == 0) { 

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
            
            //TypeTable table = new TypeTable();
            TypeTable.primitiveType(intType);
            TypeTable.classType(new ICClass(5,"A",new LinkedList<Field>(),new LinkedList<Method>()));
            ICClass b = new ICClass(6, "B", "A", new LinkedList<Field>(),new LinkedList<Method>());
            TypeTable.classType(b);
            TypeTable.printTable();
            
        } 
        
        
        
        
        public static void main(String[] args) {
            //testTypeTable();
            //input checking
            if (args.length == 0 || args.length > 4) { 
                System.out.println("Input error - expected: java IC.Compiler <file.ic> [options]");
            }
            
            FileReader icTextFile = null;
            FileReader libSigTextFile = null;
            boolean isPrint = isPrintAst(args);
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
                Program icProg = (Program) parseSymbol.value;
                Program libProg = null;
                if (!parseSuccessful(args[0], parseSymbol)) {
                    System.exit(-1);
                }
                
                if (parseLibrary) { 
                    libSigTextFile = new FileReader(signaturePath);
                    Lexer libSigLexer = new Lexer(libSigTextFile);
                    LibraryParser libSigParser = new LibraryParser(libSigLexer);
                    libSigParser.printTokens = false;
                    Symbol libParseSymbol = libSigParser.parse(); 
                    libProg = (Program) libParseSymbol.value; //the lib program
                    ICClass libraryClass = libProg.getClasses().get(0); //get the library class
                    libNameIsLibrary(libraryClass);
                    if (!parseSuccessful(signaturePath, libParseSymbol)) {
                        System.exit(-1);
                    }
                    
                   
                    icProg.addClass(libraryClass);
                    
                }
                
                //AST TREE IS GOOD includes library if needed
                TypeTableConstructor ttc = new TypeTableConstructor(args[0]);
                ttc.visit(icProg); //build type table
                if (!TypeTable.isLegalHeirarchy()) {
                     System.out.println("Semantic Error: Illegal class heirarchy.");
                     System.exit(-1);
                }
                SymbolTableConstructor stc = new SymbolTableConstructor(args[0]);
                SymbolTable st = (SymbolTable) stc.visit(icProg);
                
                semanticChecks(icProg);
                    
     
                if (isDumpSymTable(args)) {
                    st.printSymbolTable();
                    TypeTable.printTable();
                }
                
                if (isPrint) {  
                    PrettyPrinter printer = new PrettyPrinter(args[0]);
                    String traverse = (String)printer.visit(icProg);
                    System.out.println(traverse);
                        
                    
                }

                icTextFile.close();
                //Translate to LIR
                LirProgram lirProg = LirProgram.getInstance();
                LirTranslator lirTranslator = new LirTranslator();
                lirTranslator.visit(icProg);
                //System.out.println(lirProg.toString()); 
                
                if (shouldPrintLir(args)) { // Write lir translation to file.lir
                    FileWriter fw = new FileWriter(args[0].substring(0, args[0].length()-3) + ".lir");
                    fw.append(lirProg.toString());
                    fw.close();  
                }
                
                
            }catch (SyntaxError se) { 
                se.printErrorMsg();
                System.exit(-1);
            }
            catch (LexicalError se) { 
                se.printMessage();
                System.exit(-1);
            }  
            
            
            catch (Exception e) {
               e.printStackTrace();
               System.exit(-1);
            }
        }

        private static boolean shouldPrintLir(String[] args) {
            for (int i = 0; i < args.length; i++) { 

                if (args[i].compareTo("-print-lir") == 0) { 

                    return true;
                }
            }
            return false;
        }

        private static void libNameIsLibrary(ICClass libraryClass) {
			if (libraryClass.getName().compareTo("Library") != 0){
				System.out.println("semantic error - name of library class must be 'Library' ");
				System.exit(-1);
			}
			
		}

		private static void semanticChecks(Program icProg) {
		    if (!TypeTable.isLegalHeirarchy()) { 
		        System.out.println("semantic error: illegal class heirarchy.");
		        System.exit(-1);
		    }
		    scopeCheck(icProg);
		    bcCheck(icProg);
            thisCheck(icProg);
            if (!mainCheck(icProg)) { 
                System.out.println("semantic error - must contain exactly one static method main: {string[] -> void} ");
                System.exit(-1);
            }
            typeCheck(icProg);
            ReturnAllPathsCheck.doAllPathsHaveReturn(icProg);
              
            
           
            
        }

		private static void scopeCheck(Program icProg) {
            ScopeChecker tc = new ScopeChecker();
            Object temp = tc.visit(icProg);
            ASTNode atemp;
            if (temp != null) { 
                atemp = (ASTNode) temp;
                System.out.println("semantic error at line " + atemp.getLine() + ": scope rule error.");
                System.exit(-1);
            }

        }
		
		private static void typeCheck(Program icProg) {
		    TypeChecker tc = new TypeChecker();
            Object temp = tc.visit(icProg);
            ASTNode atemp;
            if (temp != null) { 
                atemp = (ASTNode) temp;
                System.out.println("semantic error at line " + atemp.getLine() + ": type mismatch.");
                System.exit(-1);
            }

		}
		    
		
        private static boolean mainCheck(Program icProg) { 
            return SingleMainCheck.check(icProg.getEnclosingScope());
        }
        private static void bcCheck(Program icProg) {
            BreakContinueChecker bcCheck = new BreakContinueChecker();
            Object temp = bcCheck.visit(icProg);
            ASTNode atemp;
            if (temp != null) { 
                atemp = (ASTNode) temp;
                System.out.println("semantic error at line " + atemp.getLine() + ": break/continue out of loop scope.");
                System.exit(-1);
            }
        }
        private static void thisCheck(Program icProg) {
            ThisChecker tCheck = new ThisChecker();
            Object temp = tCheck.visit(icProg);
            ASTNode atemp;
            if (temp != null) { 
                atemp = (ASTNode) temp;
                System.out.println("semantic error at line " + atemp.getLine() + ": 'this' cannot appear in a static scope.");
                System.exit(-1);
            }
        }
		private static boolean parseSuccessful(String path, Symbol parseSymbol) {
			if (parseSymbol == null) {
				System.out.println("Error parsing " + path + ".");
				return false;
			}
			else {
				System.out.println("Parsed " + path + " successfully!");
				return true;
			}
		}

    }