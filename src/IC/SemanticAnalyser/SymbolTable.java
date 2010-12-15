package IC.SemanticAnalyser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private Map<String,SemanticSymbol> entries;
    private String id;
    private SymbolTable parentSymbolTable;
    private List<SymbolTable> children;
   
    public List<SymbolTable> getChildren() {
        return children;
    }

    public void setChildren(List<SymbolTable> children) {
        this.children = children;
    }

    public SymbolTable(String id) {
      this.id = id;
      entries = new HashMap<String,SemanticSymbol>();
    }
    
    public boolean addEntry(String key, SemanticSymbol value) { 
        entries.put(key, value);
        return true;
    }
    
    public void addChild(SymbolTable child) { 
        if (children.contains(child))  {
            ;// maybe a problem? 
        }
        else 
            children.add(child);
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
