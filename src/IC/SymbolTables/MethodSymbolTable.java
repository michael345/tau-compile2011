package IC.SymbolTables;

import java.util.LinkedList;
import java.util.List;

public class MethodSymbolTable extends SymbolTable {
    
    List<SemanticSymbol> argsList;
    public MethodSymbolTable(String id) {
        super(id);
        argsList = new LinkedList<SemanticSymbol>();
    }

    public void printSymbolTable() { 
        System.out.println("Method Symbol Table: " + getId());
        for (SemanticSymbol sym : getEntries().values()) { 
            System.out.println("\t" + sym.toString());
        }
        printChildren();
        
        for (SymbolTable child : getChildren()) { 
            child.printSymbolTable();
        }
        System.out.println();
    }
    
    public boolean insert(String key, SemanticSymbol value) { 
        if (!super.insert(key, value)) return false;
        argsList.add(value);
        return true;
    }

    public List<SemanticSymbol> getArgsList() {
        return argsList;
    }

    public void setArgsList(List<SemanticSymbol> argsList) {
        this.argsList = argsList;
    }
    
    
    
}
