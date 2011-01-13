package IC.lir.instruction;

import IC.lir.parameter.LIROperand;

public class UnaryInstruction extends LIRInstruction {
    LIROperand op;
    UnaryInstructionEnum operator;
    
    public UnaryInstruction(LIROperand op, UnaryInstructionEnum operator) {
        super();
        this.op = op;
        this.operator = operator;
    }
    
    public String toString() { 
        return operator + " " + op;
    }
    
    
}
