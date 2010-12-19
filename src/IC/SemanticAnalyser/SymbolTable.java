package IC.SemanticAnalyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private Map<String,SemanticSymbol> entries;
    private String id;
    private SymbolTable parentSymbolTable;
    private List<SymbolTable> children;
   
    public SymbolTable(String id) {
        this.id = id;
        entries = new HashMap<String,SemanticSymbol>();
        children = new ArrayList<SymbolTable>();
      }
    
    public SemanticSymbol localLookup(String key) { 
        if (entries.containsKey(key)) { 
            return entries.get(key);
        }
        return null;
    }
    
    public SymbolTable symbolTableLookup(String id) { // returns child Symbol Table with id = id
        for (SymbolTable child : children) { 
            if (0 == id.compareTo(child.getId())) { 
                return child;
            }
        }
        return null;
    }
    
    
    public SemanticSymbol lookup(String key) { // without forward referencing
        SymbolTable temp;
        for (temp = this; temp != null; temp = temp.getParentSymbolTable()) {
            if (temp.localLookup(key) != null) {
                return temp.localLookup(key);
            }
        }
        return null; //TODO: should be lookup error or something
    }
   
    
    public boolean insert(String key, SemanticSymbol value) { 
        if (localLookup(key) == null) { 
            entries.put(key, value);
            return true;
        }
        else 
            return false; // TODO: maybe throw some kind of error - illegal re-definition
    }
    
    public List<SymbolTable> getChildren() {
        return children;
    }

    public void setChildren(List<SymbolTable> children) {
        this.children = children;
    }

    
    
    public void addChild(SymbolTable child) { 
        if (child!=null){
	    	if (children.contains(child))  {
	            ;// TODO: maybe a problem? 
	        }
	        else {
	            child.setParentSymbolTable(this);
	            children.add(child);
	        }
        }
    }
    
    public SymbolTable removeChild(String child) {  //removes and returns (pop child)
        for (SymbolTable member : children) { 
            if (0 == child.compareTo(member.getId())) { 
                children.remove(member);
                return member; 
            }
        }
        return null;
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
    
    
    public void printSymbolTable() { 
        
    }
    
    
    public void printChildren () { 
        int i;
        if (getChildren().isEmpty()) { 
            
        }
        else {
            System.out.print("Children tables: ");
            for (i = 0; i < getChildren().size()-1; i++) { 
                System.out.print(getChildren().get(i).getId() + ", ");
            }
            System.out.println(getChildren().get(i).getId());
        } 
        System.out.println();
    }
    
    
    
}
