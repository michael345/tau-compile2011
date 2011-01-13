package IC.lir.parameter;

public class LIRImmediate extends LIROperand {

    int value;
    
    public LIRImmediate(int value) {
        super();
        this.value = value;
    }
    
    public int getValue() { 
        return value;
    }
    
    public String toString() { 
        return "" + value;
    }
}
