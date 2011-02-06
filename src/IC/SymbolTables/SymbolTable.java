package IC.SymbolTables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.AST.ASTNode;
import IC.TYPE.Kind;

public class SymbolTable {
    private Map<String,SemanticSymbol> entries;
    private String id;
    private SymbolTable parentSymbolTable;
    private List<SymbolTable> children;
    private boolean isLoop;
   
    public SymbolTable(String id) {
        this.id = id;
        entries = new HashMap<String,SemanticSymbol>();
        children = new ArrayList<SymbolTable>();
        isLoop = false;
      }
    
    public boolean isStatic() { 
        SymbolTable temp = this;
        
        while (!(temp instanceof MethodSymbolTable)) { 
            temp = temp.getParentSymbolTable();
        }
        String funcName = temp.getId();
        temp = temp.getParentSymbolTable();
        if (temp.staticLookup(funcName) == null) { 
            return false;
        }
        if (temp.staticLookup(funcName).getKind().getKind() == Kind.STATICMETHOD) 
            return true;
        else 
            return false;
    }
    
    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public SemanticSymbol localLookup(String key) { 
        if (entries.containsKey(key)) { 
            SemanticSymbol result = entries.get(key);
            if (result.getKind().getKind() != Kind.STATICMETHOD) { 
                return result;
            }
        }
        return null;
    }
    
    public SemanticSymbol staticLocalLookup(String key) { 
        if (entries.containsKey(key)) {
            SemanticSymbol result = entries.get(key);
            int tempKind = result.getKind().getKind();
            if (tempKind == Kind.STATICMETHOD || tempKind == Kind.VAR || tempKind == Kind.FORMAL) {
                return result;
            }
        }
        return null;
    }
    
    private SymbolTable getClassSymbolTable(String str, ASTNode startNode) {
        SymbolTable temp = startNode.getEnclosingScope();
        while (temp.getParentSymbolTable() != null) { 
            temp = temp.getParentSymbolTable();
        }
        return temp.symbolTableLookup(str);
    }
    
    public SemanticSymbol staticLookup(String startingClass, String staticFuncName, ASTNode node ) { 
        SymbolTable start = getClassSymbolTable(startingClass, node);
        if (start == null ) { 
            return null;// class not found 
        }
        
        for (; start != null; start = start.getParentSymbolTable()) {
            if (start.staticLocalLookup(staticFuncName) != null) {
                return start.staticLocalLookup(staticFuncName);
            }
        }
        return null;
        
    }
   
    
    public SemanticSymbol staticLookup(String key) { // without forward referencing
        SymbolTable temp;
        for (temp = this; temp != null; temp = temp.getParentSymbolTable()) {
            if (temp.staticLocalLookup(key) != null) {
                return temp.staticLocalLookup(key);
            }
        }
        return null; 
    }
 

    public SymbolTable recursiveGetClass(String className, SymbolTable startPos) { 
        if (startPos.getId().compareTo(className) == 0) { 
            return startPos;
        }
        SymbolTable temp = null;
        for (SymbolTable child : startPos.getChildren()) { 
            if ((temp = recursiveGetClass(className, child)) != null) { 
                return temp;
            }
        }
        return null;
        
    }
    
    public SymbolTable getMethod(String className, String methodName) { 
        SymbolTable classSt = recursiveGetClass(className, getGlobal());
        while (classSt instanceof ClassSymbolTable) { 
            for (SymbolTable child : classSt.getChildren()) { 
                if (child.getId().compareTo(methodName) == 0 ) {
                    return child;
                }
            }
            classSt = classSt.getParentSymbolTable();
        }
        return null;
    }
    
    public SymbolTable symbolTableLookup(String symTableID) { // returns child Symbol Table with id symTableID
        for (SymbolTable child : children) { 
            if (0 == symTableID.compareTo(child.getId())) { 
                return child;
            }
        }
        // if we're here we looked for a symbol table that is not direct child of global.
        // this part makes it possible to find deep son. e.g. if A < B < C want to find B
        SymbolTable temp;
        for (SymbolTable child : children) { 
            if ((temp = child.symbolTableLookup(symTableID)) != null) { 
                return temp;
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
        return null; 
    }
    
    public SymbolTable lookupSymbolTableContaining(String key) { // without forward referencing
        SymbolTable temp;
        for (temp = this; temp != null; temp = temp.getParentSymbolTable()) {
            if (temp.localLookup(key) != null) {
                return temp;
            }
        }
        return null; 
    }
   
    
    public SymbolTable getGlobal() { 
        SymbolTable temp = this;
        while (temp.parentSymbolTable != null) {
            temp = temp.parentSymbolTable;
        }
        return temp;
        
    }
    
    public boolean insert(String key, SemanticSymbol value) { 
        if (localLookup(key) == null) { 
            entries.put(key, value);
            return true;
        }
        else 
            return false;
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
	            ;
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
    
    public SymbolTable getEnclosingClassSymbolTable() { 
        SymbolTable temp = this;
        while (!(temp instanceof ClassSymbolTable)) { 
            temp = temp.getParentSymbolTable();
        }
        return temp;
    }
    
    
}
