package IC.lir.parameter;

public class LIRImmediate extends LIRParameter {

    int value;
    
    public LIRImmediate(int value) {
        super();
        this.value = value;
    }
    
    public int getValue() { 
        return value;
    }
}
