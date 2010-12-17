package IC.SemanticAnalyser;

public class Kind {
    int kind;
    
    public static final int VAR = 1;
    public static final int FIELD = 2;
    public static final int METHOD = 3;
    public static final int CLASS = 4;
    
    public Kind(int kind) {
        this.kind = kind;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
    
    

    
    
    
}
