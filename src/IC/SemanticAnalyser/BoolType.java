package IC.SemanticAnalyser;

public class BoolType extends Type {

    public BoolType(int id) {
        super();
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "boolean";
    }

}
