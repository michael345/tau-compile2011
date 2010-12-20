package IC.SymbolTables;

public class MethodSymbolTable extends SymbolTable {

    public MethodSymbolTable(String id) {
        super(id);
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
}
