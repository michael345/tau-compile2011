package IC.SemanticChecks;

import IC.AST.ICClass;
import IC.AST.Method;
import IC.SymbolTables.ClassSymbolTable;
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
    	   int sum = 0;
           SemanticSymbol sym = classTable.localLookup("main");
           if (sym != null) { 
               if (sym.toString().compareTo("Static method : main {string[] -> void}") == 0) {
               sum++;
               }
           }
           for (SymbolTable child : classTable.getChildren()) {
        	   if(child instanceof ClassSymbolTable){
        		   sum += handleClass(child);
        	   }
           }
           return sum;
        
    }

    
 }
    

