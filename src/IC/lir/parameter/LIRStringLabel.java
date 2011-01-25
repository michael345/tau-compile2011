package IC.lir.parameter;

public class LIRStringLabel extends LIROperand {
    private String name;

    public LIRStringLabel(String name) {
        super();
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
    
    
}
