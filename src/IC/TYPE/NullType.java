package IC.TYPE;

public class NullType extends Type{

    public NullType(int id) { 
        super();
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "null";
    }

}
