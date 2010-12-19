package IC.SemanticAnalyser;

public class ClassSymbolTable extends SymbolTable {

    public ClassSymbolTable(String id) {
        super(id);
    }
    
    public void printSymbolTable() { 
        System.out.println("Class Symbol Table: " + getId());
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
