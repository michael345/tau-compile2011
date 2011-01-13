package IC.lir.instruction;

import IC.lir.parameter.LIROperand;

public class BinaryInstruction extends LIRInstruction {
    LIROperand op1;
    LIROperand op2;
    BinaryInstrucionEnum operator;
    
    public BinaryInstruction(LIROperand op1, LIROperand op2,
            BinaryInstrucionEnum operator) {
        super();
        this.op1 = op1;
        this.op2 = op2;
        this.operator = operator;
    }
    
    public String toString() { 
        return operator + " " + op1 + "," + op2;
    }
    
    
}
