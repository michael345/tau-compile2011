package IC.SemanticChecks;

import java.util.LinkedList;
import java.util.List;

import IC.AST.ICClass;
import IC.AST.If;
import IC.AST.LibraryMethod;
import IC.AST.Method;
import IC.AST.Program;
import IC.AST.Return;
import IC.AST.Statement;
import IC.AST.StatementsBlock;
import IC.TYPE.MethodType;
import IC.TYPE.TypeTable;

public class ReturnAllPathsCheck {

    static String badFunc;
    
    public static boolean doAllPathsHaveReturn(Program prog) {
        
        for (ICClass icClass : prog.getClasses()) { 
            for (Method meth : icClass.getMethods()) { 
                if (meth instanceof LibraryMethod) { 
                    continue;
                }
                IC.TYPE.MethodType mt = (MethodType) meth.getEnclosingScope().getParentSymbolTable().lookup(meth.getName()).getType();
                IC.TYPE.Type rt = mt.getReturnType();
                if (rt.equals(TypeTable.voidType)) { 
                    continue;
                }

                if (!checkAllPathsReturn(meth.getStatements())) { 
                    System.out.println("semantic error at line " + meth.getLine() + ": " + meth.getName() + " does not return on all control paths.");
                    System.exit(-1);
                    return false;
                }
                
                
            }
            
        }
        return true;
    }

    public static boolean checkAllPathsReturn(List<Statement> statements) {
        for (Statement st : statements) { 
            if (st instanceof Return) { 
                return true;
            }
        }
        for (Statement st : statements) { 
            if (st instanceof StatementsBlock) { 
                if (checkAllPathsReturn(((StatementsBlock) st).getStatements())) { 
                    return true;
                }
            }
        }
        
        // looking for if-else with both return
        for (Statement st : statements) { 
            if (st instanceof If && ((If) st).hasElse()) { 
               
                 LinkedList<Statement> ifList = new LinkedList<Statement>();
                 LinkedList<Statement> elseList = new LinkedList<Statement>();
                 
                 ifList.add(((If) st).getOperation());
                 elseList.add(((If) st).getElseOperation());
                 if (checkAllPathsReturn(ifList) && checkAllPathsReturn(elseList)) { 
                     return true;
                 } 

                    
            }
            
        }
        
        return false;
    }
}
