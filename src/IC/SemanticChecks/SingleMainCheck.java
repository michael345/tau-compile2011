package IC.SemanticChecks;

import IC.AST.ICClass;
import IC.AST.Method;
import IC.SymbolTables.SemanticSymbol;
import IC.SymbolTables.SymbolTable;
import IC.TYPE.Type;
import IC.TYPE.TypeTable;

public class SingleMainCheck {
    public static int times = 0;
    
    
    public static boolean check(SymbolTable table) {
        for (SymbolTable child : table.getChildren()) { 
            times += handleClass(child);
        }
        if (times != 1) { 
            return false;
        }
        return true;
        
    }

    private static int handleClass(SymbolTable classTable) {
           SemanticSymbol sym = classTable.lookup("main");
           if (sym == null) { 
               return 0;
           }
           if (sym.toString().compareTo("Static method : main {string[] -> void}") == 0) {
               return 1;
           }
           return 0;
        
    }

    
 }
    

