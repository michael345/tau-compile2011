package IC.lir.instruction;

import IC.lir.parameter.LIRParameter;

public class ReturnInstruction extends LIRInstruction {
    LIRParameter param;
    
    
    
    public ReturnInstruction(LIRParameter param) {
        super();
        this.param = param;
    }



    public String toString() { 
        return "Return " + param;
    }
}
