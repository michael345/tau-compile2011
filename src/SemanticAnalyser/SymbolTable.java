package SemanticAnalyser;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String,SemanticSymbol> entries;
    private String id;
    private SymbolTable parentSymbolTable;
   
    public SymbolTable(String id) {
      this.id = id;
      entries = new HashMap<String,SemanticSymbol>();
    }
    
    public boolean addEntry(String key, SemanticSymbol value) { 
        entries.put(key, value);
        return true;
    }
    
    public Map<String, SemanticSymbol> getEntries() {
        return entries;
    }
    public void setEntries(Map<String, SemanticSymbol> entries) {
        this.entries = entries;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public SymbolTable getParentSymbolTable() {
        return parentSymbolTable;
    }
    public void setParentSymbolTable(SymbolTable parentSymbolTable) {
        this.parentSymbolTable = parentSymbolTable;
    }
    
    
}
