package IC.TYPE;

public class Kind {
    int kind;
    
    public static final int VAR = 1;
    public static final int FIELD = 2;
    public static final int VIRTUALMETHOD = 3;
    public static final int CLASS = 4;
    public static final int STATICMETHOD = 5;
    public static final int FORMAL = 6;
    
    public Kind(int kind) {
        this.kind = kind;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
    
    public String toString() { 
        switch (kind) { 
            case VAR:
                return "Local variable";
            case FIELD:
                return "Field";      
            case VIRTUALMETHOD:
                return "Virtual method"; 
            case STATICMETHOD:
                return "Static method"; 
            case CLASS:
                return "Class";
            case FORMAL:
                return "Parameter";
            default:
                return "";
            }
    }
    

    
    
    
}
