package IC.SymbolTables;

public class BlockSymbolTable extends SymbolTable {

    public BlockSymbolTable(String id) {
        super(id);
    }
    
    public void printSymbolTable() { 
        System.out.println("Statement Block Symbol Table " + getId() + " ( located in " + this.getParentSymbolTable().getId() + " )");
        for (SemanticSymbol sym : getEntries().values()) { 
            System.out.println("\t" + sym.toString());
        }
        printChildren();
        
        for (SymbolTable child : getChildren()) { 
            child.printSymbolTable();
        }
        System.out.println();
    }
    
    public String toString() {return getId();}
}


