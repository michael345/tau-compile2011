package IC.lir.parameter;

public class LIRMemory extends LIROperand {

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
