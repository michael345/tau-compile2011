package IC.lir.parameter;

public class LIRLabel extends LIROperand {
    String name;
    
    public String toString() { 
        return name;
    }

    public LIRLabel(String name) {
        super();
        this.name = name;
    }
    
    
}
