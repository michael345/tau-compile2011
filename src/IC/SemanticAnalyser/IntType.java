package IC.SemanticAnalyser;

public class IntType extends Type {

    public IntType(int id) { 
        super();
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "int";
    }
    
}
