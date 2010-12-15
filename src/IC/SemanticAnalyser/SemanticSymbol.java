package IC.SemanticAnalyser;

public class SemanticSymbol {
    private SemanticType type;
    private SemanticKind kind;
    private String id;
    private boolean isForwardRef;
    private boolean isStatic;
    
    public SemanticSymbol(SemanticType type, SemanticKind kind, String id,
            boolean isForwardRef, boolean isStatic) {
        this.type = type;
        this.kind = kind;
        this.id = id;
        this.isForwardRef = isForwardRef;
        this.isStatic = isStatic;
    }

    public SemanticType getType() {
        return type;
    }

    public void setType(SemanticType type) {
        this.type = type;
    }

    public SemanticKind getKind() {
        return kind;
    }

    public void setKind(SemanticKind kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public boolean isMethod() { 
        return (kind.getKind() == SemanticKind.METHOD);
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
