package IC.lir.parameter;

public class LIRMemory extends LIRParameter {

    String name;

    public LIRMemory(String name) {
        super();
        this.name = name;
    }
    
    public String getName() { 
        return name;
    }
    
    public String toString() { 
        return getName();
    }
    
}
