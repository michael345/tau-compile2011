package IC.SymbolTables;

import IC.TYPE.ClassType;
import IC.TYPE.Kind;
import IC.TYPE.Type;

public class SemanticSymbol {
    private Type type;
    private Kind kind;
    private String id;
    private boolean isForwardRef;
    private int uniqueID;
    private static int counter = 0; // for PA4
    
    public SemanticSymbol(Type type, Kind kind, String id,
            boolean isForwardRef) {
        this.type = type;
        this.kind = kind;
        this.id = id;
        this.isForwardRef = isForwardRef;
        uniqueID = counter++;
    }

    public Type getType() {
        return type;
    }

    public int getUniqueID() { 
        return uniqueID;
    }
    
    public void setType(Type type) {
        this.type = type;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public boolean isMethod() { 
        return (kind.getKind() == Kind.VIRTUALMETHOD || kind.getKind() == Kind.STATICMETHOD);
    }
    public void setId(String id) {
        this.id = id;
    }

    public boolean isForwardRef() {
        return isForwardRef;
    }

    public void setForwardRef(boolean isForwardRef) {
        this.isForwardRef = isForwardRef;
    }
    
    public String toString() { 
        
        if (type instanceof ClassType) { 
            if (id.compareTo("this") == 0) { // dont want to print "Field: this $ClassType"
                return "";
            }
            return "" + kind + ": " + id; 
        }
        else if (type == null) { 
            return "" + kind + ": " + id; 

        }
        else if (isMethod()) { 
            return "" + kind + " : " + id  + " " + type;
        }
        else {
            return "" + kind + ": " + type + " " + id; 
        }
        
    }
    
    
}
