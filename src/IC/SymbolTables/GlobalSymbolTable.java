package IC.SymbolTables;

public class GlobalSymbolTable extends SymbolTable {
    public GlobalSymbolTable(String id) {
        super(id);
    }
    
    public void printSymbolTable() { 
        System.out.println("Global Symbol Table: " + getId());
        for (SemanticSymbol sym : getEntries().values()) { 
            System.out.println("\t" + sym.toString());
        }
        printChildren();
        
        for (SymbolTable child : getChildren()) { 
            child.printSymbolTable();
        }
        System.out.println();
    }

}
