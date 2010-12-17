package IC.SemanticAnalyser;

public class SemanticSymbol {
    private Type type;
    private Kind kind;
    private String id;
    private boolean isForwardRef;
    private boolean isStatic;
    
    public SemanticSymbol(Type type, Kind kind, String id,
            boolean isForwardRef, boolean isStatic) {
        this.type = type;
        this.kind = kind;
        this.id = id;
        this.isForwardRef = isForwardRef;
        this.isStatic = isStatic;
    }

    public Type getType() {
        return type;
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
        return (kind.getKind() == Kind.METHOD);
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

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
    
    
    
}
