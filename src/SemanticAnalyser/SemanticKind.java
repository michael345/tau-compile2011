package SemanticAnalyser;

public class SemanticKind {
    int kind;
    
    public static final int VAR = 1;
    public static final int FIELD = 2;
    public static final int METHOD = 3;
    
    public SemanticKind(int kind) {
        this.kind = kind;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
    
    

    
    
    
}
